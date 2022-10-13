package sample;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Client {
    //declares the variables which will be used across methods within the class Client
    private String username, password, hostName;
    private int portNumber;
    private boolean stop;
    public String message;
    public static int x = 0;

    //Client constructor - makes a new client with the relevant information for every new login
    Client(String username, String password, String hostName, int portNumber){
        this.username = username;
        this.password = password;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    //runs the GUI
    private void runClient(){
        //sets message as default login when intially started
        message = "login";
        //intialises variables
        stop = false;
        int userID = 0;
        int counter = 0;
        try (
                //Connect client to the server via a new socket using the hostname and port number
            Socket socket = new Socket(hostName, portNumber);
            //sets up out to read into server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //sets up in to read from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ){
                //passes login to server
                out.println("login");

                //passes username and password to server
                out.println(username);
                out.println(password);

                //reads the value out of server and stores it in display
                String check = in.readLine();
                //if the login details match ones in the database than a new scene will be displayed
                if(check.equals("pass")) {

                    //gets userID from server
                    String stringID = in.readLine();
                    //makes userID an int as it is a string when read from the server
                    userID = Integer.parseInt(stringID);
                    //intialises variables so they can be used in PLatform.runLater
                    int finalUserID = userID;
                    int finalCounter = counter;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //creates new stage/scene using the display.fxml
                                Stage dialogStage = new Stage();
                                Parent root = FXMLLoader.load(getClass().getResource("display.fxml"));
                                dialogStage.setTitle("Welcome Back " + username);
                                dialogStage.setScene(new Scene(root, 1000, 600));
                                dialogStage.show();
                                //passes the array of posts to the client having used JSON to convert to string
                                String serverResponse = in.readLine();
                                //Converts the array of posts to a JSON object
                                Object json = JSONValue.parse(serverResponse);

                                Request req;
                                //creates a list of posts and intialises it
                                List<post> data = new ArrayList<>();
                                //stores the array of posts within req
                                req = postResponse.fromJSON(json);
                                //if the element within req doesnt equal null loops through the array
                                if (req != null){
                                    for(post p : ((postResponse)req).getMessages()) {
                                        //adds the posts to the data array
                                        data.add(p);
                                    }
                                }
                                //prints out the values of post within the scene
                                displaySubs(finalUserID, data, finalCounter);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //increases counter by 1
                    counter++;
                }
                //while the users not logged out and the login details are valid
                while (!stop && (check.equals("pass"))) {
                    try {
                        //time arguments sleep for 3 seconds
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //default message is display
                    message = "display";
                    //if the unsubscribe button is clicked
                    while (postController.unsub == true) {
                        //unsubscribe is passed to server
                        message = "unsubscribe";
                        out.println(message);
                        //unsub is turned back to false to avoid the method repeating
                        postController.unsub = false;
                        //gets the user of the post they've unsubscribed from
                        String user = postController.user;
                        //passes user to the server
                        out.println(user);
                        //passes the array of posts to the client having used JSON to convert to string
                        String serverResponse = in.readLine();
                        //Converts the array of posts to a JSON object
                        Object json = JSONValue.parse(serverResponse);
                        Request req;
                        //creates a list of posts and intialises it
                        List<post> data = new ArrayList<>();
                        //stores the array of posts within req
                        req = postResponse.fromJSON(json);
                        //if the element within req doesnt equal null loops through the array
                        if (req != null){
                            for(post p : ((postResponse)req).getMessages()) {
                                //adds the posts to the data array
                                data.add(p);
                            }
                        }
                        //intialises variables so they can be used in PLatform.runLater
                        int finalUserID1 = userID;
                        int finalCounter1 = counter;
                        //updates the scene with the new posts
                        Platform.runLater(()->displaySubs(finalUserID1, data, finalCounter1));
                        postController.unsub = false;
                        break;
                        //if new post button is pressed
                        } while (newPostController.newPo) {
                            //passes new to the server
                            message = "new";
                            out.println(message);
                            //gets the content from the textarea and stores it in postContent
                            String postContent = newPostController.postCont;
                            //passes postContent and userID into the server
                            out.println(postContent);
                            out.println(userID);
                             //passes the array of posts to the client having used JSON to convert to string
                            //String serverResponse = in.readLine();
                            //updates the scene with the new post
                            //rerun(serverResponse, userID, counter);
                            newPostController.newPo = false;
                            break;
                            //if subscribe button is pressed
                        }while(displayController.subscribe) {
                            //subscribe is passed to the server
                            message = "subscribe";
                            out.println(message);
                            //the account the user wants to subscribe to is stored in subUser
                            String subUser = displayController.userSub;
                            //subUser is passed to the server
                            out.println(subUser);
                            //passes the array of posts to the client having used JSON to convert to string
                            //String serverResponse = in.readLine();
                            //updates the scene with the new post
                            //rerun(serverResponse, userID, counter);
                            displayController.subscribe = false;
                            break;
                            //if photo is pressed
                        }while(newPostController.photo) {
                            //photo is passed to the server
                            message = "photo";
                            out.println(message);
                            //byte array of the file is stored in file
                            byte[] file = newPostController.fileInBytes;
                            //the byte array is converted to string using base 64
                            String encoded_content = Base64.getEncoder().encodeToString(file);
                            //the string byte array is passed to the server
                            out.println(encoded_content);
                            //passes the array of posts to the client having used JSON to convert to string
                            //String serverResponse = in.readLine();
                            //updates the scene with the new post
                            //rerun(serverResponse, userID, counter);
                            newPostController.photo = false;
                        }while(displayController.logout){
                            message = "logout";
                            out.println(message);
                            displayController.logout = false;
                            stop = true;
                            break;
                    }
                        //passes the message to the server
                        out.println(message);
                        String serverResponse = in.readLine();
                    //updates the scene with the new post
                        rerun(serverResponse, userID, counter);
                    }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //starts a new thread for the client
    public void activate(){
        new Thread(this::runClient).start();
    }

    public static String message(String message){
        return message;
    }

    //displays the posts within the scene
    void displaySubs(int userID, List<post> data, int counter){
        try {
            displayController display = new displayController();
            GridPane grid = displayController.pane;

            //if the counter is greater than 0 then the gridPane is cleared for new data to written on
            if(counter>0) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //clears the grid
                            grid.getChildren().clear();
                            int column = 0;
                            int row = 0;
                            //for loop so the number of post.fxml's that are created are the number within the list data
                            for (post x : data) {
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                //loads new scene post.fxml within the scene display.fxml
                                fxmlLoader.setLocation(Client.class.getResource("post.fxml"));
                                AnchorPane anchorPane = fxmlLoader.load();
                                //assigns the class postController to the varibale postController
                                postController postController = fxmlLoader.getController();
                                //calls the method setData from the class postController to fill the labels with the data from the database
                                postController.setData(x);
                                //if column is equal to 2 than its reset the value to 0, and creates a new row
                                if (column == 2) {
                                    column = 0;
                                    row++;
                                }
                                //(child, column, row)
                                grid.add(anchorPane, column++, row);
                                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                                grid.setMaxWidth(Region.USE_PREF_SIZE);
                                //set grid height
                                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                                grid.setMaxHeight(Region.USE_PREF_SIZE);
                                //sets a margin for the products.fxml
                                GridPane.setMargin(anchorPane, new Insets(10));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if(counter==0) {
                int column = 0;
                int row = 0;
                //for loop so the number of post.fxml's that are created are the number within the list data
                for (post x : data) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    //loads new scene post.fxml within the scene display.fxml
                    fxmlLoader.setLocation(Client.class.getResource("post.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    //assigns the class postController to the varibale postController
                    postController postController = fxmlLoader.getController();
                    //calls the method setData from the class postController to fill the labels with the data from the database
                    postController.setData(x);
                    //if column is equal to 2 than its reset the value to 0, and creates a new row
                    if (column == 2) {
                        column = 0;
                        row++;
                    }
                    //(child, column, row)
                    grid.add(anchorPane, column++, row);
                    grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                    grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    grid.setMaxWidth(Region.USE_PREF_SIZE);
                    //set grid height
                    grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                    grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    grid.setMaxHeight(Region.USE_PREF_SIZE);
                    //sets a margin for the products.fxml
                    GridPane.setMargin(anchorPane, new Insets(10));
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    void rerun(String serverResponse, int userID, int counter){
        //Converts the array of posts to a JSON object
        Object json = JSONValue.parse(serverResponse);
        Request req;
        //creates a list of posts and intialises it
        List<post> data = new ArrayList<>();
        //stores the array of posts within req
        req = postResponse.fromJSON(json);
        //if the element within req doesnt equal null loops through the array
        if (req != null){
            for(post p : ((postResponse)req).getMessages()) {
                //adds the posts to the data array
                data.add(p);
            }
        }
        //intialises variables so they can be used in PLatform.runLater
        int finalUserID1 = userID;
        int finalCounter1 = counter;
        //prints out the values of post within the scene
        Platform.runLater(()->displaySubs(finalUserID1, data, finalCounter1));
    }

}
