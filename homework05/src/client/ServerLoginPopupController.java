package client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerLoginPopupController implements Initializable {
	@FXML
	private TextField serverAddressField;
	@FXML
	private TextField portNumberField;
	@FXML
	private TextField userNameField;
	@FXML
	private TextField userPasswordField;
	@FXML
	private Button loginBtn;
	@FXML
	private Button cancelBtn;
	// stage for drawing graphics
	private Stage controllerStage;
	// handle to the client UI controller, used for feedback
	private ClientUILayoutController clientContoller;
	// state to tell if the controller is actively displaying the pop up
	private boolean displayed = false;

	/**
	 * Setup the server login pop up, the ClientUILayoutController is necessary
	 * for feedback to the primary UI.
	 * 
	 * @param primaryStage
	 *            used for displaying the UI
	 * @param controller
	 *            used for feedback to the primary UI
	 */
	public ServerLoginPopupController(Stage primaryStage, ClientUILayoutController controller) {
		this.clientContoller = controller;
		// display UI and set the controller
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerLoginPopup.fxml"));
			// setup controller
			loader.setController(this);
			// setup the UI
			Pane pane = loader.load();
			Scene myScene = new Scene(pane);
			Stage popupStage = new Stage();
			this.controllerStage = popupStage;
			// ensure we have a proper hierarchy
			this.controllerStage.initModality(Modality.NONE);
			this.controllerStage.setScene(myScene);
			this.controllerStage.setTitle("Server Login");
			this.controllerStage.setResizable(false);
			// add handler for the close event
			this.controllerStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					// notify the client UI this popup closed
					clientContoller.loginPopUpUdate(false);
				}
			});
		} catch (IOException e) {
			this.clientContoller.displayMessage("Failed to load server login popup");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// setup the button listeners
		loginBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// run the login function
				if(isInputFilledIn()) {
					ServerAttributes attr = new ServerAttributes();
					attr.serverName = serverAddressField.getText();
					attr.port = Integer.parseInt(portNumberField.getText());
					clientContoller.login(attr);
					// hide the pop up
					showPopup(false);
				}
			}
		});

		cancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// hide the pop up
				showPopup(false);
			}
		});
	}

	/**
	 * Method for showing or hiding the login popup. Controlled by the client
	 * UI.
	 * 
	 * @param showPopup
	 *            flag indicating if the popup should be showed or hidden
	 */
	public void showPopup(boolean showPopup) {
		if (showPopup) {
			this.controllerStage.show();
			this.displayed = true;
		} else {
			this.controllerStage.hide();
			this.displayed = false;
		}
		// disable/enable client UI log in button
		clientContoller.loginPopUpUdate(showPopup);
	}

	/**
	 * Returns the flag indicating if the server connection popup is displayed.
	 * 
	 * @return boolean
	 */
	public boolean isDisplayed() {
		return displayed;
	}

	/**
	 * Check that all inputs have been filled in.
	 * 
	 * @return boolean indicating if the inputs in the popup have been filled in
	 */
	private boolean isInputFilledIn() {
		if(serverAddressField.getText().length() > 0 && portNumberField.getText().length() > 0) {
			return true;
		}
		return false;
	}
}
