#! /bin/bash

nohup java -Xms256M -Xmx256M -jar ../lib/crawler-cli.jar coordinatorcleanup --log-config ../config/logback-crawler-coordinator-cleanup.xml --conf ../config/crawler-coordinator-cleanup.yaml &> ../logs/crawler-coordinator-cleanup_stdout.log &
