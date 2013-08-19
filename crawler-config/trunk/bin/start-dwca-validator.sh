#! /bin/bash
# NOTE: -Xmx should be threadcount * 1G to allow for worst case of 2M * 500byte triplets being held in memory
nohup java -jar -Xms1G -Xmx1G ../lib/crawler-cli.jar validator --log-config ../config/logback-crawler-dwca-validator.xml --conf ../config/crawler-dwca-validator.yaml &> ../logs/crawler-dwca-validator_stdout.log &
