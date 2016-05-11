#!/usr/bin/env bash

version='1.2'
jar="aroma-authentication-service-$version.jar"

nohup java -jar $jar > server.log &
