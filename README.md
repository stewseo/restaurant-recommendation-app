## Yelp-Fusion Java Api Client

### Goals:
- Initialize a Java Client that provides strongly typed requests and responses for Yelp Fusion's business, event, and category APIs.
- Provide features based on the [Elasticsearch Java API Client]("https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html/")

### Features:
- Strongly typed requests and responses for Yelp Fusion's business, event, and category APIs.
- Blocking and asynchronous versions of Yelp Fusion APIs.
- Use of fluent builders and functional patterns to create complex nested structures.
- Integration of application classes by using an object mapper such as Jackson or any JSON-B implementation.
- Delegate  protocol handling to an HttpClient
- Build api objects, prepare a Request, and perform a Request.
- Specified paths being monitored to:
  - Forward data to an Elasticsearch output
  - Collect metrics
    - operating system
    - services running on the server
    - Kibana instances
    - Elasticsearch
  - Ship collected metrics and statistics to the specified output
    - Elasticsearch  
 
### Problem: Can't decide where to eat dinner
### Get all restaurants in 10,000 meter radius of specified latitude and longitude(SF twin peaks):
- request using cURL 
- curl -H "Authorization: Bearer $YELP_API_KEY" GET https://api.yelp.com/v3/businesses/search?location=SF&term=restaurants&longitude=-122.4477&latitude=37.7516&radius=10000&sort_by=distance <br/>
  - HTTP method: GET
  - yelp fusion api base url: https//api.yelp.com
  - endpoint on the server: v3/businesses/search
  - request parameters: location, longitude, latitude, radius, sort_by, term
      

### Initializing a YelpFusionClient

- Create the http client
  - RestClient restClient = RestClient.builder(
      new HttpHost(host, port, protocol)).build();
      
- Create the transport with a Jackson mapper
  - YelpFusionTransport transport = new RestClientTransport(
      restClient, new JacksonJsonpMapper());

- Create the API client    
  - YelpFusionClient client = new YelpFusionClient(transport);
