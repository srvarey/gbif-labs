#! /bin/bash

./start-fragmenter.sh
./start-crawlserver.sh
./start-metasync.sh
sleep 2
./start-dwca-fragmenter.sh
./start-dwca-metasync.sh
sleep 1
./start-dwca-validator.sh
./start-dwca-downloader.sh
sleep 2
./start-coordinator.sh
./start-coordinator-cleanup.sh
sleep 1
