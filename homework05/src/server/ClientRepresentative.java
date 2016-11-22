package server;

import java.nio.channels.AsynchronousSocketChannel;
import java.security.KeyStore;
import java.util.concurrent.BlockingQueue;

import commonUIElements.Message;
import commonUIElements.SocketReadThread;

/**
 * Represents a client connected to the server.
 * 
 * @author Kyle
 *
 */
public class ClientRepresentative {
	// ID
	private String name;
	// Channel to communicate over
	private AsynchronousSocketChannel socketChannel;
	// Thread for reading
	private SocketReadThread reader;
	// Handles the external SocketReadThread
	private Thread thread;
	// Trust store with certificates
	private KeyStore trustStore;

	/**
	 * Creates a client representative. This can be either a client or server
	 * depending on the implementation. It is given a socket channel, primary
	 * task is loading valid recevied messages into the message queue for the
	 * GUI.
	 * 
	 * @param ch
	 *            socket channel messages arrive on
	 * @param name
	 *            name of the connecting device for tracking purposes
	 * @param messages
	 *            queue received messages are posted to
	 * @param ts
	 *            trust store with certificates to verify signatures
	 */
	public ClientRepresentative(AsynchronousSocketChannel ch, String name, BlockingQueue<Message> messages,
			KeyStore ts) {
		this.socketChannel = ch;
		this.name = name;
		this.trustStore = ts;
		reader = new SocketReadThread(ch, messages, name, trustStore);
		thread = new Thread(reader);
		thread.start();
	}

	/**
	 * Stops the client Representative reader thread from receiving messages.
	 */
	public void stop() {

		try {
			// kill channel
			this.socketChannel.close();
			// kill thread
			reader.stop();
			thread.join();
		} catch (Exception e) {
			System.err.println("Client THREAD ERROR! " + this.name);
		}
	}

	/**
	 * Gets the name of the connected client.
	 * 
	 * @return the client name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the socket channel being listened over.
	 * 
	 * @return SocketChannel
	 */
	public AsynchronousSocketChannel getSocketChannel() {
		return socketChannel;
	}

	/**
	 * Returns the reader thread listening over the socket channel for messages.
	 * 
	 * @return Reader thread
	 */
	public SocketReadThread getReader() {
		return reader;
	}
}
