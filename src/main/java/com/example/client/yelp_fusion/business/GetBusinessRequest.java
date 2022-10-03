package com.example.client.yelp_fusion.business;


import com.example.client._types.*;
import com.example.client._types.RequestBase;

// return businesses
public class GetPipelineRequest extends RequestBase {

    private final String id;


    private final Time masterTimeout;


    private final Boolean summary;