#! /bin/bash

nohup java -Xms512M -Xmx512M -jar ../lib/metrics-cli.jar OccurrenceDatasetTaxonCube --log-config ../config/logback-cube-dataset-taxon.xml --conf ../config/cube-dataset-taxon.yaml &> ../logs/cube-dataset-taxon_stdout.log &
