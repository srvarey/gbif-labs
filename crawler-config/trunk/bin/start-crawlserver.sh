#! /bin/bash

nohup java -Xms2G -Xmx2G -jar ../lib/crawler-cli.jar crawlserver --log-config ../config/logback-crawler-crawlserver.xml --conf ../config/crawler-crawlserver.yaml &> ../logs/crawler-crawlserver_stdout.log &
