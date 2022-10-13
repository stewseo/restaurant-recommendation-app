package com.example.ll_restclient;


import com.example.client._types.*;

import java.io.*;

import static com.example.client._types.ResponseException.*;


public class WarningFailureException extends RuntimeException {
        private final com.example.client._types.Response response;

        public WarningFailureException(com.example.client._types.Response response) throws IOException {
            super(buildMessage(response));
            this.response = response;
        }


        WarningFailureException(WarningFailureException e) {
            super(e.getMessage(), e);
            this.response = e.getResponse();
        }


        public Response getResponse() {
            return response;
        }
    }

