#! /bin/bash
java -Xms128M -Xmx128M -jar ../lib/crawler-cli.jar startcrawl --conf ../config/crawler-schedule.yaml --dataset-uuid $1
