#!/bin/bash

currtime=$(date +"%Y%m%d%H%M%S")
logLocation="/Users/pavanpkulkarni/Documents/workspace/KafkaFile_BatchProcessing/"
LogFile="${logLocation}/server-start-stop_${currtime}.log"


# ====== For IntelliJ ===========
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
printf "  Kill All Kafka Processes                   \n" >>$LogFile 2>&1
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
ps -ef | grep kafka | grep -v grep | awk '{print $2}' | xargs kill -9

printf "+-------------------------------------------+\n" >>$LogFile 2>&1
printf "  Clean Log Directory                        \n" >>$LogFile 2>&1
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
rm -rf /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/logs/*


printf "+-------------------------------------------+\n" >>$LogFile 2>&1
printf "  Start Zookeeper!!!!                        \n" >>$LogFile 2>&1
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
nohup zookeeper-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/zookeeper.properties > /dev/null 2>&1 &
sleep 5

printf "+-------------------------------------------+\n" >>$LogFile 2>&1
printf "  Start Broker 9092                         \n" >>$LogFile 2>&1
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
nohup kafka-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server-9092.properties > /dev/null 2>&1 &
sleep 5

printf "+-------------------------------------------+\n" >>$LogFile 2>&1
printf "  Start Broker 9093                          \n" >>$LogFile 2>&1
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
nohup kafka-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server-9093.properties > /dev/null 2>&1 &
sleep 5

printf "+-------------------------------------------+\n" >>$LogFile 2>&1
printf "  Start Broker 9094                          \n" >>$LogFile 2>&1
printf "+-------------------------------------------+\n" >>$LogFile 2>&1
nohup kafka-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server-9094.properties > /dev/null 2>&1 &
sleep 5

printf "+--------------------------------------------+\n" >>$LogFile 2>&1
printf "  Start SchemaRegistry                        \n" >>$LogFile 2>&1
printf "+--------------------------------------------+\n" >>$LogFile 2>&1
nohup schema-registry-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/schema-registry/schema-registry.properties > /dev/null 2>&1 &
sleep 5

printf "+--------------------------------------------+\n" >>$LogFile 2>&1
printf "  Start Control Center                        \n" >>$LogFile 2>&1
printf "+--------------------------------------------+\n" >>$LogFile 2>&1
nohup control-center-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/confluent-control-center/control-center.properties > /dev/null 2>&1 &
sleep 5

printf "+--------------------------------------------+\n" >>$LogFile 2>&1
printf "  Create Topics                               \n" >>$LogFile 2>&1
printf "+--------------------------------------------+\n" >>$LogFile 2>&1
kafka-topics --create --zookeeper localhost:2181 --replication-factor 3 --partitions 5 --topic tt >>$LogFile 2>&1

printf "+--------------------------------------------+\n" >>$LogFile 2>&1
printf "  List and Describe Create Topics             \n" >>$LogFile 2>&1
printf "+--------------------------------------------+\n" >>$LogFile 2>&1
kafka-topics --list --zookeeper localhost:2181 >>$LogFile 2>&1
kafka-topics --describe --zookeeper localhost:2181 --topic tt >>$LogFile 2>&1



# ====== For IntelliJ ===========


#zookeeper-server-stop /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/zookeeper.properties
#kafka-server-stop /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server.properties
#schema-registry-stop /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/schema-registry/schema-registry.properties
#zookeeper-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/zookeeper.properties
#kafka-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server-9092.properties
#kafka-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server-9093.properties
#kafka-server-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/kafka/server-9094.properties
#schema-registry-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/schema-registry/schema-registry.properties
#control-center-start /Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/etc/confluent-control-center/control-center.properties
#kafka-topics --create --zookeeper localhost:2181 --replication-factor 3 --partitions 5 --topic tt
#kafka-topics --describe --zookeeper localhost:2181 --topic tt
#kafka-topics --list --zookeeper localhost:2181

# ======= Running jar ============
#scala -cp build/libs/KafkaFile_BatchProcessing-1.0-SNAPSHOT.jar com.pavanpkulkarni.producer.ProducerApp tt src/main/resources/properties.config
#scala -cp build/libs/KafkaFile_BatchProcessing-1.0-SNAPSHOT.jar com.pavanpkulkarni.consumer.ConsumerApp tt src/main/resources/properties.config


