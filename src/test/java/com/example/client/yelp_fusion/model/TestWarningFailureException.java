package com.example.client.yelp_fusion.model;


import com.example.client._types.*;

import java.io.*;

import static com.example.client._types.ResponseException.*;


public class TestWarningFailureException extends RuntimeException {
        private final com.example.client._types.Response response;

        public TestWarningFailureException(com.example.client._types.Response response) throws IOException {
            super(buildMessage(response));
            this.response = response;
        }


        TestWarningFailureException(TestWarningFailureException e) {
            super(e.getMessage(), e);
            this.response = e.getResponse();
        }


        public Response getResponse() {
            return response;
        }
    }

