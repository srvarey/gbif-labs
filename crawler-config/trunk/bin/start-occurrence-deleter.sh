#! /bin/bash
nohup java -Xms128M -Xmx128M -jar ../lib/occurrence-cli.jar occurrence-deleter --log-config ../config/logback-occurrence-deleter.xml --conf ../config/occurrence-deleter.yaml &> ../logs/occurrence_deleter_stdout.log &
