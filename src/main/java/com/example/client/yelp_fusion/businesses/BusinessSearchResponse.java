package com.example.client.yelp_fusion.businesses;

import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.util.*;

import java.util.function.*;

@JsonpDeserializable
public class BusinessSearchResponse extends WriteResponseBase {

    public int total;

    public Business[] businesses;

    public Object region;

    public BusinessSearchResponse() {
        super();
    }

    public int getTotal() {
        return total;
    }


    public void setTotal(int total) {
        this.total = total;
    }

    public Business[] getBusinesses() {
        return businesses;
    }

    public void setBusinesses(Business[] businesses) {
        this.businesses = businesses;
    }

    public Object getRegion() {
        return region;
    }

    public void setRegion(Object region) {
        this.region = region;
    }

    private BusinessSearchResponse(Builder builder) {
        super(builder);

    }

    public static BusinessSearchResponse of(Function<Builder, ObjectBuilder<BusinessSearchResponse>> fn) {
        return fn.apply(new Builder()).build();
    }


    public static class Builder extends WriteResponseBase.AbstractBuilder<Builder>
            implements
            ObjectBuilder<BusinessSearchResponse> {
        @Override
        protected Builder self() {
            return this;
        }

        public BusinessSearchResponse build() {
            _checkSingleUse();

            return new BusinessSearchResponse(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static final JsonpDeserializer<BusinessSearchResponse> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Builder::new,
            BusinessSearchResponse::setupIndexResponseDeserializer);

    protected static void setupIndexResponseDeserializer(ObjectDeserializer<BusinessSearchResponse.Builder> op) {
        WriteResponseBase.setupWriteResponseBaseDeserializer(op);

    }


}
