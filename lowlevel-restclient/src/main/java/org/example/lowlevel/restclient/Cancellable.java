package org.example.lowlevel.restclient;


import org.apache.http.client.methods.*;

import java.util.concurrent.*;

public abstract class Cancellable {

    public abstract void cancel();

    abstract void runIfNotCancelled(Runnable runnable);

    static final Cancellable NO_OP = new Cancellable() {
        @Override
        public void cancel() {}

        @Override
        void runIfNotCancelled(Runnable runnable) {
            throw new UnsupportedOperationException();
        }
    };

    static Cancellable fromRequest(HttpRequestBase httpRequest) {
        return new Cancellable.TestRequestCancelable(httpRequest);
    }

    private static class TestRequestCancelable extends Cancellable {

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
