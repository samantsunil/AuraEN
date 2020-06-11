#!/bin/bash
hostIP=$1
PID=`ps -eaf | grep java | grep -v grep | awk '{print $2}'`
sudo java -jar /home/ubuntu/jolokia-agent.jar start $PID --host $hostIP

