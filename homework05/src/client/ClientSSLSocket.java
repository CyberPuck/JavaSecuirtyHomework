package client;

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

	// Threads for async communication
	private class ReadThread implements Runnable {

		private AsynchronousSocketChannel socketChannel;
		private volatile boolean stopper;

		public ReadThread(AsynchronousSocketChannel ch) {
			this.socketChannel = ch;
		}

		public void stop() {
			stopper = true;
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
				try{
					size = f.get();
				} catch(Exception e) {
					System.err.println("Unable to get data length");
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

	public void run() throws Exception {
		System.out.println("Client connecting to: " + address + "@" + port);
		// create the client channel
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
		// socketChannel.configureBlocking(false);
		Future<Void> f = socketChannel.connect(new InetSocketAddress(address, port));
		f.get();
		System.out.println("Connected to server");
		// setup threads
		Thread reader = new Thread(new ReadThread(socketChannel));
		reader.start();
	}
}
