#! /bin/bash

nohup java -Xms1G -Xmx1G -jar ../lib/crawler-cli.jar downloader --log-config ../config/logback-crawler-dwca-downloader.xml --conf ../config/crawler-dwca-downloader.yaml &> ../logs/crawler-dwca-downloader_stdout.log &
