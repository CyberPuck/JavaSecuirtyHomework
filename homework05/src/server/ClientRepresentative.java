package server;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;

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
	private Thread thread;
	
	public ClientRepresentative(AsynchronousSocketChannel ch, String name, BlockingQueue<String> messages) {
		this.name = name;
		reader = new SocketReadThread(ch, messages);
		thread = new Thread(reader);
		thread.start();
	}
	
	public void stop() {
		reader.stop();
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println("Client THREAD!!!!!!");
		}
	}

	public String getName() {
		return name;
	}

	public AsynchronousSocketChannel getSocketChannel() {
		return socketChannel;
	}

	public SocketReadThread getReader() {
		return reader;
	}
}
