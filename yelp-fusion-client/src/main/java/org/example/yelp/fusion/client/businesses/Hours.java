package org.example.yelp.fusion.client.businesses;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.databind.jsontype.*;

import java.io.*;

@JsonDeserialize(using = Hours.HoursDeserializer.class)
public class Hours {
    public Object[] open;
    public String hours_type;
    public boolean is_open_now;

    public Hours() {
    }

    public Object[] open() {
        return open;
    }

    public void setOpen(Object[] open) {
        this.open = open;
    }

    public String hours_type() {
        return hours_type;
    }

    public void setHours_type(String hours_type) {
        this.hours_type = hours_type;
    }

    public boolean is_open_now() {
        return is_open_now;
    }

    public void setIs_open_now(boolean is_open_now) {
        this.is_open_now = is_open_now;
    }

    public static class HoursDeserializer extends JsonDeserializer<Hours> {

        @Override
        public Hours deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return null;
        }


    }

}
