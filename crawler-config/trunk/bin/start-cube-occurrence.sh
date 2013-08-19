#! /bin/bash

nohup java -Xms512M -Xmx512M -jar ../lib/metrics-cli.jar OccurrenceCube --log-config ../config/logback-cube-occurrence.xml --conf ../config/cube-occurrence.yaml &> ../logs/cube-occurrence_stdout.log &
