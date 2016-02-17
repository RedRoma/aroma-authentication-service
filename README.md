Authentication Service
==============================================

[<img src="https://raw.githubusercontent.com/RedRoma/banana/develop/Graphics/Logo.png" width="300">](https://github.com/RedRoma/banana)

[![Build Status](http://jenkins.sirwellington.tech/view/Banana/job/Authentication%20Service/badge/icon)](http://jenkins.sirwellington.tech/view/Banana/job/Authentication%20Service/)

The Authentication Service specializes in the Creation and Verification of Tokens.
This includes Application Tokens and User Tokens.


# Building

This project builds with maven. Just run a `mvn clean install` to compile and install to your local maven repository.


## Requirements
+ Java 8
+ Maven

# Release Notes

## 1.0
The 1.0 is a pre-release of the Authentication Service. The interface and main operations have been fleshed out,
but designed with only in-memory persistence. A Durable Persistent store will be the focus of the upcoming release.
