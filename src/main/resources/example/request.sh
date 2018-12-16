#!/bin/bash

curl -X POST \
  http://127.0.0.1:4567/accounts/transfer \
  -H 'Content-Type: application/json' \
  -d '{
	"from" : 1,
	"to" : 2,
	"amount" : 5000.99
}'