#! /bin/bash
for var in "$@"
do
	java -Xms64M -Xmx64M -jar ../lib/occurrence-cli.jar delete-occurrence --conf ../config/occurrence-deleter.yaml --occurrence-key $var
done
