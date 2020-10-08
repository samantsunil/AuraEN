 #!/bin/bash
cd /opt/zookeeper/bin/
sudo bash zkServer.sh stop
cd ~
sudo ./clear_zookeeper_logs.sh
sleep 10
cd /opt/zookeeper/bin/
sudo bash zkServer.sh start ../conf/zoo.cfg &
cd ~
