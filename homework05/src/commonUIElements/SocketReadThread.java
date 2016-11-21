package commonUIElements;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.InvalidProtocolBufferException;

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
	private BlockingQueue<Message> messages;
	private String name;

	public SocketReadThread(AsynchronousSocketChannel ch, BlockingQueue<Message> messages, String name) {
		this.socketChannel = ch;
		this.messages = messages;
		this.name = name;
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
				// convert to proto
				commonUIElements.MessageProtos.Message msg = commonUIElements.MessageProtos.Message.parseFrom(buf.array());
				System.out.println("RXed: " + msg.toString());
				// add data to the message queue
				messages.put(new Message(msg.getSender(), msg.getMessage(), msg.getSignature(), msg.getClearance()));
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
			} catch(InvalidProtocolBufferException e) {
				System.err.println("Invalid protobuf received");
			}
		}
		try {
			socketChannel.close();
		} catch (IOException e) {
			System.err.println("Failed to close socket while exiting");
		}
		System.out.println("Exiting thread...");
	}
}
