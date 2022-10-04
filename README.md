## Provide business insights based on data-driven decisions


### Get all restaurants in 10,000 meter radius of specified latitude and longitude(SF twin peaks) by category, attribute, and term:
- curl -H "Authorization: Bearer $YELP_API_KEY" GET https://api.yelp.com/v3/businesses/search?location=SF&term=restaurants&longitude=-122.4477&latitude=37.7516&radius=10000&sort_by=distance <br/>
  - yelp fusion api base url: https//api.yelp.com
  - endpoint on the server: v3/businesses/search
  - HTTP method: GET
  - request parameters: 
    - location, longitude, latitude, radius, sort_by, term
  - if the response body contains greater than the maximum results per page
    - add offset parameter. Up to 20 iterations.
      - offset += limit
  - if the response body contains greater than the maximum total results 
    - add additional fields to filter the response.
      - categories=sushi,bbq,steak
  
- Configure Filebeat inputs
  - An input is responsible for managing the harvesters and finding all sources to read from.
  - Filestream finds all files on the drive that match the defined glob paths and starts a harvester for each file. 
    - Each input runs in its own Go routine.
- Parse log data into fields 
- Send data to Elasticsearch to store and search
- Add Json document to specified data stream. 
- Visualize data on Kibana
