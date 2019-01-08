# Postman Core Library

[![Build Status](https://travis-ci.com/qbicsoftware/postman-core-lib.svg?branch=development)](https://travis-ci.com/qbicsoftware/postman-core-lib)[![Code Coverage]( https://codecov.io/gh/qbicsoftware/postman-core-lib/branch/development/graph/badge.svg)](https://codecov.io/gh/qbicsoftware/postman-core-lib)

Postman Core Library, version 1.0.0-SNAPSHOT - Provides functionality for the download of OpenBIS files and datasets

## Description
The Postman Core Library provides functionality for the download, filtering and finding of OpenBIS datasets.
Four main classes are provided:   
PostmanDataDownloader(V3) for dataset downloading   
PostmanDataFilterer for dataset filtering    
PostmanDataFinder for dataset finding    
PostmanDataStreamProvider for dataset stream providing    

## How to Install
`git clone https://github.com/qbicsoftware/postman-core-lib`
`mvn clean install`    
to create a jar with dependencies included:
`mvn clean package`

## Development
### Tests
The tests are divided into regular unit tests testing class functionality and integration tests which connect to openBIS and access a database. To run the unit tests use `mvn test`.     
Integration tests require a config file in the resources folder called 'qbicPropertiesFile.conf' : [PropertiesStub](src/main/resources/qbicPropertiesFileStub.conf). Fill in the blanks and run `mvn integration-test`. This may take a while to complete.

### Structure
Keeping and enforcing a modular structure of the main dataloading classes is highly encouraged. Moreover, unit tests and integration tests are also highly encouraged, likely even mandatory.    
New Integration tests have to end their class name with `IT`. Moreover, if a login into openBIS is required they should extend `SuperPostmanSessionSetupManagerForIntegrationTests`. This ensures that a login is performed successfully. Moreover, all dataloading objects are already provided and multiple useful functions for testing such as counting all files in a directory are provided as well.

## Author
Created by Lukas Heumos (lukas.heumos@student.uni-tuebingen.de).
