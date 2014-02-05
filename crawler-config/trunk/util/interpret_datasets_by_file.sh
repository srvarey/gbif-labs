#! /bin/bash
java -Xms1G -Xmx1G -jar ../lib/occurrence-cli.jar interpret-dataset --conf ../config/occurrence-mutator.yaml --dataset-uuid-file $1
