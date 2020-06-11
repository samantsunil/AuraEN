#!/bin/bash

cd /opt/apache-zookeeper-3.6.1-bin/bin
sudo bash zkCli.sh <<EOF
get /brokers/topics/vehicle-data-stream
deleteall /brokers/topics/vehicle-data-stream
deleteall /admin/delete_topics/vehicle-data-stream
quit
EOF
cd ~
