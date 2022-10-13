package com.example.ll_restclient;


import com.example.client._types.*;

public interface ResponseListener {

    void onSuccess(Response response);


    void onFailure(Exception exception);
}
