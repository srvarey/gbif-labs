#! /bin/bash
nohup java -Xms128M -Xmx128M -jar ../lib/occurrence-cli.jar registry-change-listener --log-config ../config/logback-registry-change.xml --conf ../config/registry-change.yaml &> ../logs/registry-change_stdout.log &
