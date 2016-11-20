package commonUIElements;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Handles the initial log keystore access. Designed similarly to encrypted
 * drives on a computer. In order to access the client pane you need to unlock
 * the keystore, pointed to from the command line argument.
 * 
 * @author Kyle
 */
public class KeystoreAccessController implements Initializable {
	@FXML
	private PasswordField keystorePasswordField;
	@FXML
	private PasswordField truststorePasswordField;
	@FXML
	private Button unlockKeystoreBtn;
	// The stage context, will be used to show this pop up
	private Stage keystoreAccessStage;
	// handle to main component that will handle the login data.
	private KeystoreAccessInterface main;

	/**
	 * Constructor, setups the JavaFX controller, keystore authentication UI,
	 * and passes the entered password to the keystore functions. ClientMain is
	 * required so when the password is passed up the primary UI can be loaded
	 * on success.
	 * 
	 * @param main
	 *            handle to ClientMain for when the password is entered.
	 */
	public KeystoreAccessController(KeystoreAccessInterface main) {
		this.main = main;
		// load the scene
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("KeystoreAccess.fxml"));
			// setup this class as the controller
			loader.setController(this);
			// setup the UI
			Pane pane = loader.load();
			Scene keystoreScene = new Scene(pane);
			this.keystoreAccessStage = new Stage();
			this.keystoreAccessStage.setTitle("Client keystore access");
			this.keystoreAccessStage.setScene(keystoreScene);
		} catch (Exception e) {
			System.err.println("Failed to load KeystoreAccess UI: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Access to the stage file to display the UI.
	 * 
	 * @return Stage of this controller
	 */
	public Stage getKeystoreAccessStage() {
		return this.keystoreAccessStage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// setup the button logic
		this.unlockKeystoreBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// Call update function
				handleLoginRequest();
			}
		});
		// login was hit by a key press, not the mouse
		this.unlockKeystoreBtn.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// Call update function
				handleLoginRequest();
			}
		});
		// enter was hit in the password box, behave like the login button
		this.keystorePasswordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					handleLoginRequest();
				}
			}
		});
		// enter was hit in the password box, behave like the login button
		this.truststorePasswordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					handleLoginRequest();
				}
			}
		});
	}

	/**
	 * When the user passes up the password make sure to pass it to main for
	 * verification.
	 */
	private void handleLoginRequest() {
		// give the password to client main
		this.main.onLoginRequest(this.keystorePasswordField.getText().toCharArray(), this.truststorePasswordField.getText().toCharArray());
		// clear out JavaFX password field
		this.keystorePasswordField.clear();
		this.truststorePasswordField.clear();
	}
}
