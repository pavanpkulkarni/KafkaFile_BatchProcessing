# Kafka File Processing 

## Scope (and extended Scope):

This program is to read a CSV file and write it to target location by applying a simple transformation on each record. In future projects I will integrate Kafka and Spark for more complex and heavy transformations. 

This project can also serve as point of reference in addressing several other funcationalties like:
1. Setting up a multi-broker Kafka cluster using script - [stop-start-scripts.sh](https://github.com/pavanpkulkarni/KafkaFile_BatchProcessing/blob/master/stop-start-scripts.sh)
2. Converting Avro type to .txt file using Kafka Consumer
3. Terminate Kafka Consumer if no data is polled for x seconds 
4. Enable WebUI monitioring for Kafka topics and brokers

## Prerequisites
1. Confluent Kafka 5.X.X
2. Scala 2.11
4. Gradle
3. Intellij/ Eclipse IDE

## Required changes to setup Multi-Broker Kafka Cluster [ localmode ]

#### Broker Setup
Create new `server.properties` files. One file for eack of the brokers.

    cp $KAFKA_HOME/etc/kafka/server.properties $KAFKA_HOME/etc/kafka/server-9092.properties   
    cp $KAFKA_HOME/etc/kafka/server.properties $KAFKA_HOME/etc/kafka/server-9093.properties   
    cp $KAFKA_HOME/etc/kafka/server.properties $KAFKA_HOME/etc/kafka/server-9094.properties 

Open `$KAFKA_HOME/etc/kafka/server-9092.properties` file and make the below changes
  
    broker.id=1  
    listeners=PLAINTEXT://:9092  
    log.dirs=$KAFKA_HOME/logs/kafka-logs-9092  

***N.B :*** If $KAFKA_HOME does not parse properly, you can add absolute path to `log.dirs`

Open `$KAFKA_HOME/etc/kafka/server-9093.properties` file and make the below changes

    broker.id=2  
    listeners=PLAINTEXT://:9093  
    log.dirs=$KAFKA_HOME/logs/kafka-logs-9093

Open `$KAFKA_HOME/etc/kafka/server-9094.properties` file and make the below changes

    broker.id=3  
    listeners=PLAINTEXT://:9094  
    log.dirs=$KAFKA_HOME/logs/kafka-logs-9094

#### Zookeeper Setup
Open `$KAFKA_HOME/etc/kafka/zookeeper.properties` file and make the below changes

    dataDir=$KAFKA_HOME/logs/zookeeper

#### Control Center Setup
Open `$KAFKA_HOME/etc/confluent-control-center/control-center.properties` file and make the below changes

    bootstrap.servers=localhost:9092,localhost:9093,localhost:9094
    confluent.controlcenter.data.dir=/Users/pavanpkulkarni/Documents/kafka/confluent-5.1.2/data/control-center

I'm using the community version of Confluent Kafka. That gives me 30 days trial period to use the Control Center. This project comes with a switch to turn off Kafka Producer and Consumer monitoring. To disable monitoring, make `enable.monitoring=false` in [properties.config](https://github.com/pavanpkulkarni/KafkaFile_BatchProcessing/blob/master/properties.config) file.


### To Run:

`cd` into project on all terminals you open.

1. Start zookeeper, brokers, schema-registry, control-center and create topics by running the [stop-start-scripts.sh](https://github.com/pavanpkulkarni/KafkaFile_BatchProcessing/blob/master/stop-start-scripts.sh) script
    
    Pavans-MacBook-Pro:KafkaFile_BatchProcessing pavanpkulkarni$ chmod +x stop-start-scripts.sh
    Pavans-MacBook-Pro:KafkaFile_BatchProcessing pavanpkulkarni$ ./stop-start-scripts.sh

2. Open a new tab to build the project  

    Pavans-MacBook-Pro:KafkaFile_BatchProcessing pavanpkulkarni$ gradle clean build

3. Open new tab for Consumer and run 

        scala -cp build/libs/KafkaFile_BatchProcessing-1.0-SNAPSHOT.jar com.pavanpkulkarni.consumer.ConsumerApp tt properties.config

    >general syntax : scala -cp build/libs/KafkaFile_BatchProcessing-1.0-SNAPSHOT.jar com.pavanpkulkarni.consumer.ConsumerApp <topic_name> <property_file_name>

4. Open new tab for Producer and run below command immediately

        scala -cp build/libs/KafkaFile_BatchProcessing-1.0-SNAPSHOT.jar com.pavanpkulkarni.producer.ProducerApp tt properties.config

    >general syntax : scala -cp build/libs/KafkaFile_BatchProcessing-1.0-SNAPSHOT.jar com.pavanpkulkarni.producer.ProducerApp <topic_name> <property_file_name> 

5. Go back to Consumer tab and check if you have received the messages from Producer.
6. Wait until counter reaches `10` for Consumer to terminate.
7. Check [output.txt](https://github.com/pavanpkulkarni/KafkaFile_BatchProcessing/blob/master/output.txt) file.

***N.B:*** We start Consumer before the Producer. This way we allow the Consumer to join th e `group.id` and be ready to accept data from Producer.

### Highlights on Avro 

[User.avsc](https://github.com/pavanpkulkarni/KafkaFile_BatchProcessing/blob/master/src/main/avro/com/pavanpkulkarni/schema/User.avsc) file defines the Avro schema for this project. I have used `com.zlad.gradle.avrohugger` plugin to generate Scala based class files for Avro schema. A special mention to `sourceFormat = SpecificRecord` in [build.gradle](https://github.com/pavanpkulkarni/KafkaFile_BatchProcessing/blob/master/build.gradle) which enables smooth conversion of .avsc file to Scala class. 

Run `gradle clean run` to generate Avro classes. Check `build/generated-src/avro` directory to view the sytem generated Avro classes. Visit [Gradle Plugins Page](https://plugins.gradle.org/plugin/com.zlad.gradle.avrohugger) to read more about this.
