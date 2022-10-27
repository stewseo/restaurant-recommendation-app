## API
public API description

## Changelog
- change 1
- change 2

## Restaurant Recommendation App

- use case: 
- build data set
#### Get business details for each restaurant in San Francisco.
1. Data source: https://www.yelp.com/developers/documentation/v3/businesses/{id}

2. Create mappings for query parameters:
  - all yelp categories:  https://blog.yelp.com/businesses/yelp_category_list/
  - all yelp categories where "parents" = "restaurants"  (afghani, african, asian fusion, baguettes . . .)
  - distinct business ids: Set<String> setOfBusinessIds

3.  Build and perform a Request for each
  - "id" in setOfBusinessIds
  - BusinessResponse that contains a null error class field.
    - index: yelp-businesses-restaurants-sf
    - document id: yelp-businesses-restaurants-sf-{id}
![yelp-business-details-sf-restaurants](https://user-images.githubusercontent.com/54422342/195266154-0d005df5-9bdb-48f5-a354-4c57164acd63.jpg)

- Provide insights on restaurant's ratings, reviews, and more through different visualizations

### Searching for a restaurant by alias using the Kibana console
![console_tests](https://user-images.githubusercontent.com/54422342/195264846-55b812b5-437a-41b9-ad37-1aa23742fcd9.jpg)

### Ingestion Pipelines to manipulate data
- Sanitize
  - secure and permanent erasure of sensitive data from datasets and media to guarantee that no residual data can be recovered even
  through extensive forensic analysis
- Normalize
  - organize data to appear similar across all records and fields. 
- Transform
  - change the format, structure, or values of data.
- Enrich
  - add fields: timestamp and geo ip to each document.


### Forward and centralize log data
- https://github.com/stewseo/filebeat-demo

### Collect system and service metrics
- https://github.com/stewseo/metricbeat-demo


