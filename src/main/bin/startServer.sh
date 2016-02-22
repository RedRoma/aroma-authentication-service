#!/usr/bin/env bash

version='1.2-SNAPSHOT'
jar="aroma-authentication-service-$version.jar"

nohup java -jar $jar > server.log &
