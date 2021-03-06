package commonUIElements;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLEngine;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * A thread object given a channel allowing to listen for incoming messages from
 * a socket.
 * 
 * @author Kyle
 *
 */
public class SocketReadThread implements Runnable {
	// Channel to listen for messages
	private AsynchronousSocketChannel socketChannel;
	// flag for stopping the thread
	private volatile boolean stopper;
	// queue for incoming messages to be added to
	private BlockingQueue<Message> messages;
	// name of the client being listened to
	private String name;
	// holds the certificates of the connected client(s)
	private KeyStore trustStore;
	// SSL Engine for encrypted communication
	private SSLEngine engine;

	/**
	 * Creates the reader thread, listens over the socket channel for messages.
	 * Messages are verified, signatures are verified, then message is posted to
	 * queue for processing from the GUI.
	 * 
	 * @param ch
	 *            socket channel
	 * @param messages
	 *            queue to post messages to
	 * @param name
	 *            client name
	 * @param engine
	 *            SSL engine for encrypted communications
	 * @param trustStore
	 *            contains certificates to verify the message signature
	 */
	public SocketReadThread(AsynchronousSocketChannel ch, BlockingQueue<Message> messages, String name,
			KeyStore trustStore, SSLEngine engine) {
		this.socketChannel = ch;
		this.messages = messages;
		this.name = name;
		this.trustStore = trustStore;
		this.engine = engine;
	}

	/**
	 * Stops the running thread.
	 */
	public void stop() {
		this.stopper = true;
	}

	/**
	 * Read socket channel for incoming messages and post them to the message
	 * queue.
	 */
	@Override
	public void run() {
		System.out.println("Waiting for input");
		// Handle reading data until we exit
		ByteBuffer buf = ByteBuffer.allocate(2048);
		while (!stopper && socketChannel.isOpen()) {
			System.out.println("Reading the socket");
			try {
				// No input from the user in 60 seconds results in kicking them
				// out
				int bytesRead = socketChannel.read(buf).get(60, TimeUnit.SECONDS);
				// ByteBuffer dst = ByteBuffer.allocate(bytesRead);
				// engine.unwrap(buf, dst);
				System.out.println("Got data of size: " + bytesRead);
				// convert to proto
				commonUIElements.MessageProtos.Message msg = commonUIElements.MessageProtos.Message
						.parseFrom(Arrays.copyOf(buf.array(), bytesRead));
				System.out.println("RXed: " + msg.toString());
				if (!msg.getName().equals(name)) {
					name = msg.getName();
				}
				// add data to the message queue
				if (SignatureSystem.verifySignature(msg.getSignature(), msg.getMessage(), msg.getName(), trustStore)) {
					messages.put(
							new Message(msg.getSender(), msg.getMessage(), msg.getSignature(), msg.getClearance()));
				} else {
					Message error = new Message(msg.getSender(), "Error: signture is invalid", msg.getSignature(),
							msg.getClearance());
					error.error = true;
					messages.put(error);
				}
				// clear the buffer for the next message
				buf.clear();
			} catch (InterruptedException e) {
				System.err.println("Failed to add message to the queue: " + e.getMessage());
				Message error = new Message(this.name, "Disconnected", "", 1);
				error.error = true;
				try {
					messages.put(error);
				} catch (InterruptedException e1) {
					System.err.println("Thread has an error, can't inform server of this transgression");
				}
				break;
			} catch (TimeoutException e) {
				System.err.println("Timeout hit, waiting again");
				Message error = new Message(this.name, "Client: " + this.name + " has timed out", "", 1);
				error.kill = true;
				try {
					messages.put(error);
				} catch (InterruptedException e1) {
					System.err.println("Thread has an error, can't inform server of this transgression");
				}
				break;
			} catch (ExecutionException e) {
				System.err.println("Execution error, breaking out of loop");
				break;
			} catch (InvalidProtocolBufferException e) {
				System.err.println("Invalid protobuf received: " + e.getMessage());
				// } catch(SSLException e) {
				// System.err.println("SSL Failed: " + e.getMessage());
			}
		}
		try {
			socketChannel.close();
		} catch (IOException e) {
			System.err.println("Failed to close socket while exiting");
		}
		System.out.println("Exiting thread...");
	}

	/**
	 * Gets the name of the client. This might change at first.
	 * 
	 * @return name of the client
	 */
	public String getName() {
		return this.name;
	}
}
