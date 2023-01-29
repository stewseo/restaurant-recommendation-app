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
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity


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
    return df.reset_index(drop=True)


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


# ### Building the Recommendation Engine:

# This function returns the updated dataframe with the name of restaurants and the final content
# that is the combination of many conditions that we want the recommendation system to consider
#Final content column is cleaned properly for the pre-prosessing step before building the recommendation
def create_final_content(similar_conditions):
    data = df.copy()
    # New mapping for price
    data['price'] = data['price'].map({'$':'$','$$':'2$','$$$':'3$','$$$$':'4$','Unknown':'unknown'})
    # Update transaction to exclude special character & space (only _ in this case)
    # Then join them together into 1 big string
    data['transactions']= data['transactions'].apply(lambda x: ' '.join([i.replace('_','') for i in x]))
    # Update review_count with new group bucket by review count bins
    group_bin = [1, 100, 200, 400, 600, 800, 1000, 2000, np.inf]
    group_name = ['group1','group2','group3','group4','group5','group6','group7','group8']
    data['review_count'] = pd.cut(data.review_count, bins = group_bin, labels = group_name, include_lowest = True)
    # Update ratings with new rating group bin
    data['rating'] = pd.cut(data['rating'], bins = [1,2,3,4,5,np.inf], labels = ['r1','r2','r3','r4','r5'], include_lowest = True,right=False)
    # Update categories to be all lower cases and remove space
    # Then join them together into 1 big string
    data['categories'] = data['categories'].apply(lambda x: ' '.join([i.lower().replace(' ','') for i in x]))
    # Create a new column by combining the values for categories, review count, rating, transactions, price
    #Different conditions based on whether the we have 1 single condition (string format) or multiple conditions (list)
    data['final_content'] = data[similar_conditions].apply(lambda x: ' '.join(x.astype(str)), axis=1) if isinstance(similar_conditions,list) else data[similar_conditions]
    # Drop duplicates for restaurants based on name and contents that we want to compare the restaurant with
    data = data[['name','final_content']].drop_duplicates().reset_index(drop=True)
    return data


def build_Yelp_recommendation_system(similar_conditions, restaurant_name, top_n):
    # Create the update df with the final content column based on similar conditions
    data = create_final_content(similar_conditions)
    #  Create the count sparse matrix for the content column and remove stop words in case there are extra unnecessary strings
    cnt_matrix = CountVectorizer(stop_words='english').fit_transform(data['final_content'])
    # Calculate cosine similarity score
    cosine_sim = cosine_similarity(cnt_matrix)
    # Get the chosen restaurant's index based on name
    restaurant_ind = data[data.name == restaurant_name].index[0]
    # Get the list of restaurant index and its cosine similarity score as tuple 
    similar_list = list(enumerate(cosine_sim[restaurant_ind]))
    # Sort this  list based on cosine similarity score from high to low 
    highest_similar_list = sorted(similar_list,key=lambda x:x[1],reverse=True)
    # There are cases where there are extremely good matches that the recommended restaurant has same cosine similarity score with the chosen restaurant (=1)
    # Because the list is sorted based on the cosine similarities score and not restaurant name, it could have different index instead of being the first index
    # Therefore we will exclude that restaurant name  out of the list and get the top n recommended restaurant
    #highest_similar_list.remove((restaurant_ind, 1.0))
    highest_similar_list = [item for item in highest_similar_list if item[0]!= restaurant_ind]
    
    print('If the restaurant name shows up in the recommended list more than once, it means that while the restaurant has the same name, it has different conditions listed compared to the other location.')
    print('Below is a list of top',top_n, 'recommended restaurants based on',', '.join(similar_conditions) if isinstance(similar_conditions,list) else similar_conditions, 'compared to',restaurant_name+':')
    [print(data.name.loc[i[0]]) for i in highest_similar_list[:top_n]]
    


# #### Build recommendation system to return the list of recommended restaurants based on your chosen restaurant name and one or more restaurants fields and the size of the top recommended list:
# #### Please choose one or more of these fields: price, categories, rating, review_count, transactions
# 

build_Yelp_recommendation_system(['price','categories','rating','review_count'], 'San Tung',10)


build_Yelp_recommendation_system('categories','Arsicault',10)

