#! /bin/bash
sleep 1
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z00 --zoom 0  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z0_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z01 --zoom 1  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z1_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z02 --zoom 2  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z2_stdout.log &
sleep 1
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z03 --zoom 3  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z3_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z04 --zoom 4  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z4_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z05 --zoom 5  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z5_stdout.log &
sleep 1
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z06 --zoom 6  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z6_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z07 --zoom 7  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z7_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z08 --zoom 8  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z8_stdout.log &
sleep 1
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z09 --zoom 9  --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z9_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z10 --zoom 10 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z10_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z11 --zoom 11 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z11_stdout.log &
sleep 1
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z12 --zoom 12 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z12_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z13 --zoom 13 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z13_stdout.log &
nohup java -Xms512M -Xmx512M -XX:MaxPermSize=128m -XX:+UseConcMarkSweepGC -jar ../lib/metrics-cli.jar DensityCube --messaging-queue-name maps_z14 --zoom 14 --conf ../config/maps.yaml --log-config ../config/logback-maps.xml &> ../logs/maps_z14_stdout.log &
sleep 1
