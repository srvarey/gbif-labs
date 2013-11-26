#! /bin/bash
nohup java -Xms256M -Xmx256M -jar ../lib/occurrence-cli.jar update-occurrence-index --conf ../config/processor-indexer-single.yaml --log-config ../config/logback-processor-indexer-single.xml &> ../logs/processor-indexer-single_stdout.log &
