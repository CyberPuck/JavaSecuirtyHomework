package client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.SSLEngine;

import commonUIElements.Message;
import commonUIElements.MessageQueueReaderThread;
import commonUIElements.SignatureSystem;
import commonUIElements.SocketReadThread;

public class ClientSSLSocket {
	private SSLEngine engine;
	// port the server is running on
	private int port;
	// address of the server
	private String address;
	// channel to talk to the server over
	private AsynchronousSocketChannel socketChannel;
	// GUI controller to report messages up to
	private ClientUILayoutController controller;
	// handles the message reader thread
	private Thread msgThread;
	// Reads messages from the queue
	private MessageQueueReaderThread msgReader;
	// Holds messages from the socket
	private BlockingQueue<Message> messages;
	// Reads messages from the socket
	private SocketReadThread socketReader;
	// handles the thread that reads from the socket
	private Thread socketThread;
	// holds the certificates
	private KeyStore keyStore;
	// holds the clients private key
	private PrivateKey privateKey;

	/**
	 * Sets up the socket connection to the server. This includes the SSLEngine
	 * setup as well.
	 * 
	 * @param address
	 *            of the server
	 * @param port
	 *            of the server
	 * @param messages
	 *            queue for received messages
	 * @param clientController
	 *            GUI controller
	 */
	public ClientSSLSocket(String address, int port, BlockingQueue<Message> messages,
			ClientUILayoutController clientController) {
		this.address = address;
		this.port = port;
		this.messages = messages;
		this.controller = clientController;
	}

	/**
	 * Handles connecting the client to the server.
	 * 
	 * @param ks
	 *            key store with certificates
	 * @param key
	 *            private key of the clients
	 * @throws Exception
	 *             throws exception if connection error occurs
	 */
	public void startClient(KeyStore ks, PrivateKey key) throws Exception {
		System.out.println("Client connecting to: " + address + "@" + port);
		// create the client channel
		this.socketChannel = AsynchronousSocketChannel.open();
		// store KS and private key
		this.keyStore = ks;
		this.privateKey = key;

		// socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(address, port), this.socketChannel,
				new CompletionHandler<Void, AsynchronousSocketChannel>() {

					@Override
					public void completed(Void result, AsynchronousSocketChannel ch) {
						System.out.println("Connected to server");
						// setup threads
						// setup the socket reader
						socketReader = new SocketReadThread(socketChannel, messages, "Server", keyStore);
						socketThread = new Thread(socketReader);
						socketThread.start();
						// setup the message reader
						msgReader = new MessageQueueReaderThread(messages, controller);
						msgThread = new Thread(msgReader);
						msgThread.start();
					}

					@Override
					public void failed(Throwable exc, AsynchronousSocketChannel ch) {
						System.err.println("Failed to connect to server");
					}
				});
	}

	/**
	 * Writes a message to the server.
	 * 
	 * @param message
	 *            to send to server
	 */
	public void writeMessage(Message message) {
		// Create the message
		commonUIElements.MessageProtos.Message msg = commonUIElements.MessageProtos.Message.newBuilder()
				.setClearance(message.clearance).setMessage(message.message).setName(message.alias)
				.setSender(message.senderName).setSignature(SignatureSystem.signMessage(message.message, privateKey))
				.build();
		socketChannel.write(ByteBuffer.wrap(msg.toByteArray()));
	}

	/**
	 * Stops the client socket.
	 */
	public void stop() {
		try {
			// kill the socket
			this.socketChannel.close();
			// kill the socket reader
			this.socketReader.stop();
			this.socketThread.interrupt();
			this.socketThread.join();
			this.msgReader.stop();
			this.msgThread.interrupt();
			this.msgThread.join();
		} catch (Exception e) {
			System.err.println("Failed to stop client reader thread: " + e.getMessage());
		}
	}
}
