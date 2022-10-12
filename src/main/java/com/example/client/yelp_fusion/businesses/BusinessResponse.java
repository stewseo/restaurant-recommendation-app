package com.example.client.yelp_fusion.businesses;

import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.util.*;

import java.util.function.*;

@JsonpDeserializable
public class BusinessResponse extends WriteResponseBase {

    public int total;

    public Business[] businesses;

    public Object region;

    public BusinessResponse() {
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

    private BusinessResponse(Builder builder) {
        super(builder);

    }

    public static BusinessResponse of(Function<Builder, ObjectBuilder<BusinessResponse>> fn) {
        return fn.apply(new Builder()).build();
    }


    public static class Builder extends WriteResponseBase.AbstractBuilder<Builder>
            implements
            ObjectBuilder<BusinessResponse> {
        @Override
        protected Builder self() {
            return this;
        }

        public BusinessResponse build() {
            _checkSingleUse();

            return new BusinessResponse(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static final JsonpDeserializer<BusinessResponse> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Builder::new,
            BusinessResponse::setupIndexResponseDeserializer);

    protected static void setupIndexResponseDeserializer(ObjectDeserializer<BusinessResponse.Builder> op) {
        WriteResponseBase.setupWriteResponseBaseDeserializer(op);

    }


}
