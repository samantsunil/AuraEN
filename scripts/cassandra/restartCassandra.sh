#!/bin/bash
echo "Restarting Cassandra service"
sudo service cassandra restart
sleep 20
nodetool repair
sleep 10
cd ~
