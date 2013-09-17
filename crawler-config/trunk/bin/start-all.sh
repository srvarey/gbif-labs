#!/bin/bash
./start-maps.sh
sleep 2
./start-cubes.sh
sleep 1
./start-processors.sh
sleep 2
./start-crawler.sh
sleep 1
