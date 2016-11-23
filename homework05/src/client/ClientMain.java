package client;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import commonUIElements.CommandLineArgs;
import commonUIElements.KeystoreAccessController;
import commonUIElements.KeystoreAccessInterface;
import javafx.application.Application;
import javafx.stage.Stage;
import keyStore.KeyStoreAccessor;
import server.ServerMain;

/**
 * Entry point for the client that communicates with the server.
 * 
 * @author Kyle
 */
public class ClientMain extends Application implements KeystoreAccessInterface {
	// The title of the UI application
	private String title = "Secure Chat Client";
	// command line args that have been parsed
	private static CommandLineArgs parser = new CommandLineArgs("Client");
	// handle to the primary stage of the application, used so if the keystore
	// is unlocked the client UI will boot up
	private Stage primaryStage;
	// initial log in pop up controller
	private KeystoreAccessController popupController;
	// controller for the primary UI
	private ClientUILayoutController clientController;
	// logger
	private static Logger logger = Logger.getLogger(ServerMain.class.getName());

	/**
	 * Entry point for the application, takes in command line args and spins up
	 * the client keystore login pop up. Upon success the primary client UI is
	 * loaded.
	 * 
	 * @param args
	 *            paramters for starting the client.
	 */
	public static void main(String[] args) {
		// parse the command line args

		if (parser.parseCommandLineArgs(args)) {
			// Launch the UI
			launch(args);
		}
	}

	/**
	 * Starts up the application from a JavaFX perspective, this will boot: the
	 * keystore login pop up and if successful will boot up the client UI.
	 */
	@Override
	public void start(Stage ps) throws Exception {
		this.primaryStage = ps;
		// set the title
		primaryStage.setTitle(title);
		// boot up the pop up
		popupController = new KeystoreAccessController(this, "Client key store access");
		popupController.getKeystoreAccessStage().show();
	}

	@Override
	public void onLoginRequest(final char[] keyStorePassword, final char[] trustStorePassword) {
		try {
		// Try to open the keystore
		KeyStore keyStore = KeyStoreAccessor.getKeyStore(keyStorePassword, parser.getKeystoreLocation());
		// setup the key management object
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, keyStorePassword);
		// Try to open the trust store
		KeyStore trustStore = KeyStoreAccessor.getKeyStore(trustStorePassword, parser.getTrustStoreLocation());
		// setup trust store management object
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(trustStore);

		if (keyStore == null) {
			logger.severe("Keystore failed to open at:  \"" + parser.getKeystoreLocation()
					+ "\" either the keystore does not exist or the password was incorrect");
			System.exit(1);
		}
		if (trustStore == null) {
			logger.severe("Trust store failed to open at:  \"" + parser.getTrustStoreLocation()
					+ "\" either the trust store does not exist or the password was incorrect");
			System.exit(1);
		}
		// remove the popup UI
		this.popupController.getKeystoreAccessStage().close();
		// setup the client UI
		this.clientController = new ClientUILayoutController(this.primaryStage, keyStore, trustStore, kmf, tmf);
		} catch(NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
			System.err.println("Failed to start up the key management engines: " + e.getMessage());
		}
		// clear out password
		Arrays.fill(keyStorePassword, ' ');
		Arrays.fill(trustStorePassword, ' ');
	}
}
