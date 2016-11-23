package client;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import commonUIElements.Message;
import commonUIElements.SocketResponseInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Primary UI controller for the client, handles connecting to the server,
 * sending messages, and receiving messages.
 * 
 * @author Kyle
 */
public class ClientUILayoutController implements Initializable, ServerLoginPopupInterface, SocketResponseInterface {
	@FXML
	private Button loginBtn;
	@FXML
	private TextArea rxField;
	@FXML
	private Button clearDisplayBtn;
	@FXML
	private TextArea msgField;
	@FXML
	private ChoiceBox<Integer> clearanceComboBox;
	@FXML
	private Button sendMsgBtn;
	// flag indicating if the client is connected to the sever
	private boolean connected = false;
	// represents the stage object
	private Stage clientUIStage;
	// Controller for the server login pop up
	private ServerLoginPopupController loginController;
	// socket for connecting to the server
	private ClientSSLSocket socket;
	// message queue
	private BlockingQueue<Message> messages = new ArrayBlockingQueue<>(5);
	// key store with certificates
	private KeyStore keyStore;
	// trust store with private key
	private KeyStore trustStore;
	// String representing the alias of the client certificate
	private String alias;
	// managers for the key stores
	private KeyManagerFactory kmf;
	private TrustManagerFactory tmf;

	/**
	 * Creates the primary GUI controller. Takes in both the key and trust
	 * stores to communicate with the server.
	 * 
	 * @param primaryStage
	 *            used for drawing the UI
	 * @param ks
	 *            key store with certs
	 * @param ts
	 *            key store with private key
	 */
	public ClientUILayoutController(Stage primaryStage, KeyStore ks, KeyStore ts, KeyManagerFactory kmf,
			TrustManagerFactory tmf) {
		this.keyStore = ks;
		this.trustStore = ts;
		this.kmf = kmf;
		this.tmf = tmf;

		this.clientUIStage = primaryStage;
		// fire up the client UI
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientUILayout.fxml"));
			// setup controller
			loader.setController(this);
			// setup the UI
			Pane pane = loader.load();
			Scene myScene = new Scene(pane);
			clientUIStage.setScene(myScene);
			clientUIStage.setTitle("Secure Chat Client");
			clientUIStage.setResizable(true);
			clientUIStage.show();
			// init the login controller
			loginController = new ServerLoginPopupController(this.clientUIStage, this);
		} catch (IOException e) {
			System.err.println("Failed to load the primary UI");
			System.exit(1);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// add clearance levels to comboBox
		this.clearanceComboBox.getItems().addAll(1, 2, 3, 4);
		this.clearanceComboBox.setValue(1);
		// Handle the buttons
		sendMsgBtn.setDisable(true);
		// setup button listeners
		loginBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!connected) {
					// run login function
					loginController.showPopup(true);
					// disable the login button
					loginBtn.setDisable(true);
				} else {
					socket.stop();
					connected = false;
				}
				updateButtons();
			}
		});

		clearDisplayBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// clear out text area
				rxField.clear();
			}
		});

		sendMsgBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// run send message function
				if (connected) {
					// only send a message if we are connected
					// get the required fields for the message
					Message message = new Message(alias, msgField.getText(), "", clearanceComboBox.getValue());
					// record message sent on UI
					rxField.setText(rxField.getText() + "Me: " + message.message + "\n");
					// record alias for the server to look up the certificate
					message.alias = alias;
					socket.writeMessage(message);
					// clear out the message field
					msgField.clear();
				}
			}
		});

		// force the text area to always scroll
		rxField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				rxField.setScrollTop(Double.MAX_VALUE);
			}
		});
		// handle the close event
		clientUIStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// close socket if open
				if (connected) {
					socket.stop();
					connected = !connected;
				}
			}
		});
	}

	/**
	 * Displays a message from the pop up.
	 * 
	 * @param message
	 *            from the login pop up
	 */
	public void displayMessage(String message) {
		this.rxField.setText(this.rxField.getText() + message + "\n");
	}

	/**
	 * Allows the login pop up to update the status of the login button.
	 * 
	 * @param isShown
	 *            flag indicating if the popup is visible
	 */
	public void loginPopUpUdate(boolean isShown) {
		loginBtn.setDisable(isShown);
	}

	@Override
	public void login(ServerAttributes attr) {
		// try to unlock the private key
		PrivateKey clientKey = null;
		this.alias = attr.alias;
		try {
			clientKey = (PrivateKey) this.keyStore.getKey(alias, attr.password);
			if (clientKey == null) {
				this.rxField.setText(rxField.getText() + "Error either the key password or alias is incorrect\n");
				return;
			}
		} catch (Exception e) {
			this.rxField.setText(rxField.getText() + "Error unlocking key: " + e.getMessage() + "\n");
		}
		this.socket = new ClientSSLSocket(attr.serverName, attr.port, messages, this);
		try {
			this.socket.startClient(this.trustStore, clientKey, this.kmf, this.tmf);
		} catch (Exception e) {
			this.rxField.setText(rxField.getText() + "Error: " + e.getMessage() + "\n");
		}
		connected = true;
		updateButtons();
	}

	/**
	 * Updates the login and send message buttons based on state.
	 */
	private void updateButtons() {
		if (connected) {
			loginBtn.setText("Log out");
			sendMsgBtn.setDisable(false);
		} else {
			loginBtn.setText("Log in");
			sendMsgBtn.setDisable(true);
		}
	}

	@Override
	public void socketMessage(Message message) {
		// write out a socket message
		rxField.setText(
				rxField.getText() + message.senderName + "@" + message.clearance + ": " + message.message + "\n");
		if (message.error || message.kill) {
			this.socket.stop();
			connected = !connected;
			// JavaFx threading issue :(
			// updateButtons();
		}
	}

	@Override
	public void socketError(String error) {
		// write out an error
		rxField.setText(rxField.getText() + "Error: " + error + "\n");
	}
}
