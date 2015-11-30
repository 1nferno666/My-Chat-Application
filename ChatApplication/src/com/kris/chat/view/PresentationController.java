package com.kris.chat.view;

import java.io.IOException;

import com.kris.chat.MainApp;
import com.kris.chat.connection.ConnectionManager;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PresentationController {
	private Stage primaryStage;
	private Scene loginScene;
	private Scene mainScene;
	private ConnectionManager connectionManager;

	public void showLoginLayout() {
		FXMLLoader fxlmLoader = new FXMLLoader();
		fxlmLoader.setLocation(MainApp.class.getResource("view/LoginLayout.fxml"));

		try {
		    AnchorPane rootLayout = fxlmLoader.load();
		    LoginLoayoutController loginCotroller =  fxlmLoader.getController();
		    loginCotroller.setConnectionManager(connectionManager);
		    loginCotroller.setPresentationController(this);
		    loginScene = new Scene(rootLayout);
		    primaryStage.hide();
			primaryStage.setScene(loginScene);
			primaryStage.show();
		} catch (IOException e) {
			System.err.println("Failed to load FXML file!");
			e.printStackTrace();
		}
	}

	public void showMainLayout() {
		FXMLLoader fxlmLoader = new FXMLLoader();
		fxlmLoader.setLocation(MainApp.class.getResource("view/MainLayout.fxml"));

		try {
		    AnchorPane rootLayout = fxlmLoader.load();
		    MainLayoutController mainCotroller =  fxlmLoader.getController();
		    mainCotroller.setConnecitonManager(connectionManager);
		    mainCotroller.setPresentationController(this);
		    mainCotroller.setModel(MainApp.model);
		    mainScene = new Scene(rootLayout);
		    primaryStage.hide();
			primaryStage.setScene(mainScene);
			primaryStage.show();
			connectionManager.requestInformation();
		} catch (IOException e) {
			System.err.println("Failed to load FXML file!");
			e.printStackTrace();
		}
	}
	
	public void showDialogLayout(Event event, MainLayoutController main) {
		try {
			// Load the fxml file and create a new stage for the pop-up dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/CreateRoomDialog.fxml"));
			AnchorPane page = loader.load();
			
			CreateRoomDialogController dialogController = loader.getController();
			dialogController.setConnectionManager(connectionManager);
			dialogController.setModel(MainApp.model);
			dialogController.setMainLayoutController(main);
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Create Chat Room");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner((Stage) ((Node) event.getSource()).getScene().getWindow());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PresentationController(Stage primaryStage, ConnectionManager connectionManager) {
		this.primaryStage = primaryStage;
		this.connectionManager = connectionManager;
	}

}
