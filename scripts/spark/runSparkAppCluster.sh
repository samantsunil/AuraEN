#!/bin/bash
echo "Starting stream processing of data streams"
#sudo killall -9 java
sleep 30
cd /opt/spark/spark-2.4.5-bin-hadoop2.6/bin
sudo ./spark-submit \
--driver-memory 2g \
--master spark://136.186.108.185:7077
--total-executor-cores 4 \
--executor-memory 1g \
--class "com.iot.app.spark.processor.IoTDataProcessor" \
/home/ubuntu/iot-spark-processing/target/iot-spark-processor-1.0.0.jar &
cd ~
