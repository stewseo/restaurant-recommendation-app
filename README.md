### restaurant-recommender-java provides a java-client to
- Build yelp fusion request objects
- Handle http transport using the java 11 http client
- Parse json results from the response body
- Map json data to objects
- Create and store documents in Elasticsearch


#### Build a request, parsing the response, and mapping to a Business object

```
int docsCount = yelpClient.getDocsCount(index);

Map<String, Set<String>> categoriesMap = elasticSearch.getCategoriesMap(indexNyc, maxResults, timestamp);

for(String category: categoriesMap.values()
        .stream()
        .flatMap(Set::stream).collect(Collectors.toSet())) {

BusinessSearchResponse<Business> businessSearchResponse = yelpClient.search(s -> s
                .location("nyc")
                .term("restaurants")
                .categories(c -> c
                        .alias(category))
                .sort_by(sort_by)
                .offset(aOffset)
                .limit(limit)
        , Business.class);


if (businessSearchResponse != null && businessSearchResponse.error() == null) {
```

### restaurant-recommender-python