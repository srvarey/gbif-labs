#! /bin/bash
for var in "$@"
do
	java -Xms64M -Xmx64M -jar ../lib/crawler-cli.jar startcrawl --conf ../config/crawler-schedule.yaml --dataset-uuid $var
done
