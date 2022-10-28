### Restaurant Recommendation System using Data extracted from https://www.yelp.com/developers/documentation/v3/business through Elasticsearch


#### Data Extraction example: Get all fields from the Yelp Fusion Business Details Endpoint



### Data Cleaning

<pre>
POST yelp-businesses-restaurants-sf/_update_by_query?conflicts=proceed
{
  "query": {
    "bool": {
      "should": [
        {
          "exists": {
            "field": "messaging.url"
          }
        },
        {
          "exists": {
            "field": "special_hours"
          }
        }
      ]
    }
  },
  "script": {
    "source": "ctx._source.remove('messaging.url'); ctx._source.remove('special_hours');",
    "lang": "painless"
  }
}
</pre>

![308834780_631296281762118_2177015024900733153_n](https://user-images.githubusercontent.com/54422342/197595491-48fc484e-b677-4797-848e-14c30757270d.png)

### Data Processing
- add fields to documents: timestamp, geo_location
- configuring Filebeat: https://github.com/stewseo/filebeat-demo


### Collecting system and service metrics
- https://github.com/stewseo/metricbeat-demo
  


