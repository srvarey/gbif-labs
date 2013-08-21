#! /bin/bash
sleep 1
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z00 --zoom 0  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z0_stdout.log &
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z01 --zoom 1  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z1_stdout.log &
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z02 --zoom 2  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z2_stdout.log &
sleep 1
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z03 --zoom 3  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z3_stdout.log &
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z04 --zoom 4  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z4_stdout.log &
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z05 --zoom 5  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z5_stdout.log &
sleep 1
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z06 --zoom 6  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z6_stdout.log &
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z07 --zoom 7  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z7_stdout.log &
nohup java -Xms128M -Xmx128M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z08 --zoom 8  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z8_stdout.log &
sleep 1
nohup java -Xms256M -Xmx256M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z09 --zoom 9  --write-batch-size 25000 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z9_stdout.log &
nohup java -Xms256M -Xmx256M -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name density_map_z10 --zoom 10 --write-batch-size 25000 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z10_stdout.log &
sleep 1
