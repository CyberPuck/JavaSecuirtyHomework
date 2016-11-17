package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Handles creating the server socket, listening for SSL clients on the port
 * defined in the command line args.
 * 
 * @author Kyle
 *
 */
public class ServerSSLSocket {
	private class QueueReaderThread implements Runnable {

		private volatile boolean stop = false;
		private BlockingQueue<String> messages;
		ServerUILayoutController controller;
		
		public QueueReaderThread(BlockingQueue<String> messages, ServerUILayoutController controller) {
			this.messages = messages;
			this.controller = controller;
		}
		
		@Override
		public void run() {
			while(!stop) {
				String msg;
				while((msg = messages.poll()) != null) {
					controller.socketMessage(msg);
				}
			}
		}
		
		public void terminate() {
			stop = true;
		}
	}
	
	private int port;
	private AsynchronousServerSocketChannel connector;
	private ArrayList<ClientRepresentative> clients;
	private BlockingQueue<String> messages;
	// needed to respond to UI
	private ServerUILayoutController controller;
	private Thread thread;
	private QueueReaderThread reader;
	
	public ServerSSLSocket(int port, BlockingQueue<String> messages, ServerUILayoutController controller) {
		this.port = port;
		this.messages = messages;
		this.controller = controller;
		this.clients = new ArrayList<>();
	}

	public void startServer() throws Exception {
		connector = AsynchronousServerSocketChannel.open()
				.bind(new InetSocketAddress(port));
		
		// setup the blocking queue reader thread
		reader = new QueueReaderThread(messages, controller);
		thread = new Thread(reader);
		thread.start();

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
				System.err.println("Failed to connect: " + exc.getMessage());
				controller.socketError("Failed to connect: " + exc.getMessage());
			}
		});
		System.out.println("Ready for clients");
	}
	
	public void writeMessage(String message) {
		// TODO: Need to check clearance levels with the clients
		for(ClientRepresentative client : this.clients) {
			client.getSocketChannel().write(ByteBuffer.wrap(message.getBytes()));
		}
	}
	
	public void stop() {
		try {
			connector.close();
		} catch (IOException e) {
			System.err.println("Failed to kill server :( " + e.getMessage());
			controller.socketError("Failed to connect: " + e.getMessage());
		}
		
		reader.terminate();
		try {
			thread.join();
		} catch (InterruptedException e1) {
			System.err.println("FUCKIN' THREADS!!!!!");
		}
		for(ClientRepresentative rep : this.clients) {
			rep.stop();
		}
	}
}
