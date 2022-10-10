package com.example.client.yelp_fusion.model;

import org.apache.http.client.methods.*;

import java.util.concurrent.*;

public abstract class TestCancelable {

    public abstract void cancel();

    abstract void runIfNotCancelled(Runnable runnable);

    static final TestCancelable NO_OP = new TestCancelable() {
        @Override
        public void cancel() {}

        @Override
        void runIfNotCancelled(Runnable runnable) {
            throw new UnsupportedOperationException();
        }
    };

    static TestCancelable fromRequest(HttpRequestBase httpRequest) {
        return new TestCancelable.TestRequestCancelable(httpRequest);
    }

    private static class TestRequestCancelable extends TestCancelable {

        private final HttpRequestBase httpRequest;

        private TestRequestCancelable(HttpRequestBase httpRequest) {
            this.httpRequest = httpRequest;
        }

        public synchronized void cancel() {
            this.httpRequest.abort();
        }


        synchronized void runIfNotCancelled(Runnable runnable) {
            if (this.httpRequest.isAborted()) {
                throw newCancellationException();
            }
            runnable.run();
        }
    }

    static CancellationException newCancellationException() {
        return new CancellationException("request was cancelled");
    }
}
