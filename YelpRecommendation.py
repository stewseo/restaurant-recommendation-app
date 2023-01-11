#!/usr/bin/env python
# coding: utf-8

# # Recommendation Engine for Yelp Restaurants in San Francisco - CA

# ## Authors: Dannie Vo and Stew Seo

import numpy as np
import pandas as pd
import json
from elasticsearch import Elasticsearch
from jproperties import Properties
import matplotlib.pyplot as plt
from IPython.display import display, Image
get_ipython().run_line_magic('matplotlib', 'inline')
import plotly.express as px
import plotly.graph_objects as go


# ### Connecting to Elasticsearch:

def create_df_from_ES_index(yelp_ES_config_path,ES_index_name):
    # Connect to Elasticsearch using Cloud_id and password stored in properties file
    configs = Properties()
    with open(yelp_ES_config_path, 'rb') as config_file:
        configs.load(config_file)
    client = Elasticsearch(cloud_id=configs.get('Cloud_ID').data,basic_auth=("elastic", configs.get('Elastic_pw').data))
    # client.info()

    # Get the first patch of document search
    resp = client.search(index = ES_index_name, query={"match_all": {}}, size = 10000)
    hits_meta = resp['hits']['hits']

    # Get all documents 
    # Work for all cases when there are more than 10000 restaurants in that city/state
    while resp['hits']['total']['relation'] == 'gte':
        max_ts = max([i['_source']['timestamp'] for i in resp['hits']['hits']])
        resp = client.search(index = ES_index_name, query={"range": {"timestamp":{"gte":max_ts}}}, size = 10000)
        hits_meta.extend(resp['hits']['hits'])
        
    total_restaurants = len(hits_meta)
    # Create a dataframe with all source info for all restaurants in San Francisco
    df = pd.DataFrame()
    for i in range(total_restaurants):
        df = df.append(pd.json_normalize(hits_meta[i]['_source']))
    df = df.reset_index(drop=True)
    return df


# Create dataframe after connecting to Elasticsearch given the index name
df = create_df_from_ES_index('/Users/dannie/Documents/Projects/restaurant-recommendation-system/Yelp_ES_config.properties','yelp-fusion-restaurants-sf')


# ### Data Cleaning:

def data_cleaning(df, city):
    #On the df,price for $$$$ restaurants are not showing =>re-assign
    df.loc[df['price']=='$$$$','price'] = '$$$$'
    #Re-assign restaurants with NA price to Unknown
    df.loc[df['price'].isna(),'price'] = 'Unknown'
    #Remove restaurant with empty categories
    df = df[df['categories'].apply(len) != 0]
    # Convert the list of dictionaries with alias and titles into the list of all categories that the restaurant has using the title values
    df['categories'] = df['categories'].apply(lambda x: pd.DataFrame.from_dict(x)['title'].to_list())
    # Remove the mismatched transactions to only include options for delivery, pickup, or restaurant reservation
    df['transactions'] = df['transactions'].apply(lambda y: list(filter(lambda x: x in ['delivery','pickup','restaurant_reservation'], y)))
    # Convert the list of address dispplayed into a string
    df['location.display_address'] = df['location.display_address'].apply(lambda x: ', '.join(map(str, x)))
    # Drop some un-used columns  - some columns has specific values to when the doc was indexed on Elasticsearch
    df = df.drop(['hours','phone','is_closed','timestamp', 'messaging.url', 'messaging.use_case_text'], axis=1)
    # Convert different transaction categories into columns and join it with the original dataframe
    transaction_df = pd.get_dummies(df['transactions'].explode()).groupby(level=0).sum()
    df = df.join(transaction_df)
    # Only get the restaurants with price '$','$$','$$$', or'$$$$'
    df = df[df.price.isin(['$','$$','$$$','$$$$','Unknown'])]
    # For the restaurants with nan state (location), extract the state from the display_address
    df.loc[df['location.state'].isna(),'location.state'] = df[df['location.state'].isna()]['location.display_address'].str.split(', ').apply(lambda x: x[-1].split(' ')[0])
    # Only get the restaurants within the city
    df = df[df['location.city'] == city]
    #  Only get the restaurants that has the zipcode with number(digits) pattern
    df = df[df['location.zip_code'].str.isdigit()]
    return df


df = data_cleaning(df,'San Francisco')


# ### Data Exploration:

def show_image(name):
    restaurant = df[df.name == name].reset_index(drop=True)
    for i in range(len(restaurant)):
        print('Restaurant:',name,'\t Location:',restaurant.loc[i,'location.display_address'])
        [display(Image(pics)) for pics in restaurant.loc[i,'photos']]

    


# #### Show the restaurant's main images on Yelp

show_image("Brenda's French Soul Food")


show_image("Tartine Bakery")


# #### Top 5 restaurants with the most reviews:

df.sort_values(by='review_count',ascending=False).head()[['name','review_count','categories','rating','display_phone','price','location.display_address','delivery','pickup']]


df.groupby('rating').name.agg(['count']).reset_index(drop=False)


# #### Total restaurants by Yelp ratings:

fig = px.bar(df.groupby('rating').name.agg(['count']).reset_index(drop=False)
, x="rating", y="count", color="rating", text_auto=True, title = "Total Restaurants by Yelp Ratings")
fig.update_layout(xaxis_title = "Rating",yaxis_title = "Total Restaurants")
fig.show()


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

fig = px.scatter_mapbox(df, lat="coordinates.latitude", lon="coordinates.longitude", color="location.zip_code",
                  zoom=11, hover_name  = "name", hover_data = ['rating','review_count','transactions','categories'])
fig.update_layout(mapbox_style="open-street-map")
fig.update_layout(margin={"r":0,"t":0,"l":0,"b":0})
fig.update_layout(title = 'SF Restaurant Locations')
fig.show()


# #### Total restaurants breakdown by location (zipcode) and Yelp rating scale:

df.groupby(['location.zip_code','rating']).name.agg(['count']).reset_index(drop=False)


fig = px.sunburst(df.groupby(['location.zip_code','rating']).name.agg(['count']).reset_index(drop=False), path=['location.zip_code','rating'], values='count', title = 'Total Restaurants by Zipcode and Rating')
fig.show()


# #### Restaurants by Zipcode with Delivery/Pickup service:

delivery_pickup = df.groupby('location.zip_code').agg({'delivery':'sum','pickup':'sum'}).reset_index(drop=False)


delivery_pickup[delivery_pickup['location.zip_code'].isin(['94110','94103','94102','94109','94133'])]


fig = px.bar(delivery_pickup[delivery_pickup['location.zip_code'].isin(['94110','94103','94102','94109','94133'])],
             x=['delivery','pickup'], y="location.zip_code", orientation='h', barmode='group',title='Total Restaurants by Delivery/Pickup Service for Top 5 Zipcodes With The Most Restaurants', 
            labels = {'location.zip_code':'Zipcode','variable':'Type','value':'Total Restaurants'})
fig.show()


# #### Restaurants by Price Range/Rating:

rest_by_price = df.groupby('price').name.agg(['count']).reset_index(drop=False).sort_values(by='count',ascending=False)


fig = px.treemap(rest_by_price, path=['price'], values='count', title = 'Total Restaurants by Price')
fig.update_traces(root_color="lightgrey")
fig.show()

