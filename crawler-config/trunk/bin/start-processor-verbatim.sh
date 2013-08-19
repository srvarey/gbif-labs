#! /bin/bash
nohup java -Xms2G -Xmx2G -jar ../lib/occurrence-cli.jar verbatim-processor --log-config ../config/logback-processor-verbatim.xml --conf ../config/processor-verbatim.yaml &> ../logs/processor-verbatim_stdout.log &
