package com.kris.chat.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.kris.chat.MainApp;
import com.kris.chat.connection.ConnectionManager;
import com.kris.chat.connection.MessageListenerAdapter;
import com.kris.chat.interfaces.MessageListener;
import com.kris.chat.model.ChatClientModel;
import com.kris.chat.model.User;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginLoayoutController implements Initializable {
	private PresentationController presentationController;
	private ConnectionManager connectionManager;


	private MessageListener adapter = new MessageListenerAdapter(){
		@Override
		public void handleLogin(boolean status) {
			if (status) {
				presentationController.showMainLayout();
			} else {
				try {
					connectionManager.getSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				errorLable.setText("Wrong username or passowrd!");
			}
		}
		
	};

	public void setConnectionManager(ConnectionManager connectionManager) {
		if (this.connectionManager != null) {
			this.connectionManager.removeMessageListener(adapter);
		}
		this.connectionManager = connectionManager;
		this.connectionManager.addMessageListener(adapter);
		
	}

	public void setPresentationController(PresentationController presentationController) {
		this.presentationController = presentationController;
	}

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Label errorLable;
	
	private ChatClientModel model;

	public Label getErrorLable() {
		return errorLable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void handleLogin(){
		if (!(usernameField.getText().length() <= 0) && !(passwordField.getText().length() <= 0)) {
			MainApp.model.setClientUser(new User(usernameField.getText(), passwordField.getText()));
			try {
				if (!connectionManager.isConnectionEstablished()) {
					model = MainApp.model;
					connectionManager.establishConnection();
					connectionManager.authenticate(model.getClientUser().getUsername(), model.getClientUser().getPassword());
				}
			} catch (IOException e) {
				errorLable.setText("Server currently unavailable!");;
			}
		}
	}
	
	@FXML
	private void handleRegister() {
		/*if (!(usernameField.getText().length() <= 0) && !(passwordField.getText().length() <= 0)) {
			model.setClientUser(new User(usernameField.getText(), passwordField.getText()));
			try {
				if (!connectionManager.isConnectionEstablished()) {
					model = MainApp.model;
					connectionManager.establishConnection();
					connectionManager.registerUser(model.getClientUser().getUsername(), model.getClientUser().getPassword());
				}
			} catch (IOException e) {
				errorLable.setText("Server currently unavailable!");;
			}
		}*/
	}
	
	@FXML
	private void handleExit(Event event) {
		Platform.exit();
	}

	@FXML
	private void onEnterPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
				handleLogin();
		}
	}
}
