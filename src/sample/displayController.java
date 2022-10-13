package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class displayController implements Initializable {

    @FXML
    public GridPane grid;

    @FXML
    public AnchorPane rootPane;

    @FXML
    public TextField subUser;
    public Button viewPost;

    @FXML
    private Button logoutButton;

    public static GridPane pane;

    public static boolean newPost = false;

    public static boolean viewAll = false;

    public static boolean subscribe = false;

    public static boolean logout = false;

    public static String userSub = "";

    @Override
    public void initialize(URL location, ResourceBundle resources){
        pane = pane();
    }

    public GridPane pane(){
            return this.grid;
    }


    public void homeOnAction(ActionEvent actionEvent) {

    }

    public void logoutOnAction(ActionEvent actionEvent) {
        logout = true;
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }

    public void searchButtonOnAction(ActionEvent actionEvent) {
        subscribe = true;
        userSub = subUser.getText();
    }

    public void postOnAction(ActionEvent actionEvent) throws IOException {
        //sets newPost to true
        newPost = true;
        //creates new stage using the newPost.fxml
        Stage dialogStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("newPost.fxml"));
        dialogStage.setTitle("New Post");
        dialogStage.setScene(new Scene(root, 600, 400));
        //displays the scene
        dialogStage.show();
    }

    public void viewAllOnAction(ActionEvent actionEvent) {
        viewAll = true;
    }

    public void viewPostOnAction(ActionEvent actionEvent) {
    }
}
