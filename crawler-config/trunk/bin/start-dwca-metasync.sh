#! /bin/bash

nohup java -Xms256M -Xmx256M -jar ../lib/crawler-cli.jar dwca-metasync --log-config ../config/logback-crawler-dwca-metasync.xml --conf ../config/crawler-dwca-metasync.yaml &> ../logs/crawler-dwca-metasync_stdout.log &
