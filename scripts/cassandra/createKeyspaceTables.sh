#!/bin/bash
hostIp=`ifconfig eth0 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1`
cd /usr/bin/
sudo ./cqlsh $hostIp -f '/home/ubuntu/createcqlcommands.txt'
cd ~
