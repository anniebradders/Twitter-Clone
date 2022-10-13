package sample;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController{


    private String hostName = "localhost";
    private int portNumber = 12345;

    @FXML
    private static Label loginMessageLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label numAttempt;

    public static String msg = "";

    public void loginButtonOnAction() throws IOException, SQLException {
        if(!usernameInput.getText().isBlank() && !passwordInput.getText().isBlank()){
            msg = "login";
            //creates a new client using the username and password from the two textFields
            new Client(usernameInput.getText(), passwordInput.getText(), hostName, portNumber).activate();
        }else{
            //changes the label if either of the textFields are blank
            loginMessageLabel.setText("Not a valid username and/or password");
        }
    }
}


