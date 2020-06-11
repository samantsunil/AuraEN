#!/bin/bash
nodetool decommission
sleep 90
sudo service cassandra stop
sleep 30

