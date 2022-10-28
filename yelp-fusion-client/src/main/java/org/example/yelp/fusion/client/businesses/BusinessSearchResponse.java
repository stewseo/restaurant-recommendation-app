package org.example.yelp.fusion.client.businesses;


import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.json.jackson.*;
import org.example.lowlevel.restclient.*;

import java.io.*;
import java.util.*;

//@JsonDeserialize(using = BusinessSearchResponse.BusinessSearchResponseDeserializer.class)
public class BusinessSearchResponse {
    public int total;
    public String error;

    public List<Business_> businesses;

    public Region region;

    public int total() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String error() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Business_> businesses() {
        return businesses;
    }

    public void setBusinesses(List<Business_> businesses) {
        this.businesses = businesses;
    }

    public Region region() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public BusinessSearchResponse() {

    }


    public static class Region {
        public Double latitude;
        public Double longitude;

        public Object center;

        public Region() {
        }

        public Object center() {
            return center;
        }

        public void setCenter(Object center) {
            this.center = center;
        }

        public Double latitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double longitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }


    public static class BusinessSearchResponseDeserializer extends JsonDeserializer<BusinessSearchResponse> {

        @Override
        public BusinessSearchResponse deserialize(com.fasterxml.jackson.core.JsonParser jp, DeserializationContext ctx) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);

            PrintUtils.green("field names: " + node.fieldNames());
            PrintUtils.green("size: " + node.size());

            BusinessSearchResponse res = new BusinessSearchResponse();

            if (node.get("total") != null) {
                res.setTotal(node.get("total").asInt());
            }

            PrintUtils.green("field names: " + node.fieldNames());

            JsonNode businessesNode = node.get("businesses");

            PrintUtils.green("businessesNode size = " + businessesNode.size() + " ctx = " + ctx);

            if (businessesNode.isObject()) {
                PrintUtils.green("businessesNode is object: " + businessesNode.size());
            }

            if (businessesNode.isArray()) {
                PrintUtils.green("businessesNode is array: " + businessesNode.size());
            }

            return res;
        }

    }
}



