package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import commonUIElements.Message;
import commonUIElements.MessageQueueReaderThread;

/**
 * Handles creating the server socket, listening for SSL clients on the port
 * defined in the command line args.
 * 
 * @author Kyle
 *
 */
public class ServerSSLSocket {
	private int port;
	private AsynchronousServerSocketChannel connector;
	private ArrayList<ClientRepresentative> clients;
	private BlockingQueue<Message> messages;
	// needed to respond to UI
	private ServerUILayoutController controller;
	private Thread msgThread;
	private MessageQueueReaderThread msgReader;
	

	public ServerSSLSocket(int port, BlockingQueue<Message> messages, ServerUILayoutController controller) {
		this.port = port;
		this.messages = messages;
		this.controller = controller;
		this.clients = new ArrayList<>();
	}

	public void startServer() throws Exception {
		connector = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));

		// setup the blocking queue reader thread
		msgReader = new MessageQueueReaderThread(messages, controller);
		msgThread = new Thread(msgReader);
		msgThread.start();

		// accept incoming clients
		connector.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel ch, Void attachment) {
				// TODO: Log
				System.out.println("Client connected!");
				// handle I/O
				connector.accept(null, this);
				// create the client
				ClientRepresentative client = new ClientRepresentative(ch, "client" + clients.size(), messages);
				clients.add(client);
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				if (exc != null) {
					// only log if a failure was thrown
					System.err.println("Failed to connect: " + exc.getMessage());
					controller.socketError("Failed to connect: " + exc.getMessage());
				} else {
					System.out.println("Random failed event?");
				}
			}
		});
		System.out.println("Ready for clients");
	}

	public void writeMessage(Message message) {
		// TODO: Need to check clearance levels with the clients
		System.out.println("Writing messages");
		for (ClientRepresentative client : this.clients) {
			if (!client.getName().equals(message.senderName)) {
				String msg = message.clearance + message.message;
				client.getSocketChannel().write(ByteBuffer.wrap(msg.getBytes()));
			}
		}
	}

	public void stop() {
		try {
			connector.close();
		} catch (IOException e) {
			System.err.println("Failed to kill server :( " + e.getMessage());
			controller.socketError("Failed to connect: " + e.getMessage());
		}
		System.out.println("connector closed");
		msgReader.stop();
		try {
			msgThread.interrupt();
			System.out.println("Waiting on reader to stop");
			msgThread.join();
		} catch (InterruptedException e1) {
			System.err.println("FUCKIN' THREADS!!!!!");
		}
		for (ClientRepresentative rep : this.clients) {
			rep.stop();
		}
		System.out.println("Waiting on clients");
		// clear out clients
		this.clients.clear();
	}
}
