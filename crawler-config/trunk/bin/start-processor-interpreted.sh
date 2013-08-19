#! /bin/bash
nohup java -Xms3G -Xmx3G -jar ../lib/occurrence-cli.jar interpreted-processor --log-config ../config/logback-processor-interpreted.xml --conf ../config/processor-interpreted.yaml &> ../logs/processor-interpreted_stdout.log &
