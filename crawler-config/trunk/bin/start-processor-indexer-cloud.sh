#! /bin/bash
nohup java -Xms256M -Xmx256M -jar ../lib/occurrence-cli.jar update-occurrence-index --conf ../config/processor-indexer-cloud.yaml --log-config ../config/logback-processor-indexer-cloud.xml &> ../logs/processor-indexer-cloud_stdout.log &
