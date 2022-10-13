package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class server {

    public static void main(String[] args) {
        //intialises portNumber
        int portNumber = 12345;

        try(
                //creates new serverSocket with the port number
            ServerSocket serverSocket = new ServerSocket(portNumber);
        ){
            while(true){
                //server accepts connections
                Socket client = serverSocket.accept();
                //new thread is created with that socket
                new ClientHandler(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread{
        //creates variables which wil be used within the ClientHandler class
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        //constructor for clientHandler
        public ClientHandler(Socket socket) throws IOException {
            //makes sure socket is the correct socket for the thread is multiple clients are run at once
            this.clientSocket = socket;
            //sets out ready to pass to client
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            //sets in ready to read from client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public void run(){
            boolean stop = false;
            boolean y = false;
            try {
                int userID = 0;
                //creates connection to the databse
                Connection conn = databaseConnection.connect();
                //while stop equals false
                while (!stop) {
                    String input;
                    //reads from the client and stores it in the variable input and checks that its not null
                    if (((input = in.readLine()) != null)) {
                        boolean x = true;
                        //if input equals login
                        if (input.equals("login")) {
                            //selects all columns from the table users
                            String sql = "SELECT * FROM users";
                            //assigns the ability to create an sql statement to the variable stmt
                            Statement stmt = conn.createStatement();
                            //executes the sql statement
                            ResultSet rs = stmt.executeQuery(sql);
                            //reads the values for username from client
                            String username = in.readLine();
                            //reads the values for password from client
                            String password = in.readLine();
                            //while there is another row in the database
                            while ((rs.next()) && (!y)) {
                                //userID is assigned the value from the column USER_ID
                                userID = rs.getInt("USER_ID");
                                //dataUsername is assigned the value from the column USERNAME
                                String dataUsername = rs.getString("USERNAME");
                                //dataPassword is assigned the value from the column PASSWORD
                                String dataPassword = rs.getString("PASSWORD");
                                //if there is a match between the login details and values within the database the program will continue
                                if (dataUsername.equals(username) && dataPassword.equals(password)) {
                                    y = true;
                                    x = true;
                                    //creates and intialises new List data
                                    List<post> data = null;
                                    //passes pass back to the client
                                    out.println("pass");
                                    //passes userID back to the client
                                    out.println(userID);
                                    //cloes the resultset
                                    rs.close();
                                    //closes the database connection
                                    conn.close();
                                    //gets the data for the posts from the database
                                    out.println(new postResponse(getSubs(userID, data)));

                                } else {
                                    x = false;
                                }
                            }
                            //if the login details dont match any result in the database than the connection is closed
                        if (!x) {
                            conn.close();
                            clientSocket.close();
                        }
                        //if input equals unsubscribe
                        } else if ((y) && (input.equals("unsubscribe"))) {
                                //username is passed from the client and stored in user
                                String user = in.readLine();
                                //calls method unsubscribe
                                unsubscribe(userID, user);
                                List<post> data = null;
                                //passes the values of the list of posts using JSON
                                out.println(new postResponse(getSubs(userID, data)));
                        } else if ((y) && (input.equals("display"))) {
                            List<post> data = null;
                            //passes the values of the list of posts using JSON
                            out.println(new postResponse(getSubs(userID, data)));
                        } else if ((y) && (input.equals("new"))) {
                            PreparedStatement ps;
                            //creates connection to the database
                            conn = databaseConnection.connect();
                            //reads content of post from client and stores it in postContent
                            String postContent = in.readLine();
                            //reads userID of poster from client and stores it in userNam
                            String userNam = in.readLine();
                            if(!userNam.equals("display")) {
                                //converts userNam into an int
                                int user = Integer.parseInt(userNam);
                                //creates sql statement
                                String sql = "INSERT INTO POSTS(POST_CONTENT, USER_ID) VALUES(?,?)";
                                ps = conn.prepareStatement(sql);
                                //sets first ? to value of postContent
                                ps.setString(1, postContent);
                                //sets second ? to value of user
                                ps.setInt(2, user);
                                //executes statement
                                ps.executeUpdate();
                                List<post> data = null;
                                //passes the values of the list of posts using JSON
                                out.println(new postResponse(getSubs(userID, data)));
                            }
                            //closes database connection
                            conn.close();
                            //if login is valid and input equals subscribe
                        }else if((y) && (input.equals("subscribe"))){
                            PreparedStatement ps;
                            //establishes connection to database
                            conn = databaseConnection.connect();
                            //get the subscribee username from client
                            String sub = in.readLine();
                            //creates sql statement
                            String sql = "SELECT USER_ID FROM USERS WHERE USERNAME = ?";
                            ps = conn.prepareStatement(sql);
                            //sets first parameter to value of sub
                            ps.setString(1, sub);
                            ResultSet res = ps.executeQuery();
                            //stores the value of column USER_ID in followerID where USERNAME = sub
                            int followerID = res.getInt("USER_ID");
                            //closes resultset
                            res.close();
                            //creates new sql statement
                            String sql1 = "INSERT INTO SUBS(FOLLOWER_ID,FOLLOWEE_ID) VALUES(?,?)";
                            PreparedStatement ps1 = conn.prepareStatement(sql1);
                            //sets first parameter to userID
                            ps1.setInt(1, userID);
                            //sets second parameter to followerID
                            ps1.setInt(2, followerID);
                            ps1.executeUpdate();
                            List<post> data = null;
                            //passes the values of the list of posts using JSON
                            out.println(new postResponse(getSubs(userID, data)));
                            //closes databas connection
                            conn.close();
                            //if login is valid and input equals photo
                        }else if((y) && (input.equals("photo"))){
                            //stores the string byte array from client in encoded_content
                            String encoded_content = in.readLine();
                            if(!encoded_content.equals("display")) {
                                //establishes connection to the database
                                conn = databaseConnection.connect();
                                //converts the string byte array back to the byte array using base 64
                                byte[] raw = Base64.getDecoder().decode(encoded_content);
                                //creates sql statement
                                String sql = "INSERT INTO POSTS(USER_ID, FILE) VALUES(?,?)";
                                PreparedStatement ps = conn.prepareStatement(sql);
                                //sets first paramter to the value of userID
                                ps.setInt(1, userID);
                                //sets the second parameter to the value of raw
                                ps.setBytes(2, raw);
                                //executes statement
                                ps.executeUpdate();
                                List<post> data = null;
                                //passes the values of the list of posts using JSON
                                out.println(new postResponse(getSubs(userID, data)));
                                //closes connection to database
                                conn.close();
                            }else if((y) && (input.equals("logout"))){
                                clientSocket.close();
                            }
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    static List<post> getSubs(int userID, List<post> data){
        post Post;
        //creates new array data
        data = new ArrayList<>();
        //establishes connection to the database
        Connection conn = databaseConnection.connect();
        try {
            //creates arraylist subscriptions
            ArrayList<Integer> subscriptons = new ArrayList<Integer>();
            //creates sql statement
            String sql  = "SELECT * FROM SUBS WHERE FOLLOWER_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            //sets first parameter to the value of userID
            ps.setInt(1, userID);
            //executes statement
            ResultSet res = ps.executeQuery();

            while(res.next()) {
                //gets the value of follower_ID and stores it in the variable followerID
                int followerID = res.getInt("FOLLOWEE_ID");
                //stores the users the client is subscribed to in the arraylist subscriptions
                subscriptons.add(followerID);
            }
            //for all values within subscriptions
            for(int i = 0; i < subscriptons.size(); i++){
                //sql statement is created
                String sql2 = "SELECT * FROM POSTS WHERE USER_ID = ? ORDER BY POST_ID DESC";
                ps = conn.prepareStatement(sql2);
                //first parameter is the element at i in subscriptions
                ps.setInt(1, subscriptons.get(i));
                //executes statement
                res = ps.executeQuery();
                //while there is a next row
                while(res.next()){
                    String encoded_content = "";
                    //stores value in column POST_CONTENT in postText
                    String postText = res.getString("POST_CONTENT");
                    //stores value in column FILE in raw
                    byte[] raw = res.getBytes("FILE");
                    //if there is an entry in raw than it is converted to a string using base 64
                    if(raw != null) {
                        encoded_content = Base64.getEncoder().encodeToString(raw);
                    }
                    //a new post is creaed
                    Post = new post(postText, encoded_content, Integer.toString(subscriptons.get(i)));
                    //if postText does not equal null that sets text area to that value
                    if(postText != null) {
                        Post.setTextPost(postText);
                        //if raw is not null than it sets the image view to the byte array raw
                    }else if(raw != null){
                        Post.setImage(raw);
                    }
                    //creates sql statement
                    String sql3 = "SELECT USERNAME FROM USERS WHERE USER_ID = ?";
                    PreparedStatement ps1 = conn.prepareStatement(sql3);
                    //first parameter is the value of the element at i in subscriptions
                    ps1.setInt(1, subscriptons.get(i));
                    //executes statement
                    ResultSet res1 = ps1.executeQuery();
                    //sets the value of userName to the value within the USERNAME column
                    String userName = res1.getString("USERNAME");
                    //sets the label as the subscribees username
                    Post.setUserName(userName);
                    //adds post to the data list
                    data.add(Post);

                }
            }
            //closes the database connection
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                if(conn != null) {
                    conn.close();
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return data;
    }

    static void unsubscribe(int userID, String user) {
        Connection conn = databaseConnection.connect();
        try {
            String sql = "SELECT USER_ID FROM USERS WHERE USERNAME = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ResultSet res = ps.executeQuery();

            int followerID = res.getInt("USER_ID");

            //res.close();

            String sql2 = "DELETE FROM SUBS WHERE FOLLOWER_ID = ? AND FOLLOWEE_ID = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql2);
            ps1.setInt(1, userID);
            ps1.setInt(2, followerID);
            ps1.executeUpdate();

            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
