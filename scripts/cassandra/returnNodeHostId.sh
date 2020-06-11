#!/bin/bash
tempId=`/usr/bin/cqlsh -e "select host_id from system.local;"`
hostId="$(echo $tempId | awk '{ print $3 }')"
echo $hostId
cd ~

