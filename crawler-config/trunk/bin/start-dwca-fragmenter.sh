#! /bin/bash

nohup java -Xms1G -Xmx1G -jar ../lib/crawler-cli.jar dwcafragmenter --log-config ../config/logback-crawler-dwca-fragmenter.xml --conf ../config/crawler-dwca-fragmenter.yaml &> ../logs/crawler-dwca-fragmenter_stdout.log &
