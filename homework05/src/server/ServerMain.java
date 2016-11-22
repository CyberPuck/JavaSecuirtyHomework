package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import commonUIElements.CommandLineArgs;
import commonUIElements.KeystoreAccessController;
import commonUIElements.KeystoreAccessInterface;
import commonUIElements.XMLLogFormatter;
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
	private static Logger logger = Logger.getLogger("ServerLogger");

	public static void main(String[] args) {
		// parse the command line
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
					settings.setProperty("serverAlias", "myKey");
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
			// setup the logger
			try {
				// setup console logger
				ConsoleHandler ch = new ConsoleHandler();
				ch.setFormatter(new SimpleFormatter());
				logger.addHandler(ch);
				// setup XML logger, appending so there is an audit trail over
				// the life of the log file
				FileHandler fileHandler = new FileHandler(settings.getProperty("logFile"), true);
				fileHandler.setFormatter(new XMLLogFormatter());
				logger.addHandler(fileHandler);

				logger.info("Starting the UI");
			} catch (IOException e) {
				logger.severe("Failed to setup the XML file logger, logging to console");
			}
			// Start the UI
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
		popupController = new KeystoreAccessController(this, "Server key store access");
		popupController.getKeystoreAccessStage().show();
	}

	@Override
	public void onLoginRequest(final char[] keyStorePassword, final char[] trustStorePassword) {
		// Try to open the key store
		KeyStore keyStore = KeyStoreAccessor.getKeyStore(keyStorePassword, parser.getKeystoreLocation());
		// Try to open the trust store
		KeyStore trustStore = KeyStoreAccessor.getKeyStore(trustStorePassword, parser.getTrustStoreLocation());
		// clear out password
		Arrays.fill(keyStorePassword, ' ');
		Arrays.fill(trustStorePassword, ' ');
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
		this.serverContoller = new ServerUILayoutController(this.primaryStage, settings, parser.getSettings(), keyStore, trustStore);
	}

}
