## Provide business insights based on data-driven decisions


### Get all restaurants in 10,000 meter radius of specified latitude and longitude(SF twin peaks) by category, attribute, and term:
- curl -H "Authorization: Bearer $YELP_API_KEY" GET https://api.yelp.com/v3/businesses/search?location=SF&term=restaurants&longitude=-122.4477&latitude=37.7516&radius=10000&sort_by=distance <br/>
  - Add a JSON document to the specified data stream or index and makes it searchable
    - Index restaurants by term, location, coordinates, radius and category
      - index business details https://www.yelp.com/developers/documentation/v3/business
    - Index all yelp categories to search and visualize
      - https://www.yelp.com/developers/documentation/v3/category_list
        
![most_popular_yelp_categories](https://user-images.githubusercontent.com/54422342/193863560-83920614-5029-49f9-9ea5-fa541c9bf262.jpg)  

### Configure inputs and output of data shippers
- Configure Filebeat inputs
  - An input is responsible for managing the harvesters and finding all sources to read from.
  - Filestream finds all files on the drive that match the defined glob paths and starts a harvester for each file. 
    - Each input runs in its own Go routine.

### Manipulate Data
- Sanitize
  - Data Sanitization involves the secure and permanent erasure of sensitive data from datasets and media to guarantee that no residual data can be recovered even
  through extensive forensic analysis
- Normalize
  - Data normalization is the organization of data to appear similar across all records and fields. 
- Transform
  - Data transformation is the process of changing the format, structure, or values of data.
- Enrich 
  - add data from your existing indices to incoming documents during ingest



