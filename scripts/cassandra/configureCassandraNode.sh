#!/bin/bash
seedIp=$1
publicIp="$(curl http://169.254.169.254/latest/meta-data/public-ipv4)"
privateIp="$(curl http://169.254.169.254/latest/meta-data/local-ipv4)"
echo $privateIp
sudo sed -i 's/^\(\s*-\s*seeds:\s\).*/\1'\"$privateIp\"'/' /etc/cassandra/cassandra.yaml
sudo sed -i 's/^\(listen_address:\s\).*/\1'$privateIp'/' /etc/cassandra/cassandra.yaml
sudo sed -i 's/^\(broadcast_rpc_address:\s\).*/\1'$publicIp'/' /etc/cassandra/cassandra.yaml
