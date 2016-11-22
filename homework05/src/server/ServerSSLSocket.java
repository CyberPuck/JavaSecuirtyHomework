package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import commonUIElements.Message;
import commonUIElements.MessageQueueReaderThread;
import commonUIElements.SignatureSystem;

/**
 * Handles creating the server socket, listening for SSL clients on the port
 * defined in the command line args.
 * 
 * @author Kyle
 *
 */
public class ServerSSLSocket {
	// port number the server is running
	private int port;
	// server socket
	private AsynchronousServerSocketChannel connector;
	// list of connected clients
	private ArrayList<ClientRepresentative> clients;
	// Holds incoming messages until the server GUI can handle them
	private BlockingQueue<Message> messages;
	// needed to respond to UI
	private ServerUILayoutController controller;
	// thread controller the message reader queue thread
	private Thread msgThread;
	// handles incoming messages, posts results to GUI
	private MessageQueueReaderThread msgReader;
	// key store containing client certificates
	private KeyStore keyStore;
	// server private key for signing messages
	private PrivateKey privateKey;

	/**
	 * Creates the ServerSSLSocket, this requires the SSLEngine to enable SSL
	 * comms.
	 * 
	 * @param port
	 *            number server is running off of
	 * @param messages
	 *            queue used to update the GUI when a client message is received
	 * @param controller
	 *            UI controller to kick back messages and information
	 */
	public ServerSSLSocket(int port, BlockingQueue<Message> messages, ServerUILayoutController controller) {
		this.port = port;
		this.messages = messages;
		this.controller = controller;
		this.clients = new ArrayList<>();
	}

	/**
	 * Starts up the server to communicate to clients.
	 * 
	 * @param keyStore
	 *            contains the client certificates
	 * @param key
	 *            used for signing server messages
	 * @throws Exception
	 *             thrown if an error occurs during connection setup
	 */
	public void startServer(KeyStore keyStore, PrivateKey key) throws Exception {
		connector = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));

		// setup the blocking queue reader thread
		msgReader = new MessageQueueReaderThread(messages, controller);
		msgThread = new Thread(msgReader);
		msgThread.start();

		// save off the keys
		this.keyStore = keyStore;
		this.privateKey = key;

		// accept incoming clients
		connector.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel ch, Void attachment) {
				// TODO: Log
				System.out.println("Client connected!");
				// handle I/O
				connector.accept(null, this);
				// create the client
				ClientRepresentative client = new ClientRepresentative(ch, "client" + clients.size(), messages,
						keyStore);
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

	/**
	 * Writes a message to all clients that CAN receive the message.
	 * 
	 * @param message
	 *            Message to send to clients
	 */
	public void writeMessage(Message message) {
		// TODO: Need to check clearance levels with the clients
		System.out.println("Writing messages");
		for (ClientRepresentative client : this.clients) {
			if (!client.getName().equals(message.senderName)) {
				// Create the message
				commonUIElements.MessageProtos.Message msg = commonUIElements.MessageProtos.Message.newBuilder()
						.setClearance(message.clearance).setMessage(message.message).setName(message.alias)
						.setSender(message.senderName)
						.setSignature(SignatureSystem.signMessage(message.message, privateKey)).build();
				System.out.println("Message size: " + msg.toByteArray().length);
				client.getSocketChannel().write(ByteBuffer.wrap(msg.toByteArray()));
			}
		}
	}

	/**
	 * Stops the server. No errors are returned as if a failure occurs we got
	 * some serious problems that only a reboot at the time of writing can fix.
	 */
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
