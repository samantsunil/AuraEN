 #!/bin/bash
cd /opt/kafka/bin/
sudo bash kafka-server-stop.sh
sudo killall -9 java

cd ~
sudo bash clear_kafka_logs.sh
sleep 10
cd /opt/kafka/bin/
sudo bash kafka-server-start.sh ../config/server.properties &
#sudo bash kafka-server-start.sh ../config/server-1.properties &
#sudo bash kafka-server-start.sh ../config/server-2.properties &
cd ~
