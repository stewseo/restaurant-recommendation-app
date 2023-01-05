#!/usr/bin/env python
# coding: utf-8

# # Recommendation Engine for Yelp Restaurants in New York

# ## Authors: Dannie Vo and Stew Seo

import numpy as np
import pandas as pd
import json
from elasticsearch import Elasticsearch
import matplotlib.pyplot as plt
from IPython.display import display, Image
get_ipython().run_line_magic('matplotlib', 'inline')


import plotly.express as px
import plotly.graph_objects as go
import os

# ### Connecting to Elasticsearch:
#Connect to Elasticsearch using Cloud_id and password
client = Elasticsearch(cloud_id= os.getenv("ES_Cloud_ID"),basic_auth=("elastic", os.getenv("ES_Cloud_PW")))
client.info()
#Get the first patch of document search
resp = client.search(index='yelp-fusion-businesses-restaurants-nyc', query={"match_all": {}}, size = 10000)
hits_meta = resp['hits']['hits']
#Get all documents 
while resp['hits']['total']['relation'] == 'gte':
    max_ts = max([i['_source']['timestamp'] for i in resp['hits']['hits']])
    resp = client.search(index='yelp-fusion-businesses-restaurants-nyc', query={"range": {"timestamp":{"gte":max_ts}}}, size = 10000)
    hits_meta.extend(resp['hits']['hits'])


total_restaurants = len(hits_meta)
#Create a dataframe with all source info for all restaurants in NYC
df = pd.DataFrame()
for i in range(total_restaurants):
    df = df.append(pd.json_normalize(hits_meta[i]['_source']))
df = df.reset_index(drop=True)


# ### Data Cleaning:

df.head()


#On the df,price for $$$$ restaurants are not showing =>re-assign
df.loc[df['price']=='$$$$','price'] = '$$$$'
#Remove rows with empty categories
df = df[df['categories'].apply(len) != 0]


df[['location.cross_streets', 'location.country',
       'location.address3', 'location.address2', 'location.city',
       'location.address1', 'location.display_address', 'location.state',
       'location.zip_code']].head(3)


df['categories'].apply(lambda x: pd.DataFrame.from_dict(x)['title'].to_list())


# Convert the list of dictionaries with alias and titles into the list of all categories that the restaurant has using the title values
df['categories'] = df['categories'].apply(lambda x: pd.DataFrame.from_dict(x)['title'].to_list())


# Remove the mismatched transactions to only include options for delivery, pickup, or restaurant reservation
df['transactions'] = df['transactions'].apply(lambda y: list(filter(lambda x: x in ['delivery','pickup','restaurant_reservation'], y)))
# Convert the list of address dispplayed into a string
df['location.display_address'] = df['location.display_address'].apply(lambda x: ', '.join(map(str, x)))


# Drop some un-used columns
df = df.drop(['distance', 'messaging.url', 'messaging.use_case_text','special_hours'], axis=1)


# Convert  different transaction categories into columns and join it with the original dataframe
transaction_df = pd.get_dummies(df['transactions'].explode()).groupby(level=0).sum()
df = df.join(transaction_df)


# Only get the restaurants with price '$','$$','$$$', or'$$$$'
df = df[df.price.isin(['$','$$','$$$','$$$$'])]


# For the restaurants with nan state (location), extract the state from the display_address
df.loc[df['location.state'].isna(),'location.state'] = df[df['location.state'].isna()]['location.display_address'].str.split(', ').apply(lambda x: x[-1].split(' ')[0])





# Only get the restaurants within the state of NY
df = df[df['location.state'] == 'NY']


# ### Data Exploration:

df[df.name.str.contains('Katz')]['name']


def show_image(name):
    restaurant = df[df.name == name].reset_index(drop=True)
    for i in range(len(restaurant)):
        print('Restaurant:',name,'\t Location:',restaurant.loc[i,'location.display_address'])
        [display(Image(pics)) for pics in restaurant.loc[i,'photos']]

    


# #### Show the restaurant's main images on Yelp

show_image("Lombardi's Pizza")


show_image("Katz's Delicatessen")


# #### Top 10 restaurants with the most reviews:

df.sort_values(by='review_count',ascending=False).head(10)[['name','review_count','categories','rating','display_phone','price','location.display_address','delivery','pickup']]


df.groupby('rating').name.agg(['count']).reset_index(drop=False)


# #### Total restaurants by Yelp ratings:

fig = px.bar(df.groupby('rating').name.agg(['count']).reset_index(drop=False)
, x="rating", y="count", color="rating", text_auto=True, title = "Total Restaurants by Yelp Ratings")
fig.update_layout(xaxis_title = "Rating",yaxis_title = "Total Restaurants")
fig.show()


df.price.value_counts()


# #### Calculating the total number of restaurants by categories

# Get the restaurant counts for all categories
# Note: 1 restaurant could have multiple categories
cat = df['categories'].apply(pd.Series).stack().reset_index(drop=True).value_counts()
cat = cat.to_frame(name='count').reset_index().rename(columns={'index':'category'})


fig = go.Figure(data=[go.Pie(labels=cat.head(10).category, values=cat.head(10)['count'],hole = 0.3,
                            pull = [0.2,0,0,0,0,0,0,0,0,0])])
fig.update_layout(title = 'Top 10 categories')
fig.show()


# #### All restaurants by location

fig = px.scatter_mapbox(df, lat="coordinates.latitude", lon="coordinates.longitude", color="location.city",
                  zoom=11, hover_name  = "name", hover_data = ['rating','review_count','transactions','categories'])
fig.update_layout(mapbox_style="open-street-map")
fig.update_layout(margin={"r":0,"t":0,"l":0,"b":0})
fig.update_layout(title = 'Restaurant Locations in NY')
fig.show()


# #### Total restaurants breakdown by location (city) and Yelp rating scale:

df.groupby(['location.city','rating']).name.agg(['count']).reset_index(drop=False)


fig = px.sunburst(df.groupby(['location.city','rating']).name.agg(['count']).reset_index(drop=False), path=['location.city','rating'], values='count', title = 'Total Restaurants by City and Rating')
fig.show()


# #### Restaurants in Cities with Delivery/Pickup service:

delivery_pickup = df.groupby('location.city').agg({'delivery':'sum','pickup':'sum'}).reset_index(drop=False)


delivery_pickup[delivery_pickup['location.city'].isin(['New York','Brooklyn','Bronx','Staten Island','Queens','Manhattan'])]


fig = px.bar(delivery_pickup[delivery_pickup['location.city'].isin(['New York','Brooklyn','Bronx','Staten Island','Queens','Manhattan'])],
             x=['delivery','pickup'], y="location.city", orientation='h', barmode='group',title='Total Restaurants by Delivery/Pickup Service for top Cities in NY', 
            labels = {'location.city':'City','variable':'Type','value':'Total Restaurants'})
fig.show()

