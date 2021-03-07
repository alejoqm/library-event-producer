import csv
import json
import requests

url = 'http://127.0.0.1:4420/v1/libraryEvent'
i = 1;

with open('books.csv', newline='') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
    for row in spamreader:
        i = i + 1
        body = { "id": i, "book": { "author": row[1],  "name": row[0]}}
        requests.post(url, json=body)