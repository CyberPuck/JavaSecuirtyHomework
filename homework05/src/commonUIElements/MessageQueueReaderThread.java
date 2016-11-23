package commonUIElements;

import java.util.concurrent.BlockingQueue;

/**
 * Handles reading messages from the message queue and passes them to the
 * controller to display them.
 * 
 * @author Kyle
 */
public class MessageQueueReaderThread implements Runnable {
	private volatile boolean stop = false;
	private BlockingQueue<Message> messages;
	SocketResponseInterface controller;

	/**
	 * Takes in the message queue and controller interface.
	 * 
	 * @param messages
	 *            queue for received messages.
	 * @param controller
	 *            to post results of messages received.
	 */
	public MessageQueueReaderThread(BlockingQueue<Message> messages, SocketResponseInterface controller) {
		this.messages = messages;
		this.controller = controller;
	}

	@Override
	public void run() {
		while (!stop) {
			Message msg;
			try {
				System.out.println("Waiting on a message");
				msg = messages.take();
				System.out.println("Message RXed!!");
				if (msg != null) {
					controller.socketMessage(msg);
				}
			} catch (InterruptedException e) {
				System.err.println("Queue thread Error: " + e.getMessage());
			}
		}
		System.out.println("Exiting...");
	}

	/**
	 * Stops the message queue reader.
	 */
	public void stop() {
		stop = true;
	}
}
