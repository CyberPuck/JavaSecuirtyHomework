package client;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

public class ClientSSLSocket {
	// timeout for client socket to finish the connection
	private static long CLIENT_TIMEOUT_MS = 5000;

	private SSLEngine engine;
	private int port;
	private String address;

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
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(address, port));

		// add finish connection timeout
		long startTime = System.currentTimeMillis();
		while (!socketChannel.finishConnect()) {
			// throw error and return if client can't finish the connection
			if ((System.currentTimeMillis() - startTime) > CLIENT_TIMEOUT_MS) {
				System.err.println("Client failed to finish connection to " + address);
				socketChannel.close();
				return;
			}
		}

		// create the buffers for the SSL engine
		SSLSession session = engine.getSession();
	}
}
