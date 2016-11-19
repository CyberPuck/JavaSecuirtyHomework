package commonUIElements;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A thread object given a channel allowing to listen for incoming messages from
 * a socket.
 * 
 * @author Kyle
 *
 */
public class SocketReadThread implements Runnable {
	private AsynchronousSocketChannel socketChannel;
	private volatile boolean stopper;
	private BlockingQueue<String> messages;

	public SocketReadThread(AsynchronousSocketChannel ch, BlockingQueue<String> messages) {
		this.socketChannel = ch;
		this.messages = messages;
	}

	public void stop() {
		this.stopper = true;
	}

	@Override
	public void run() {
		System.out.println("Waiting for input");
		// Handle reading data until we exit
		ByteBuffer buf = ByteBuffer.allocate(1024);
		while (!stopper && socketChannel.isOpen()) {
			System.out.println("Reading the socket");
			try {
				// No input from the user in 60 seconds results in kicking them out
				int bytesRead = socketChannel.read(buf).get(60, TimeUnit.SECONDS);
				System.out.println("Got data of size: " + bytesRead);

				String data = new String(buf.array(), 0, bytesRead);
				System.out.println("RXed: " + data);
				// add data to the message queue
				messages.put(data);
				// clear the buffer for the next message
				buf.clear();
			} catch (InterruptedException e) {
				System.err.println("Failed to add message to the queue: " + e.getMessage());
				break;
			} catch(TimeoutException e) {
				System.err.println("Timeout hit, waiting again");
			} catch(ExecutionException e) {
				System.err.println("Execution error, breaking out of loop");
				break;
			}
		}
		System.out.println("Exiting thread...");
	}
}
