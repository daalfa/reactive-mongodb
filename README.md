[![Java CI with Maven](https://github.com/daalfa/reactive-mongodb/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/daalfa/reactive-mongodb/actions/workflows/maven.yml)

# Restaurant Matching Service
This is a simple restaurant search service with multiple search criteria.  
It is possible to use up to 5 criteria:  
`name` - filter restaurants containing the text  
`cuisine` - filter restaurants with cuisine containing the text  
`distance` - filter restaurants with same or shorted distance  
`rating` - filter restaurants with same or higher rating  
`price` - filter restaurants with same or lower price

This service will return up to 5 restaurants matching the filter.  
If no filter, the service will return the best match based on distance first, then rating, then price.

Links:
 * http://localhost:8080/v1/restaurants?name=delicious&price=20.0&rating=3.0&distance=4&cuisine=i
 * http://localhost:8080/webjars/swagger-ui/index.html

## Decisions
Based on the problem, it was assumed this is a read-intensive service with rare writes/updates.  
MongoDB was chosen because the Restaurant/Cuisine data can be easily Denormalized without too much redundancy.  
Denormalizating Cuisine into cuisine-names embedded in Restaurant is optional for reading performance.  
MongoDB as any noSQL database can be easily scaled horizontally allowing the service to be elastic.  

Since we should not use out of the box text search, a simple regex matcher is used for the text fields `name` and `cuisine`.  
This allows searching the expression not only as prefix or suffix, but in between words.  
Text index was used in the collection to speed up the text search operation.  

In `application.yaml` there is a property `application.initializeDatabase` to make the Service read data from the `csv` during startup.

The numeric fields are Double to allow fine-grained values in the future.  
There are validations for the numbers and strings (mostly sanitize regular expression symbols).

Finally, WebFlux is used to allow non-blocking operations with MongoDB and other calls.  
This makes the service to run more efficiently with less resources since it reuses threads.  
* Webflux don't automatically support document references, but this is not a problem since we denormalized cuisine. 
The reason for creating a collection for Cuisine is merely for future implementations where insert and validation can be easily done.

## Stack
The Service implements a reactive REST API with WebFlux Spring Boot application with back pressure support
and a noSQL MongoDB database.  
Java 21 experimental flag to enable Template Strings.  
MapStruct is used because it supports Java Records, while ModelMapper don't.  
TestContainer is used for quick MongoDB integration tests.

### API
The service have a single REST endpoint `/v1/restaurants` with query parameters for filtering and classifying results.

Example of full query:  
`/v1/restaurants?name=Tasty&price=15.0&rating=4.0&distance=2&cuisine=American`

Example of Response body:
```json
  {
    "cuisine": "Chinese",
    "name": "Main Street",
    "price": "17.5",
    "distance": "2.1",
    "rating": "4.5"
  }
```


### Database
There are two document collections (equivalent to tables in a relational database):  
* `restaurants`
* `cuisines`

Since `cuisine` is just a type it could be easily embedded in the `restaurant` collection.   
But it is better to manage cuisines and validate if a cuisine exists using document references (similar to foreign key in SQL).

Example of `cuisine` document:
```json
  {
    "_id": {"$oid": "66304a6a0d595177b2efad03"},
    "_class": "com.davi.demo.restaurant.service.model.Cuisine",
    "name": "chinese"
  }
```

Example of `restaurant` document:
```json
  {
    "_id": {"$oid": "66304aac0d595177b2efad08"},
    "_class": "com.davi.demo.restaurant.service.model.Restaurant",
    "cuisine": "chinese",
    "name": "Main Street",
    "price": "17.5",
    "distance": "2.1",
    "rating": "4.5"
  }
```
