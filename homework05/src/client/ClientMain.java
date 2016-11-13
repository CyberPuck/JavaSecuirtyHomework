package client;

import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;

import commonUIElements.KeystoreAccessController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import keyStore.KeyStoreAccessor;

/**
 * Entry point for the client that communicates with the server.
 * 
 * @author Kyle
 */
public class ClientMain extends Application {
	// The title of the UI application
	private String title = "Secure Chat Client";
	// command line args that have been parsed
	private static CommandLineArgs parser = new CommandLineArgs();
	// handle to the primary stage of the application, used so if the keystore
	// is unlocked the client UI will boot up
	private Stage primaryStage;
	// initial log in pop up controller
	private KeystoreAccessController popupController;

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
			// execute the client application
			// TODO: Move this logic to the UI controller that will establish
			// the connection
			// ClientSSLSocket socket = new
			// ClientSSLSocket(parser.getHostName(), parser.getPort());
			// try {
			// socket.run();
			// long startTime = System.currentTimeMillis();
			// while (System.currentTimeMillis() - startTime < 60000) {
			// // do nothing
			// }
			// } catch (Exception e) {
			// System.err.println("Socket err: " + e.getMessage());
			// }
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
		popupController = new KeystoreAccessController(this);
		popupController.getKeystoreAccessStage().show();
	}

	public void onLoginRequest(final char[] password) {
		// Try to open the keystore
		KeyStore keyStore = KeyStoreAccessor.getKeyStore(password, parser.getKeystoreLocation());
		// clear out password
		Arrays.fill(password, ' ');
		if (keyStore == null) {
			System.err.println("Keystore failed to open at:  " + parser.getKeystoreLocation()
					+ " either the keystore does not exist or the password was incorrect");
			System.exit(1);
		}
		// remove the popup UI
		this.popupController.getKeystoreAccessStage().close();
		// fire up the client UI
		try {
			Pane pane = FXMLLoader.load(getClass().getResource("ClientUILayout.fxml"));
			Scene myScene = new Scene(pane);
            primaryStage.setScene(myScene);
            primaryStage.setResizable(true);
            primaryStage.show();
		} catch (IOException e) {
			System.err.println("Failed to load the primary UI");
			System.exit(1);
		}
	}
}
