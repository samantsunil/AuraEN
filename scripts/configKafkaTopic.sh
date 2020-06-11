#!/bin/bash
partitions_old=$1
partitions_new=$2
replication_old=$3
replication_new=$4
sudo sed -i 's/--replication-factor '$replication_old'/--replication-factor '$replication_new'/' /home/ubuntu/createTopic.sh
sudo sed -i 's/--partitions '$partitions_old'/--partitions '$partitions_new'/' /home/ubuntu/createTopic.sh