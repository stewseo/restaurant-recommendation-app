package com.example.client.yelp_fusion.model;


import com.example.client._types.*;

public interface ResponseListener {

    void onSuccess(Response response);


    void onFailure(Exception exception);
}
