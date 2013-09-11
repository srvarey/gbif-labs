#! /bin/bash
nohup java -Xms1G -Xmx1G -jar ../lib/occurrence-cli.jar occurrence-dataset-mutator --log-config ../config/logback-occurrence-mutator.xml --conf ../config/occurrence-mutator.yaml &> ../logs/occurrence_mutator_stdout.log &
