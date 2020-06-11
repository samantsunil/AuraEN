#!/bin/bash

cd /opt/kafka/kafka_2.11-2.4.0/bin/
sudo bash kafka-topics.sh --zookeeper  ec2-3-24-240-124.ap-southeast-2.compute.amazonaws.com:2181 --delete --topic vehicle-data-stream
cd ~
