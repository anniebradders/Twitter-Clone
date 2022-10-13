package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class postResponse extends Request {

    private static final String _class =
            postResponse.class.getSimpleName();

    private List<post> data;

    public postResponse(List<post> data){
        if(data == null || data.contains(null)){
            throw new NullPointerException();
        }
        this.data = data;
    }

    List<post> getMessages(){ return data; }

    public Object toJSON() {
        // serialize messages into a JSONArray
        JSONArray arr = new JSONArray();
        for (post Post : data)
            arr.add(Post.toJSON());
        // serialize this as a JSONObject
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("messages", arr);
        return obj;
    }

    public static postResponse fromJSON(Object val){
        try{
            JSONObject obj = (JSONObject)val;

            if(!_class.equals(obj.get("_class")))
                return null;
            JSONArray arr = (JSONArray)obj.get("messages");
            List<post> data = new ArrayList<>();
            for(Object data_obj : arr)
                data.add(post.fromJSON(data_obj));
            return new postResponse(data);
        }catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
