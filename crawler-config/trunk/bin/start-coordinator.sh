#! /bin/bash

#nohup java -jar ../lib/crawler-cli.jar coordinator --log-config ../config/logback-crawler-coordinator.xml --conf ../config/crawler-coordinator.yaml &> ../logs/crawler-coordinator_stdout.log &

# With debugging turned on:
nohup java -Xms256M -Xmx256M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar ../lib/crawler-cli.jar coordinator --log-config ../config/logback-crawler-coordinator.xml --conf ../config/crawler-coordinator.yaml &> ../logs/crawler-coordinator_stdout.log &
