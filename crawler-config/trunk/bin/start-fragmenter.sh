#! /bin/bash

nohup java -Xms1G -Xmx1G -jar ../lib/crawler-cli.jar fragmenter --log-config ../config/logback-crawler-xml-fragmenter.xml --conf ../config/crawler-xml-fragmenter.yaml &> ../logs/crawler-xml-fragmenter_stdout.log &
