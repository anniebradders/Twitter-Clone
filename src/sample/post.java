package sample;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.Base64;

public class post {
    public String textPost;
    public String userName;
    public String encode;
    private static final String _class =
            post.class.getSimpleName();

    public String getTextPost() {
        return textPost;
    }

    public void setTextPost(String textPost) {
        this.textPost = textPost;
    }

    public String getUserName() {
        return userName;
    }

    public void setImage(byte[] image){
        String encoded_content = Base64.getEncoder().encodeToString(image);
        this.encode = encode;
    }

    public String getImage(){
        return encode;
    }

    public void setUserName(String userID) {
        this.userName = userID;
    }

    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("textPost", textPost);
        obj.put("userName", userName);
        obj.put("encode", encode);
        return obj;
    }

    public static post fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject) val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize message fields (checking timestamp for null)
            String textArea = (String) obj.get("textPost");
            String userName = (String) obj.get("userName");
            String encode = (String) obj.get("encode");
            // construct the object to return (checking for nulls)
            return new post(textArea, encode, userName);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    public post(String textArea, String encode,  String userName) {
        this.textPost = textArea;
        this.encode = encode;
        this.userName = userName;
    }
}
