package com.kris.chat;


import java.io.IOException;

import com.kris.chat.connection.ConnectionManager;
import com.kris.chat.model.ChatClientModel;
import com.kris.chat.view.PresentationController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {
	private volatile Stage primaryStage;
	private PresentationController presentationController;
	public static ChatClientModel model;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("My Chat App");
		this.primaryStage.setResizable(false);
		ConnectionManager connectionManager = new ConnectionManager();
		model = new ChatClientModel();
		presentationController = new PresentationController(primaryStage, connectionManager);
		presentationController.showLoginLayout();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                    	if (model != null) {
                    		connectionManager.handleExit(model.getClientUser().getUsername());
						}
                        System.out.println("Application Closed by click to Close Button(X)");
                        System.exit(0);
                        try {
							connectionManager.getSocket().close();
							if (model != null) {
								connectionManager.handleExit(model.getClientUser().getUsername());
							}
						} catch (IOException e) {
							System.err.println("Cannot close socket!");
							e.printStackTrace();
						}
                    }
                });
            }
		});
		
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
