#!/bin/bash
count=$1
cd /opt/kafka/kafka_2.11-2.4.0/bin/
sudo bash kafka-topics.sh --alter --zookeeper ec2-3-xx-xxx-124.ap-southeast-2.compute.amazonaws.com:2181 --topic vehicle-data-stream --partitions $count
