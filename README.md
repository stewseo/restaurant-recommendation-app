## Restaurant Recommendation App

- Create a map using categories.json file
  - https://blog.yelp.com/businesses/yelp_category_list/
- Search all 192 categories that share parent: "restaurants"
- Create a document for each business: yelp-businesses-<category>-<business id>
- Create test visualization panels to verify that indices, documents, fields and data types match what's specified.
![yelp-businesses-restaurants-sf-categories_all](https://user-images.githubusercontent.com/54422342/195233219-20dac4c7-0149-42ad-acad-1147a4220664.jpg)

- Provide insights on restaurant's ratings, reviews, and more through different visualizations


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

