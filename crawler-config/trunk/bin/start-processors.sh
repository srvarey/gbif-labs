#! /bin/bash
./start-processor-fragment.sh
./start-processor-verbatim.sh
./start-processor-interpreted.sh
sleep 1
./start-processor-indexer.sh
sleep 1
