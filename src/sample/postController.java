package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class postController {
    public post Post;

    @FXML
    private Label messageArea;

    @FXML
    private Label userName;

    @FXML
    private ImageView imageDisplay;

    @FXML
    private Button subscribe;

    public static String msg = "";

    public static boolean sub = false;

    public static boolean unsub = false;

    public static String user = "";

    public void setData(post Post) {
        this.Post = Post;
        //sets the Username label to the value of the username
            userName.setText("@" + Post.getUserName());
            //if there is a value in the FILE column that it is set to the imageview
        if(!Post.getImage().equals("")){
            String image = Post.getImage();
            byte[] raw_content = Base64.getDecoder().decode(image);
            Image img = new Image(new ByteArrayInputStream(raw_content));
            //image is displayed
            imageDisplay.setImage(img);
            //if the column POST_CONTENT is not null than the message area is set to that value
        } else if(Post.getTextPost() != null) {
            messageArea.setText(Post.getTextPost());
        }
    }

    public void editProductOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void subscribeOnAction(ActionEvent actionEvent) {
        //ubsub us set to true
        unsub = true;
        Client.message("unsubscribe");
        //user is equal to the username of the users who posted
        user = Post.getUserName();
        //msg = "unsubscribe";
    }

    public static String getSubName(post Post){
        return Post.getUserName();
    }

    public static String message(){
        return "unsubscribe";
    }
}
