package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

import javax.net.ssl.SSLEngine;

public class ClientSSLSocket {
	// timeout for client socket to finish the connection
	private static long TIMEOUT_MS = 5000;

	private SSLEngine engine;
	private int port;
	private String address;
	private AsynchronousSocketChannel socketChannel;
	private Thread thread;
	private ReadThread reader;

	// Threads for async communication
	private class ReadThread implements Runnable {

		private AsynchronousSocketChannel socketChannel;
		private volatile boolean stopper;

		public ReadThread(AsynchronousSocketChannel ch) {
			this.socketChannel = ch;
		}

		public void stop() {
			stopper = true;
			try {
				socketChannel.close();
			} catch (IOException e) {
				System.err.println("Failed to stop the client read channel");
			}
		}

		@Override
		public void run() {
			// Handle reading data until we exit
			ByteBuffer buf = ByteBuffer.allocate(1024);
			while (!stopper && socketChannel.isOpen()) {
				Future<Integer> f = socketChannel.read(buf);
				while (!f.isDone() && !stopper) {
					// wait
				}
				int size = 0;
				try {
					size = f.get();
				} catch (Exception e) {
					System.err.println("Unable to get data length: " + e.getMessage() + " :: " + socketChannel.isOpen());
					break;
				}
				String data = new String(buf.array(), 0, size);
				System.out.println("RXed: " + data);
				if (data.equals("hello")) {
					socketChannel.write(ByteBuffer.wrap("HI!".getBytes()));
				}
			}
			System.out.println("Exiting thread...");
		}
	}

	/**
	 * Setup the SSL engine, involves opening trust and key stores, and defining
	 * the SSL algorithms.
	 * 
	 * @param address
	 * @param port
	 */
	public ClientSSLSocket(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public void startClient() throws Exception {
		System.out.println("Client connecting to: " + address + "@" + port);
		// create the client channel
		socketChannel = AsynchronousSocketChannel.open();
		// socketChannel.configureBlocking(false);
		Future<Void> f = socketChannel.connect(new InetSocketAddress(address, port));
		f.get();
		System.out.println("Connected to server");
		// setup threads
		reader = new ReadThread(socketChannel);
		thread = new Thread(reader);
		thread.start();
	}

	public void writeMessage(String message) {
		socketChannel.write(ByteBuffer.wrap(message.getBytes()));
	}
	
	public void stop() {
		try {
			reader.stop();
			thread.join();
		} catch (Exception e) {
			System.err.println("Failed to stop client reader thread: " + e.getMessage());
		}
	}
}
