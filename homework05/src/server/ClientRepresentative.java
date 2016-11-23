package server;

import java.nio.channels.AsynchronousSocketChannel;
import java.security.KeyStore;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.SSLEngine;

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
	// Engine used in comms with the client
	private SSLEngine engine;

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
			KeyStore ts, SSLEngine engine) {
		this.socketChannel = ch;
		this.name = name;
		this.trustStore = ts;
		reader = new SocketReadThread(ch, messages, name, trustStore, engine);
		thread = new Thread(reader);
		thread.start();
		this.engine = engine;
	}

	/**
	 * Stops the client Representative reader thread from receiving messages.
	 */
	public void stop() {

		try {
			// set output to false
			this.engine.closeOutbound();
			// kill channel
			this.socketChannel.close();
			// kill thread
			reader.stop();
			thread.join();
			this.engine.closeInbound();
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
		this.name = reader.getName();
		return reader.getName();
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
