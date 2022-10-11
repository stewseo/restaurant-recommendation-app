## Restaurant Recommendation App

- Load json file with all Yelp categories.
- Testing the Response body of each restaurants sub-category. 
- Verify that data is clean before searching business details by id.

## real-time data 10/10/2022. all restaurants in sf by sub category
![yelp_businesses_sf](https://user-images.githubusercontent.com/54422342/194987591-2679c0f1-3e7a-4204-9695-a03b3aefaf04.jpg)


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



