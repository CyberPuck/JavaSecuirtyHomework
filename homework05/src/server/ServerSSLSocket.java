package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

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
	private static Logger logger = Logger.getLogger("ServerLogger");
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
	// trust store containing client certificates
	private KeyStore trustStore;
	// server private key for signing messages
	private PrivateKey privateKey;
	// Management objects for the key and trust stores
	private KeyManagerFactory kmf;
	private TrustManagerFactory tmf;
	// SSL engine
	private SSLEngine serverEngine;
	// SSL Context, holds the key and trust managers
	private SSLContext sslContext;
	// list of client aliases with associated clearance level
	private Properties clientSettings;

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
	 * @param clientSettings
	 *            Properties with alias to clearance associations
	 */
	public ServerSSLSocket(int port, BlockingQueue<Message> messages, ServerUILayoutController controller,
			Properties clientSettings) {
		this.port = port;
		this.messages = messages;
		this.controller = controller;
		this.clients = new ArrayList<>();
		this.clientSettings = clientSettings;

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
	public void startServer(KeyStore trustStore, PrivateKey key, KeyManagerFactory kmf, TrustManagerFactory tmf)
			throws Exception {
		connector = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));

		// setup the blocking queue reader thread
		msgReader = new MessageQueueReaderThread(messages, controller);
		msgThread = new Thread(msgReader);
		msgThread.start();

		// save off the keys
		this.trustStore = trustStore;
		this.privateKey = key;
		// store the management factories for SSL
		this.kmf = kmf;
		this.tmf = tmf;

		// setup the SSL context
		// NOTE: Force TLSv1.2 to avoid issues like POODLE, BEAST, etc.
		sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
		// setup the SSL engine for incoming connections
		serverEngine = sslContext.createSSLEngine();

		// client always needs to authenticate
		serverEngine.setNeedClientAuth(true);
		serverEngine.setUseClientMode(false);
		// accept incoming clients
		connector.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel ch, Void attachment) {
				logger.info("Client connected");
				controller.displayMessage("Client Connected");
				// handle I/O
				connector.accept(null, this);
				// create the client
				ClientRepresentative client = new ClientRepresentative(ch, "client" + (clients.size()+1), messages,
						trustStore, serverEngine);
				clients.add(client);
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				if (exc != null) {
					// only log if a failure was thrown
					logger.severe("Failed to connect: " + exc.getMessage());
					controller.socketError("Failed to connect: " + exc.getMessage());
				} else {
					logger.fine("Null error thrown");
				}
			}
		});
		logger.info("Ready for clients on port: " + port);
	}

	/**
	 * Writes a message to all clients that CAN receive the message.
	 * 
	 * @param message
	 *            Message to send to clients
	 */
	public void writeMessage(Message message) {
		// if the message is an error don't forward to clients
		if(message.error) {
			return;
		}
		// if the message is a kill order find the client and kill it
		if(message.kill) {
			for(ClientRepresentative rep : this.clients) {
				if(rep.getName().equals(message.alias)) {
					// stop the client
					rep.stop();
					// remove from list
					this.clients.remove(rep);
				}
			}
		}
		// check the clearance level of the client
		int i = Integer.parseInt(clientSettings.getProperty(message.senderName));
		System.out.println(i);
		if (Integer.parseInt(clientSettings.getProperty(message.senderName)) < message.clearance) {
			controller.displayMessage(
					"Error: " + message.senderName + " is not cleared for " + message.clearance + " level messages.");
			logger.severe(message.senderName + " is not cleared for " + message.clearance + " level messages.");
		} else {
			logger.info("Writing message");
			for (ClientRepresentative client : this.clients) {
				// do not transmit to client, and make sure each client has the
				// clearance to receive the message
				if (!client.getName().equals(message.senderName)
						&& Integer.parseInt(clientSettings.getProperty(client.getName())) > message.clearance) {
					logger.fine("Sending message to: " + client.getName());
					// Create the message, include signature from the server
					commonUIElements.MessageProtos.Message msg = commonUIElements.MessageProtos.Message.newBuilder()
							.setClearance(message.clearance).setMessage(message.message).setName(message.alias)
							.setSender(message.senderName)
							.setSignature(SignatureSystem.signMessage(message.message, privateKey)).build();
					client.getSocketChannel().write(ByteBuffer.wrap(msg.toByteArray()));
				}
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
			logger.severe("Failed to kill server: " + e.getMessage());
			controller.socketError("Failed to kill server: " + e.getMessage());
		}
		logger.fine("connector closed");
		msgReader.stop();
		try {
			msgThread.interrupt();
			logger.fine("Waiting on msg reader to stop");
			msgThread.join();
		} catch (InterruptedException e1) {
			logger.severe("Failed to interrupt message reader thread");
		}
		for (ClientRepresentative rep : this.clients) {
			rep.stop();
		}
		// clear out clients
		this.clients.clear();
		logger.info("ServerSSLSocket has been stopped");
	}
}
