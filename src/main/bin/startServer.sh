#!/usr/bin/env bash

version='1.1-SNAPSHOT'
jar="banana-authentication-service-$version.jar"

nohup java -jar $jar > server.log &
