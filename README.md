### Api Client for Yelp Fusion Endpoints

#### Create a RestClient with HttpHost
```
RestClientBuilder restBuilder = RestClient.builder(new HttpHost("https://api.yelp.com")); 
Header[] defaultHeaders = {new BasicHeader("Authorization",  "Bearer " + token)};
restBuilder.setDefaultHeaders(defaultHeaders).build();
```

#### Create Transport with a Jackson mapper
```
       YelpRestTransport yelpTransport;

        try (RestClient restClient = restClientBuilder.build()) {
            // initialize Transport with a rest client and object mapper
            yelpTransport = new YelpRestTransport(
                    restClient,
                    mapper);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            
```

#### Create a client to build Yelp Fusion API objects
```
YelpFusionClient yelpClient = new YelpFusionClient(yelpTransport);

```


#### Build Api Objects
```

            BusinessSearchResponse<Business> resp = yelpClient.search(s -> s
                                        .location("nyc")
                                        .term("restaurants")
                                        .limit(50)
                                        .offset(fOffset)
                                        .price("4")
                                        .sort_by("review_count")
                                        .categories(c -> c
                                                .alias("pizza")), 
                                Business.class);
```

#### Indexed restaurant and categories
```
static final int indexedCategoriesNycRestaurants = 309; // total sub categories of "restaurants" in new york city
static final int indexedRestaurantsNyc = 17773; // total restaurants in database for NYC
static final int indexedRestaurantsSF = 4410;   // total restaurants in SF
static final int indexedCategoriesSFRestaurants = 244; // total sub categories of "restaurants" in San Francisco
```


### Data Processing
- add fields to documents: timestamp, geo_location
- configuring Filebeat: https://github.com/stewseo/filebeat-demo


### Collecting system and service metrics
- https://github.com/stewseo/metricbeat-demo
  


