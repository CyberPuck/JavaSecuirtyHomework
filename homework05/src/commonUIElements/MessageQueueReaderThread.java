package commonUIElements;

import java.util.concurrent.BlockingQueue;

public class MessageQueueReaderThread implements Runnable{
	private volatile boolean stop = false;
	private BlockingQueue<Message> messages;
	SocketResponseInterface controller;

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

	public void stop() {
		stop = true;
	}
}
