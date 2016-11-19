package client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.SSLEngine;

import commonUIElements.MessageQueueReaderThread;
import commonUIElements.SocketReadThread;

public class ClientSSLSocket {
	private SSLEngine engine;
	private int port;
	private String address;
	private AsynchronousSocketChannel socketChannel;
	private ClientUILayoutController controller;
	private Thread msgThread;
	// Reads messages from the queue
	private MessageQueueReaderThread msgReader;
	private BlockingQueue<String> messages;
	// Reads messages from the socket
	private SocketReadThread socketReader;
	private Thread socketThread;

	/**
	 * Setup the SSL engine, involves opening trust and key stores, and defining
	 * the SSL algorithms.
	 * 
	 * @param address
	 * @param port
	 */
	public ClientSSLSocket(String address, int port, BlockingQueue<String> messages, ClientUILayoutController clientController) {
		this.address = address;
		this.port = port;
		this.messages = messages;
		this.controller = clientController;
	}

	public void startClient() throws Exception {
		System.out.println("Client connecting to: " + address + "@" + port);
		// create the client channel
		this.socketChannel = AsynchronousSocketChannel.open();
		// socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(address, port), this.socketChannel, new CompletionHandler<Void, AsynchronousSocketChannel>() {

			@Override
			public void completed(Void result, AsynchronousSocketChannel ch) {
				System.out.println("Connected to server");
				// setup threads
				// setup the socket reader
				socketReader = new SocketReadThread(socketChannel, messages);
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

	public void writeMessage(String message) {
		socketChannel.write(ByteBuffer.wrap(message.getBytes()));
	}
	
	public void stop() {
		try {
			// kill the socket
			this.socketChannel.close();
			// kill the socket reader
			this.socketReader.stop();
			this.socketThread.join();
			this.msgReader.stop();
			this.msgThread.interrupt();
			this.msgThread.join();
		} catch (Exception e) {
			System.err.println("Failed to stop client reader thread: " + e.getMessage());
		}
	}
}
