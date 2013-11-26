#! /bin/bash
./start-processor-fragment.sh
./start-processor-verbatim.sh
./start-processor-interpreted.sh
sleep 1
./start-processor-indexer-single.sh
./start-processor-indexer-cloud.sh
sleep 1
./start-occurrence-deleter.sh
./start-occurrence-mutator.sh
./start-registry-listener.sh
sleep 1
