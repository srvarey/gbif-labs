#! /bin/bash

nohup java -Xms512M -Xmx512M -jar ../lib/metrics-cli.jar OccurrenceDatasetCountryCube --log-config ../config/logback-cube-dataset-country.xml --conf ../config/cube-dataset-country.yaml &> ../logs/cube-dataset-country_stdout.log &
