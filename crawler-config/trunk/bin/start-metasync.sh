#! /bin/bash

# TODO: adding logback config stops startup, don't know why
nohup java -Xms256M -Xmx256M -jar ../lib/crawler-cli.jar metasync --conf ../config/crawler-xml-metasync.yaml &> ../logs/crawler-xml-metasync_stdout.log &
