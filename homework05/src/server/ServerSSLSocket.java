package server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
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
	private int port;

	private class ReadThread implements Runnable {
		private AsynchronousSocketChannel socketChannel;
		private volatile boolean stopper;

		public ReadThread(AsynchronousSocketChannel ch) {
			this.socketChannel = ch;
		}

		public void stop() {
			this.stopper = true;
		}

		@Override
		public void run() {
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
				} catch(Exception e) {
					System.err.println("Unable to get data length");
				}
				String data = new String(buf.array(), 0, size);
				System.out.println("RXed: " + data);
				if (data.equals("hello")) {
					socketChannel.write(ByteBuffer.wrap("HI!".getBytes()));
				}
			}
			System.out.println("Exiting thread...");
		}

	}

	public ServerSSLSocket(int port) {
		this.port = port;
	}

	public void startServer() throws Exception {
		final AsynchronousServerSocketChannel connector = AsynchronousServerSocketChannel.open()
				.bind(new InetSocketAddress(port));

		// accept incoming clients
		connector.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel ch, Void attachment) {
				System.out.println("Client connected!");
				// handle I/O
				connector.accept(null, this);
				// start up the reader thread
				Thread reader = new Thread(new ReadThread(ch));
				reader.start();
				// greet the client
				System.out.println("Sending Message");
				Future<Integer> f = ch.write(ByteBuffer.wrap("hello".getBytes()));
				try {
					System.out.println("Server TX: " + f.get());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				System.err.println("Failed to connect: " + exc.getMessage());

			}

		});
	}
}
