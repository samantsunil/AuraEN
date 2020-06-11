#!/bin/bash
repFac=$1
cd /usr/bin/
sudo ./cqlsh -e "ALTER KEYSPACE VehicleDataKeySpace WITH replication = {'class':'SimpleStrategy', 'replication_factor':$repFac};"
cd ~
nodetool repair
