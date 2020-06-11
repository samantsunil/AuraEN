#!/bin/bash
brokerId=$1
dnsName="$(curl http://169.254.169.254/latest/meta-data/public-hostname)"
sudo sed -i 's/^\(broker.id=\).*/\1'$brokerId'/' /opt/kafka/kafka_2.11-2.4.0/config/server.properties
sudo sed -i 's/^\(listeners=PLAINTEXT:\/\/\).*/\1'$dnsName:9092'/' /opt/kafka/kafka_2.11-2.4.0/config/server.properties
#sudo sed -i 's/^\(zookeeper.connect=\).*/\1'$zkDns'/' /opt/kafka/kafka_2.11-2.4.0/config/server.properties
#config create topic  based on no of brokers for partitions and replication factor.

