package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Properties;

import commonUIElements.CommandLineArgs;
import commonUIElements.KeystoreAccessController;
import commonUIElements.KeystoreAccessInterface;
import javafx.application.Application;
import javafx.stage.Stage;
import keyStore.KeyStoreAccessor;

public class ServerMain extends Application implements KeystoreAccessInterface {
	private Stage primaryStage;
	private String title = "Secure Chat Server";
	private KeystoreAccessController popupController;
	private static CommandLineArgs parser;
	private ServerUILayoutController serverContoller;
	private static Properties settings;

	public static void main(String[] args) {
//		long timeout = 60000;
		parser = new CommandLineArgs("Server");
		parser.setSettings(".serverSettings");
		if (parser.parseCommandLineArgs(args)) {
			// Read in the settings file
			settings = new Properties();
			try {
				settings.load(new FileInputStream(parser.getSettings()));
			} catch (FileNotFoundException e) {
				System.out.println(parser.getSettings() + " does not exist, creating a default settings file");
				try {
					File newSettingsFile = new File(".serverSettings");
					newSettingsFile.createNewFile();
					settings.load(new FileInputStream(newSettingsFile));
					// set to default parameters
					settings.setProperty("logFile", "log.xml");
					settings.setProperty("port", "5280");
					settings.store(new FileOutputStream(newSettingsFile), null);
				} catch (IOException e2) {
					System.err
							.println("Failed to create default settings file, exiting application: " + e2.getMessage());
					System.exit(1);
				}
			} catch (IOException e) {
				// exit, something was wrong with the settings file
				System.out.println("Failed to open settings file: " + e.getMessage());
				System.exit(1);
			}
			// TODO: Implement controllers then add in SSL
			// ServerSSLSocket socket = new ServerSSLSocket(parser.getPort());
			// try {
			// socket.startServer();
			// long startTime = System.currentTimeMillis();
			// while(System.currentTimeMillis() - startTime <= timeout) {
			// // do nothing
			// }
			// } catch(Exception e) {
			// System.err.println(e.getMessage());
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

	@Override
	public void onLoginRequest(final char[] password) {
		// Try to open the keystore
		KeyStore keyStore = KeyStoreAccessor.getKeyStore(password, parser.getKeystoreLocation());
		// clear out password
		Arrays.fill(password, ' ');
		if (keyStore == null) {
			System.err.println("Keystore failed to open at:  \"" + parser.getKeystoreLocation()
					+ "\" either the keystore does not exist or the password was incorrect");
			System.exit(1);
		}
		// remove the popup UI
		this.popupController.getKeystoreAccessStage().close();
		// setup the client UI
		this.serverContoller = new ServerUILayoutController(this.primaryStage, settings, parser.getSettings());
	}

}
