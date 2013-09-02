#! /bin/bash

nohup java -Xms1G -Xmx1G -jar ../lib/metrics-cli.jar OccurrenceCube --log-config ../config/logback-cube-occurrence.xml --conf ../config/cube-occurrence.yaml &> ../logs/cube-occurrence_stdout.log &
