import pandas as pd

# Reading the first CSV file
authors_df = pd.read_csv('authors.csv')

# Reading the second CSV file
papers_df = pd.read_csv('papers.csv')

# Displaying the first few rows of each dataframe to verify
print("First DataFrame:")
print(authors_df.head())

print("\nSecond DataFrame:")
print(papers_df.head())

print(authors_df.shape)
print(papers_df.shape)

# Dropping rows with any null values in the first dataframe
authors_df = authors_df.dropna()

# Dropping rows with any null values in the second dataframe
papers_df = papers_df.dropna()

print(authors_df.shape)
print(papers_df.shape)

print('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')
# paratirithike oti ta source_id telika den einai unique opos tha eprepe
is_id_unique = papers_df['source_id'].is_unique
print(is_id_unique)

# kratame ta 'source_id' pou emfanizonte mono mia fora sto papers_df
unique_source_id = papers_df['source_id'].value_counts()[papers_df['source_id'].value_counts() == 1].index
print(unique_source_id)
unique_rows = papers_df[papers_df['source_id'].isin(unique_source_id)]
print(unique_rows)

# elegxo tora an einai ontos unique
is_id_unique = unique_rows['source_id'].is_unique
print(is_id_unique)
print('|___________________________|')

# vazo se mia colona olokliro to onoma tou sigrafea
authors_df['full_name'] = authors_df['first_name'] + ' ' + authors_df['last_name']
authors_df.drop(columns=['first_name', 'last_name'], inplace=True)
print(authors_df)

# kano inner join, gia na paro mono ta koina shmeia ton 2 dataframes me vasi to 'source_id'
merged_df = pd.merge(authors_df, unique_rows, on='source_id', how='inner')
print(merged_df)
print(merged_df.shape)

# omadopoio me vasi to 'source_id'
grouped_df = merged_df.groupby('source_id').agg({
    'full_name': lambda x: ', '.join(x),
    'institution': lambda x: ', '.join(x),
    'year': 'first',
    'title': 'first',
    'abstract': 'first',
    'full_text': 'first',
}).reset_index()
print(grouped_df)
# grouped_df_2 = merged_df.groupby('source_id')
# for name, group in grouped_df_2:
#     print(f"\nGroup name: {name}")
#     print(group['full_name'] + " " + group['institution'])
#     break

# einai pano apo 200 papers ara imaste ok = 909
#grouped_df = grouped_df[grouped_df['source_id'] < 3410]
print(grouped_df.shape)

# ftiaxno to csv axreio gia na to xreisimopoihso sto java project tis lucene
grouped_df.to_csv('corpus.csv', index=False)
print('======================================================================')

# ###
# row_with_id_195 = papers_df[papers_df['source_id'] == 195]
# print(row_with_id_195)
# print(row_with_id_195['year'])
# print(row_with_id_195['title'])
# print(row_with_id_195['abstract'])
# print(row_with_id_195['full_text'])
# print('---------------')

# row_with_id_195 = authors_df[authors_df['first_name'] == "Miguel"]
# print(row_with_id_195)
# #134
# #445
# #347
# #627
# print('---------------++++++++++++++++++++=')
# row_with_id_195 = papers_df[papers_df['source_id'] == 134]
# print(row_with_id_195)
# print('++++++++++++++++++++=')
# row_with_id_195 = papers_df[papers_df['source_id'] == 445]
# print(row_with_id_195)
# print('++++++++++++++++++++=')
# row_with_id_195 = papers_df[papers_df['source_id'] == 347]
# print(row_with_id_195)
# print('++++++++++++++++++++=')
# row_with_id_195 = papers_df[papers_df['source_id'] == 627]
# print(row_with_id_195)
# print('++++++++++++++++++++=')

# unique_to_df1 = authors_df[~authors_df['source_id'].isin(papers_df['source_id'])]
# print(unique_to_df1)

# unique_to_df2 = papers_df[~papers_df['source_id'].isin(authors_df['source_id'])]
# print(unique_to_df2)
# ###
