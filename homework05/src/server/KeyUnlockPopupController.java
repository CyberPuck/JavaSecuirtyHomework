package server;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Handles unlocking the server's key in the key store. It is protected with a
 * different password than the key stores.
 * 
 * @author Kyle
 */
public class KeyUnlockPopupController implements Initializable {
	@FXML
	private PasswordField keyPasswordField;
	@FXML
	private Button unlockBtn;
	// controller access to allow modifications after the UI finishes
	private ServerUILayoutController controller;
	// Stage for drawing the popup
	private Stage popupStage;

	public KeyUnlockPopupController(ServerUILayoutController controller) {
		this.controller = controller;
		// setup the UI
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("KeyUnlockPopup.fxml"));
			// setup controller
			loader.setController(this);
			// setup UI
			Pane pane = loader.load();
			Scene myScene = new Scene(pane);
			this.popupStage = new Stage();
			this.popupStage.initModality(Modality.NONE);
			this.popupStage.setScene(myScene);
			this.popupStage.setTitle("Server Key Unlock");
			this.popupStage.setResizable(false);
			// handle the exit button
			this.popupStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					// update the controller
					controller.keyUnlockPopupUpdate(false);
				}
			});
		} catch (IOException e) {
			this.controller.displayMessage("Failed to load key login popup");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// handle the button
		unlockBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// check the password is good
				if (keyPasswordField.getText().length() > 0) {
					controller.unlockKey(keyPasswordField.getText().toCharArray());
					// clear out the text
					keyPasswordField.clear();
					// hide the popup
					showPopup(false);
				}
			}
		});
	}

	/**
	 * Handles showing the pop up and updating the server start button.
	 * 
	 * @param showPopup
	 *            flag indicating if the pop up is visible.
	 */
	public void showPopup(boolean showPopup) {
		if (showPopup) {
			this.popupStage.show();
		} else {
			this.popupStage.hide();
		}
		// disable/enable server UI log in button
		controller.keyUnlockPopupUpdate(showPopup);
	}

}
