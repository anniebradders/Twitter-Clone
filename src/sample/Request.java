package sample;

import org.json.simple.JSONAware;
import org.json.simple.*;
public abstract class Request implements JSONAware{
        public abstract Object toJSON();

        public String toJSONString(){
            return toJSON().toString();
        }

        public String toString() { return toJSONString(); }

}
