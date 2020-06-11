#!/bin/bash
echo "clearing kafka logs"
cd /var/lib/kafka/data/
sudo rm -r *
cd /opt/kafka/
sudo rm -rf kafka-log*
cd ~
sudo rm -rf /tmp/kafka*
