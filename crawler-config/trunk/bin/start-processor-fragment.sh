#! /bin/bash
nohup java -Xms4G -Xmx4G -jar ../lib/occurrence-cli.jar fragment-processor --log-config ../config/logback-processor-fragment.xml --conf ../config/processor-fragment.yaml &> ../logs/processor-fragment_stdout.log &
