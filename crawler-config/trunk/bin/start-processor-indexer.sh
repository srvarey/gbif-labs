#! /bin/bash
nohup java -Xms256M -Xmx256M -jar ../lib/occurrence-cli.jar update-occurrence-index --conf ../config/processor-indexer.yaml --log-config ../config/logback-processor-indexer.xml &> ../logs/processor-indexer_stdout.log &
