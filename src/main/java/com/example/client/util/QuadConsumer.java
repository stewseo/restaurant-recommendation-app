package com.example.client.util;

@FunctionalInterface
public interface QuadConsumer<T, U, V, X> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @param x the fourth input argument
     */
    void accept(T t, U u, V v, X x);
}