#! /bin/bash

nohup java -Xms4G -Xmx4G -XX:MaxPermSize=256m -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=60 -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=2 -XX:GCLogFileSize=128M -jar ../lib/metrics-cli.jar OccurrenceCube --log-config ../config/logback-cube-occurrence.xml --conf ../config/cube-occurrence.yaml &> ../logs/cube-occurrence_stdout.log &
