package commonUIElements;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

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
			Future<Integer> f = socketChannel.read(buf);
			while (!f.isDone() && !stopper) {
				// wait
			}
			int size = 0;
			try {
				size = f.get();
			} catch (Exception e) {
				System.err.println("Unable to get data length");
			}
			String data = new String(buf.array(), 0, size);
			System.out.println("RXed: " + data);
			messages.add(data);
		}
		System.out.println("Exiting thread...");
	}
}
