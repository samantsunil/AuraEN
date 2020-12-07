#!/bin/bash
brokerDns=$1
cassandraSeedIp=$2
shift 2
for i in $*; do
echo "$i" >>/opt/spark/spark-2.4.5-bin-hadoop2.6/conf/slaves
done
round() {
  printf "%.${2}f" "${1}"
}
temp="$(awk '/MemTotal/ {print $2/1024}' /proc/meminfo)"
mem="$(round ${temp} 0)"
driverMem=0
if [ $mem -le 1024 ]
then
	driverMem=512
elif [ $mem -le 2048 ]
then
	driverMem=1024
else
	driverMem=2048
fi
dnsName="$(curl http://169.254.169.254/latest/meta-data/public-hostname)"
sudo sed -i 's/^\(--driver-memory\s\).*/\1'$driverMem'm \\/' /home/ubuntu/runSparkAppCluster.sh
sudo sed -i 's/^\(--master\sspark:\/\/\).*/\1'$dnsName:7077' \\/' /home/ubuntu/runSparkAppCluster.sh
sudo sed -i 's/^\(com.iot.app.kafka.brokerlist=\).*/\1'$brokerDns'/' /home/ubuntu/iot-spark.properties
sudo sed -i 's/^\(com.iot.app.cassandra.host=\).*/\1'$cassandraSeedIp'/' /home/ubuntu/iot-spark.properties
sudo sed -i 's/^\(spark.driver.host\s\).*/\1'$dnsName'/' /opt/spark/spark-2.4.5-bin-hadoop2.6/conf/spark-defaults.conf
sudo sed -i 's/^\(spark.driver.bindAddress\s\).*/\1'$dnsName'/' /opt/spark/spark-2.4.5-bin-hadoop2.6/conf/spark-defaults.conf
sudo sed -i 's/^\(export\s\SPARK_PUBLIC_DNS=\).*/\1'$dnsName'/' /opt/spark/spark-2.4.5-bin-hadoop2.6/conf/spark-env.sh
