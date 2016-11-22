package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import commonUIElements.Message;
import commonUIElements.SocketResponseInterface;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerUILayoutController implements Initializable, SocketResponseInterface, KeyUnlockPopupInterface {
	private static Logger logger = Logger.getLogger(ServerUILayoutController.class.getName());

	@FXML
	private TextArea activityMsgArea;
	@FXML
	private TextField activityLogFilefield;
	@FXML
	private ScrollPane clientScrollPane;
	@FXML
	private TextField settingsLogFileField;
	@FXML
	private Button logFileBrowser;
	@FXML
	private Button saveSettingsBtn;
	@FXML
	private Button revokeClientCertBtn;
	@FXML
	private Button serverBtn;
	@FXML
	private TextField clientCertField;
	@FXML
	private Button browseCertBtn;
	@FXML
	private Button importCertBtn;
	@FXML
	private TextField serverPortField;

	private static String LOG_FILE_PROPTERTY = "logFile";
	private static String SERVER_PORT_PROPTERTY = "port";

	private boolean serverOnline = false;
	private Stage serverUIStage;
	private Properties settings;
	private String settingsFile;
	private FileChooser fileChooser = new FileChooser();
	private ServerSSLSocket serverSSLSocket;
	private BlockingQueue<Message> messages = new ArrayBlockingQueue<>(5);
	// Controller for the key login pop up
	private KeyUnlockPopupController keyUnlockController;
	// Holds the client certificates
	private KeyStore keyStore;
	// Holds the server private/public keys
	private KeyStore trustStore;
	// Hold the key object for signing and SSL access
	private PrivateKey serverKey;

	public ServerUILayoutController(Stage primaryStage, Properties settings, String settingsFile, KeyStore keyStore,
			KeyStore trustStore) {
		this.settings = settings;
		this.serverUIStage = primaryStage;
		this.settingsFile = settingsFile;
		this.keyStore = keyStore;
		this.trustStore = trustStore;
		// fire up the client UI
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerUILayout.fxml"));
			// setup controller
			loader.setController(this);
			// setup the UI
			Pane pane = loader.load();
			Scene myScene = new Scene(pane);
			serverUIStage.setScene(myScene);
			serverUIStage.setTitle("Secure Chat Server");
			serverUIStage.setResizable(true);
			serverUIStage.show();
			// init the login controller
			keyUnlockController = new KeyUnlockPopupController(this);

			serverSSLSocket = new ServerSSLSocket(Integer.parseInt(settings.getProperty(SERVER_PORT_PROPTERTY)),
					messages, this);
		} catch (IOException e) {
			System.err.println("Failed to load the primary UI");
			logger.log(Level.SEVERE, "Failed to load the primary UI");
			System.exit(1);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// make activity boxes not editable
		activityLogFilefield.setEditable(false);
		activityMsgArea.setEditable(false);

		// update Settings data from the file
		if (settings.containsKey(LOG_FILE_PROPTERTY)) {
			settingsLogFileField.setText(settings.getProperty(LOG_FILE_PROPTERTY));
			activityLogFilefield.setText(settings.getProperty(LOG_FILE_PROPTERTY));
		}
		if (settings.containsKey(SERVER_PORT_PROPTERTY)) {
			serverPortField.setText(settings.getProperty(SERVER_PORT_PROPTERTY));
		}

		// add button handlers
		saveSettingsBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				settings.setProperty(LOG_FILE_PROPTERTY, settingsLogFileField.getText());
				activityLogFilefield.setText(settings.getProperty(LOG_FILE_PROPTERTY));
				settings.setProperty(SERVER_PORT_PROPTERTY, serverPortField.getText());
				try {
					settings.store(new FileOutputStream(settingsFile), null);
				} catch (IOException e) {
					System.err.println("Failed to save settings file: " + e.getMessage());
					activityMsgArea.setText(
							activityMsgArea.getText() + "Failed to save settings file: " + e.getMessage() + "/n");
					logger.log(Level.SEVERE, "Failed to save settings file: " + e.getMessage());
				}
				// TODO: update the logger
				// TODO: stop the server if it is running
				if (serverOnline) {
					serverSSLSocket.stop();
				}
			}
		});

		// add the log file browse button
		logFileBrowser.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				File file = fileChooser.showOpenDialog(serverUIStage);
				if (file != null) {
					settingsLogFileField.setText(file.toString());
				}
			}
		});

		// setup the start server button
		serverBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!serverOnline) {
					// start the unlock controller
					keyUnlockController.showPopup(true);

				} else {
					serverBtn.setText("Start Server");
					// stop the server
					serverSSLSocket.stop();
					// toggle boolean
					serverOnline = !serverOnline;
				}

			}
		});
		// handle the close button
		serverUIStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// if the server is online, close all connections
				if (serverOnline) {
					serverSSLSocket.stop();
					serverOnline = !serverOnline;
				}
			}
		});
	}

	@Override
	public void socketMessage(Message message) {
		logger.log(Level.INFO,
				"Msg from client: " + message.senderName + "@" + message.clearance + ": " + message.message);
		System.out.println("Msg from client: " + message);
		activityMsgArea.setText(activityMsgArea.getText() + message.senderName + "@" + message.clearance + ": "
				+ message.message + "\n");
		message.alias = settings.getProperty("serverAlias");
		this.serverSSLSocket.writeMessage(message);
	}

	@Override
	public void socketError(String error) {
		activityMsgArea.setText(activityMsgArea.getText() + "Error: " + error + "\n");
		logger.log(Level.SEVERE, "Socket error: " + error);
		// stop socket
		this.serverSSLSocket.stop();
	}

	/**
	 * Displays a message in the activity window of the server UI.
	 * 
	 * @param message
	 *            String to show on the activity area.
	 */
	public void displayMessage(String message) {
		activityMsgArea.setText(activityMsgArea.getText() + message + "\n");
	}

	@Override
	public void unlockKey(char[] password) {
		// attempt to unlock the key
		try {
			if(keyStore.containsAlias(settings.getProperty("serverAlias"))) {
				System.out.println("Key exists");
			}
			serverKey = (PrivateKey) keyStore.getKey(settings.getProperty("serverAlias"), password);
			// verify the password
			if (serverKey == null) {
				activityMsgArea.setText(
						activityMsgArea.getText() + "Error: Key password is invalid or the alias is incorrect\n");
				logger.log(Level.SEVERE, "Error: Key password is invalid or the alias is incorrect\n");
			} else {
				// start the server
				serverSSLSocket.startServer(trustStore, serverKey);
				serverBtn.setText("Stop Server");
				serverOnline = !serverOnline;
			}
		} catch (Exception e) {
			activityMsgArea.setText(activityMsgArea.getText() + "Error: " + e.getMessage() + "\n");
			logger.log(Level.SEVERE, "Key unlock error: " + e.getMessage());
		}
		// wipe the password
		Arrays.fill(password, ' ');
	}

	/**
	 * Enables a pop up to disable the server button until dialog operations
	 * have completed.
	 * 
	 * @param isDisabled
	 *            boolean indicating if the button should be disabled.
	 */
	public void keyUnlockPopupUpdate(boolean isDisabled) {
		serverBtn.setDisable(isDisabled);
	}
}
