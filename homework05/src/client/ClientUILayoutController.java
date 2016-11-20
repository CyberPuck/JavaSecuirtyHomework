package client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

public class ClientUILayoutController implements Initializable, ServerLoginPopupInterface, SocketResponseInterface {
	@FXML
	private Button loginBtn;
	@FXML
	private Button exportBtn;
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

	public ClientUILayoutController(Stage primaryStage) {
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
					connected = true;
				} else {
					socket.stop();
					connected = false;
				}
				updateButtons();
			}
		});

		exportBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// run export function
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
					socket.writeMessage(clearanceComboBox.getValue() + msgField.getText());
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

	public void displayMessage(String message) {
		this.rxField.setText(this.rxField.getText() + "\n" + message);
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
		this.socket = new ClientSSLSocket(attr.serverName, attr.port, messages, this);
		try {
			this.socket.startClient();
		} catch (Exception e) {
			this.rxField.setText(rxField.getText() + "\nError: " + e.getMessage());
		}
		connected = true;
		updateButtons();
	}

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
		rxField.setText(rxField.getText() + "\n" + message.senderName + "@" + message.clearance + ": " + message.message);
	}

	@Override
	public void socketError(String error) {
		// write out an error
		rxField.setText(rxField.getText() + "\nError: " + error);
	}
}
