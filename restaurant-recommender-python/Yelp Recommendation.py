# # Recommendation Engine for Yelp Restaurants

# ## Authors: Dannie Vo and Stew Seo

import numpy as np
import pandas as pd
import json
from elasticsearch import Elasticsearch


# ### Connecting to Elasticsearch:
client = Elasticsearch(cloud_id=CLOUD_ID,basic_auth=("elastic", Elastic_pw))
client.info()

resp = client.search(index='yelp-businesses-restaurants-sf', query={"match_all": {}},size=10000)
total_restaurants = resp['hits']['total']['value']
total_restaurants
df = pd.DataFrame()
for i in range(total_restaurants):
    df = df.append(pd.json_normalize(resp['hits']['hits'][i]['_source']))
df = df.reset_index(drop=True)
df.head()


# ### Data Cleaning:
df.columns
df[['location.cross_streets', 'location.country',
       'location.address3', 'location.address2', 'location.city',
       'location.address1', 'location.display_address', 'location.state',
       'location.zip_code']].head(3)
df['categories'][1]
df['categories'] = pd.Series([pd.DataFrame(df['categories'][i])['title'].tolist() for i in range(len(df))])
df['transactions'] = df['transactions'].apply(lambda x: ', '.join(map(str, x)))
df['location.display_address'] = df['location.display_address'].apply(lambda x: ''.join(map(str, x)))
df.head()