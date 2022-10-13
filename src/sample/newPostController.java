package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class newPostController {

    @FXML
    public TextArea textArea;

    @FXML
    private Button newPost;

    @FXML
    private Button pho;

    public static boolean newPo = false, photo = false;

    public static String postCont = "";

    public static String file = "";

    public static byte[] fileInBytes = {};

    public void newPostOnAction(ActionEvent actionEvent){
        newPo = true;
        postCont = textArea.getText();
        Stage stage = (Stage) newPost.getScene().getWindow();
        stage.close();
    }

    public void photoOnAction(ActionEvent actionEvent) {
        Stage dialogStage = new Stage();
        FileChooser fc = new FileChooser();
        try {
            File tmp = fc.showOpenDialog(pho.getScene().getWindow());
            FileInputStream fileInput = new FileInputStream(tmp);
            fileInBytes = Files.readAllBytes(tmp.toPath());
            String encoded_content =
                    Base64.getEncoder().encodeToString(fileInBytes);
            fileInput.read(fileInBytes);
            fileInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) pho.getScene().getWindow();
        stage.close();
        photo = true;
    }

}
