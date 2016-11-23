package client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import commonUIElements.Message;
import commonUIElements.MessageQueueReaderThread;
import commonUIElements.SignatureSystem;
import commonUIElements.SocketReadThread;

/**
 * Handles the socket connection to the server, for I/O.
 * 
 * @author Kyle
 */
public class ClientSSLSocket {
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
	// Management objects for the key and trust stores
	private KeyManagerFactory kmf;
	private TrustManagerFactory tmf;
	// SSL engine
	private SSLEngine clientEngine;
	// SSL Context, holds the key and trust managers
	private SSLContext sslContext;

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
	 * @param kmf
	 *            holds the client public/private keys
	 * @param tmf
	 *            holds the server cert
	 * @throws Exception
	 *             throws exception if connection error occurs
	 */
	public void startClient(KeyStore ks, PrivateKey key, KeyManagerFactory kmf, TrustManagerFactory tmf)
			throws Exception {
		System.out.println("Client connecting to: " + address + "@" + port);
		// create the client channel
		this.socketChannel = AsynchronousSocketChannel.open();
		// store KS and private key
		this.keyStore = ks;
		this.privateKey = key;
		// setup the SSL context and SSL engine
		// this.sslContext = SSLContext.getInstance("TLSv1.2");
		// this.sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
		// SecureRandom.getInstance("SHA1PRNG"));
		// // setup the SSL engine to connect to the client
		// this.clientEngine = sslContext.createSSLEngine(this.address,
		// this.port);
		// // server always needs to authenticate
		// this.clientEngine.setNeedClientAuth(true);
		// this.clientEngine.setUseClientMode(true);

		socketChannel.connect(new InetSocketAddress(address, port), this.socketChannel,
				new CompletionHandler<Void, AsynchronousSocketChannel>() {

					@Override
					public void completed(Void result, AsynchronousSocketChannel ch) {
						System.out.println("Connected to server");
						// setup threads
						// setup the socket reader
						socketReader = new SocketReadThread(socketChannel, messages, "Server", keyStore, clientEngine);
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
		// try {
		// ByteBuffer dst = ByteBuffer.allocate(2048);
		// this.clientEngine.wrap(ByteBuffer.wrap(msg.toByteArray()), dst);
		socketChannel.write(ByteBuffer.wrap(msg.toByteArray()));
		// } catch (SSLException e) {
		// System.err.println("Error writing message over socket: " +
		// e.getMessage());
		// }
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
