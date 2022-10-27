package org.example.lowlevel.restclient;


public interface ResponseListener {

    void onSuccess(Response response);


    void onFailure(Exception exception);
}
