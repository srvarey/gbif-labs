#! /bin/bash
java -Xms1G -Xmx1G -jar ../lib/occurrence-cli.jar delete-occurrence --conf ../config/occurrence-deleter.yaml --occurrence-key-file $1
