This implementation relies on local CB with the following resources:

bucket name: acme
username: admin
password: adminadmin

The security user should have access to respective bucket.


- To add a new document

curl -X POST \
-H "Content-Type: application/json" \
-d "{ \"id\": \"2\", \"name\": \"Cristiano\", \"address\": \"NeueRoß\", \"interests\": [\"music\"] }" \
http://localhost:8083/users

All documents have expire time of 10 seconds.

- To get a document by id

curl -X GET http://localhost:8083/users/1