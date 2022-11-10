package org.example.yelp.fusion.client.business;


import org.example.elasticsearch.client.json.*;

@JsonpDeserializable
public class Attributes {
    public Object[] attributes;

    public Attributes() {}

    public Object[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Object[] attributes) {
        this.attributes = attributes;
    }
}
