package com.example.client.yelp_fusion.business;


import com.example.client._types.*;
import com.example.client._types.RequestBase;
import com.example.client.yelp_fusion.category.*;

// return businesses
public class GetBusinessRequest extends RequestBase {
    Terms[] term;
    Attributes[] attributes;
    Categories[] categories;

    Coordinates coordinates;
}
