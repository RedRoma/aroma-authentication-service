Authentication Service
==============================================

[<img src="https://raw.githubusercontent.com/RedRoma/Aroma/develop/Graphics/Logo.png" width="300">](https://github.com/RedRoma/Aroma)

[![Build Status](http://jenkins.redroma.tech/view/Aroma/job/Authentication%20Service/badge/icon)](http://jenkins.redroma.tech/view/Aroma/job/Authentication%20Service/)

The Authentication Service specializes in the Creation and Verification of Tokens.
This includes Application Tokens and User Tokens.


# Building

This project builds with maven. Just run a `mvn clean install` to compile and install to your local maven repository.


## Requirements
+ Java 8
+ Maven

# Release Notes
## 1.1
This 1.1 release persists token to a Cassandra Table.

## 1.0
The 1.0 is a pre-release of the Authentication Service. The interface and main operations have been fleshed out,
but designed with only in-memory persistence. A Durable Persistent store will be the focus of the upcoming release.
