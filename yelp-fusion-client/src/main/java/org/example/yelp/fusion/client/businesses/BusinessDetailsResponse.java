package org.example.yelp.fusion.client.businesses;


import org.example.elasticsearch.client._types.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;

import java.util.function.*;

@JsonpDeserializable
public class BusinessDetailsResponse extends WriteResponseBase {

    public int total;

    public Business[] businesses;

    public Object region;

    public BusinessDetailsResponse() {
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

    private BusinessDetailsResponse(Builder builder) {
        super(builder);

    }

    public static BusinessDetailsResponse of(Function<Builder, ObjectBuilder<BusinessDetailsResponse>> fn) {
        return fn.apply(new Builder()).build();
    }


    public static class Builder extends WriteResponseBase.AbstractBuilder<Builder>
            implements
            ObjectBuilder<BusinessDetailsResponse> {
        @Override
        protected Builder self() {
            return this;
        }

        public BusinessDetailsResponse build() {
            _checkSingleUse();

            return new BusinessDetailsResponse(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static final JsonpDeserializer<BusinessDetailsResponse> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Builder::new,
            BusinessDetailsResponse::setupIndexResponseDeserializer);

    protected static void setupIndexResponseDeserializer(ObjectDeserializer<BusinessDetailsResponse.Builder> op) {
        WriteResponseBase.setupWriteResponseBaseDeserializer(op);

    }


}
