package server;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ServerUILayoutController implements Initializable{
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
	private Button revokeClientCertBtn;
	@FXML
	private TextField clientCertField;
	@FXML
	private Button browseCertBtn;
	@FXML
	private Button importCertBtn;
	@FXML
	private TextField serverPortField;
	
	private boolean serverOnline = false;
	private Stage serverUIStage;
	// TODO: Implement server start up popup
	
	public ServerUILayoutController(Stage primaryStage) {
		this.serverUIStage = primaryStage;
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
//			loginController = new ServerLoginPopupController(this.serverUIStage, this);
		} catch (IOException e) {
			System.err.println("Failed to load the primary UI");
			System.exit(1);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// add clearance levels to comboBox
		System.out.println("TODO: Implement the server UI");
	}
}
