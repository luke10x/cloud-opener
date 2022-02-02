#!/bin/bash

docker run --rm -v "${PWD}/website/lib/generated:/local" openapitools/openapi-generator-cli generate \
    -i https://raw.githubusercontent.com/luke10x/cloud-opener/main/backend-api/src/main/resources/cloudopener-openapi-schema.yaml \
    -g typescript-fetch	 \
    -o /local/typescript-fetch-client 

# list of more generators:
# https://openapi-generator.tech/docs/generators/

