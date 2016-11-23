package commonUIElements;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.Status;

/**
 * Helps SSL engines handshake and other tasks need to get SSL working so data
 * can be transported.
 * 
 * @author Kyle
 */
public class SSLHelper {

	/**
	 * Does the TLS handshake to establish a connection.
	 * 
	 * @param socketChannel
	 *            channel to send data
	 * @param engine
	 *            SSLEngine used to check on status of handshake
	 * @param outgoingNetData
	 *            ByteBuffer for outgoing data
	 * @param incomingNetData
	 *            ByteBuffer for incoming data
	 */
//	public void doHandshake(SocketChannel socketChannel, SSLEngine engine, ByteBuffer outgoingNetData,
//			ByteBuffer incomingNetData) {
//		int bufferSize = engine.getSession().getApplicationBufferSize();
//		ByteBuffer outgoingAppData = ByteBuffer.allocate(bufferSize);
//		ByteBuffer incomingAppData = ByteBuffer.allocate(bufferSize);
//		// start the handshake
//		engine.beginHandshake();
//		SSLEngineResult.HandshakeStatus results = engine.getHandshakeStatus();
//		while (results != SSLEngineResult.HandshakeStatus.FINISHED
//				&& results != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
//			switch (results) {
//			case NEED_UNWRAP:
//				if (socketChannel.read(incomingNetData) < 0) {
//					System.err.println("Error reading data");
//				}
//				incomingNetData.flip();
//				SSLEngineResult res = engine.unwrap(incomingNetData, incomingAppData);
//				incomingNetData.compact();
//				results = res.getHandshakeStatus();
//				if (res.getStatus() == Status.OK) {
//					break;
//				} else {
//					System.err.println("Issue has occured: " + res.getStatus().toString());
//				}
//				break;
//			case NEED_WRAP:
//				// empty network buffer
//				outgoingNetData.clear();
//				// send out going data
//				res = engine.wrap(outgoingAppData, outgoingNetData);
//				results = res.getHandshakeStatus();
//
//			default:
//				System.err.println("SSL Engine handshake returned value not catched: " + results.toString());
//			}
//		}
//	}
}
