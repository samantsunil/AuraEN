#!/bin/bash
echo "submitting streaming job to spark cluster..."
sudo killall -9 java
sleep 60
sudo bash clear_logs.sh
cd /opt/spark/spark-2.4.5-bin-hadoop2.6/bin
sudo ./spark-submit \
--driver-memory 512m
--class "com.iot.app.spark.processor.IoTDataProcessor" \
<path>/iot-spark-processor-1.0.0.jar &
cd ~
