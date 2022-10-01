## Yelp-Fusion Java Api Client

### Goals:
- Provide features based on the Elasticsearch Java Api Client: 
  - Strongly typed requests and responses for Yelp Fusion's business, event, and category APIs.
  - Blocking and asynchronous versions of all APIs.
  - Use of fluent builders and functional patterns to allow writing concise yet readable code when creating complex nested structures.
  - Integration of application classes by using an object mapper such as Jackson or a JSON-B implementation.
  - Delegates protocol handling to an Apache HttpClient

### Tests for:
- createOrUpdate a json file containing up to 1000 search results using parameter(s):
  - offset
- createOrUpdate a json file containing the total search results
  - https://api.yelp.com/v3/businesses/search?location=SF&term=restaurants
    - total: 4300
 
- Monitor files located in a specified input path with Filebeat
- Forward data to be centralized to an Elasticsearch deployment
- Searching the results from any previous request

- Create the http client
  - RestClient restClient = RestClient.builder(
      new HttpHost(host, port, protocol)).build();
- Create the transport with a Jackson mapper
  - YelpFusionTransport transport = new RestClientTransport(
      restClient, new JacksonJsonpMapper());

- Create the API client    
  - YelpFusionClient client = new YelpFusionClient(transport);
