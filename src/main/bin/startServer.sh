#!/usr/bin/env bash

jar="authentication-service.jar"

nohup java -jar $jar > server.log &
