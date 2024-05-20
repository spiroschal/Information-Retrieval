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

###
is_id_unique = papers_df['source_id'].is_unique
print(is_id_unique)
duplicated_rows = papers_df[papers_df.duplicated('source_id', keep=False)]
print(duplicated_rows)
unique_source_id = papers_df['source_id'].value_counts()[papers_df['source_id'].value_counts() == 1].index
print(unique_source_id)
unique_rows = papers_df[papers_df['source_id'].isin(unique_source_id)]
print(unique_rows)
is_id_unique = unique_rows['source_id'].is_unique
print(is_id_unique)
print('---------------')
# vazo se mia colona olokliro to onoma tou sigrafea
authors_df['full_name'] = authors_df['first_name'] + ' ' + authors_df['last_name']
authors_df.drop(columns=['first_name', 'last_name'], inplace=True)
print(authors_df)
merged_df = pd.merge(authors_df, unique_rows, on='source_id', how='inner')
print(merged_df)
print(merged_df.shape)
grouped_df = merged_df.groupby('source_id').agg({
    'source_id': 'first',
    'full_name': lambda x: ', '.join(x),
    'institution': lambda x: ', '.join(x),
    'year': 'first',
    'title': 'first',
    'full_text': 'first',
}).reset_index()
print(grouped_df)
grouped_df_2 = merged_df.groupby('source_id')
for name, group in grouped_df_2:
    print(f"\nGroup name: {name}")
    print(group['full_name'] + " " + group['institution'])
    break

# einai pano apo 200 papers
grouped_df.to_csv('corpus.csv', index=False)
print('======================================================================')
row_with_id_195 = papers_df[papers_df['source_id'] == 195]
print(row_with_id_195)
print(row_with_id_195['year'])
print(row_with_id_195['title'])
print(row_with_id_195['abstract'])
print(row_with_id_195['full_text'])
print('---------------')

row_with_id_195 = authors_df[authors_df['first_name'] == "Miguel"]
print(row_with_id_195)
#134
#445
#347
#627
print('---------------++++++++++++++++++++=')
row_with_id_195 = papers_df[papers_df['source_id'] == 134]
print(row_with_id_195)
print('++++++++++++++++++++=')
row_with_id_195 = papers_df[papers_df['source_id'] == 445]
print(row_with_id_195)
print('++++++++++++++++++++=')
row_with_id_195 = papers_df[papers_df['source_id'] == 347]
print(row_with_id_195)
print('++++++++++++++++++++=')
row_with_id_195 = papers_df[papers_df['source_id'] == 627]
print(row_with_id_195)
print('++++++++++++++++++++=')

unique_to_df1 = authors_df[~authors_df['source_id'].isin(papers_df['source_id'])]
print(unique_to_df1)

unique_to_df2 = papers_df[~papers_df['source_id'].isin(authors_df['source_id'])]
print(unique_to_df2)
###

# Merging the dataframes on the 'id' column
merged_df = pd.merge(authors_df, papers_df, on='source_id', how='inner')
print(merged_df)
print(merged_df.shape)

merged_df = pd.merge(authors_df, papers_df, on='source_id', how='right')
print(merged_df)
print(merged_df.shape)

merged_df = pd.merge(authors_df, papers_df, on='source_id', how='left')
print(merged_df)
print(merged_df.shape)


# Creating the first dataframe
df1 = pd.DataFrame({
    'id': [1, 2, 2],
    'name': ['Alice', 'Bob', 'Charlie'],
    'age': [30, 25, 35]
})

# Creating the second dataframe
df2 = pd.DataFrame({
    'id': [1, 2, 4],
    'product': ['Book_1', 'Book_2', 'Book_3'],
    'price': [1200, 800, 400]
})

# Merging the dataframes on the 'id' column
merged_df = pd.merge(df1, df2, on='id', how='right')

# Printing the merged dataframe
print("Merged DataFrame (Inner Join):")
print(merged_df)