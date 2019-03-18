package com.pavanpkulkarni.consumer



import java.io.{BufferedWriter, File, FileWriter}
import java.util.Properties
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Collections
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.errors.TimeoutException
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Logger
import com.pavanpkulkarni.schema.User

class KafkaFileConsumer(val topic: String, val propertyFile: String) {

  val logger = Logger.getLogger( getClass.getName )
  var propertyFileName = ConfigFactory.parseFile( new File(propertyFile) )
  val propertyConfs = ConfigFactory.load( propertyFileName )

  logger.info( "Property Details : " + propertyConfs.getConfig( "kafkaJobProperties" ) )

  private val props = new Properties()
  props.put("bootstrap.servers", propertyConfs.getString("kafkaJobProperties.kafkaCommon.bootstrap.servers"))
  props.put("schema.registry.url", propertyConfs.getString("kafkaJobProperties.kafkaCommon.schema.registry.url"))
  props.put("key.deserializer", classOf[StringDeserializer].getCanonicalName)
  props.put("value.deserializer", classOf[KafkaAvroDeserializer].getCanonicalName)
  props.put("group.id", propertyConfs.getString("kafkaJobProperties.consumerProperties.group.id"))
  props.put("enable.auto.commit", propertyConfs.getString("kafkaJobProperties.consumerProperties.enable.auto.commit"))
  props.put("auto.commit.interval.ms", propertyConfs.getString("kafkaJobProperties.consumerProperties.auto.commit.interval.ms"))
  props.put("session.timeout.ms", propertyConfs.getString("kafkaJobProperties.consumerProperties.session.timeout.ms"))
  props.put("consumer.timeout.ms", propertyConfs.getString("kafkaJobProperties.consumerProperties.consumer.timeout.ms"))
  props.put("specific.avro.reader", propertyConfs.getString("kafkaJobProperties.consumerProperties.specific.avro.reader"))
  props.put("auto.offset.reset", propertyConfs.getString("kafkaJobProperties.consumerProperties.auto.offset.reset"))

  if(propertyConfs.getString("kafkaJobProperties.kafkaCommon.enable.monitoring").equalsIgnoreCase("yes"))
    props.put("interceptor.classes", propertyConfs.getString("kafkaJobProperties.consumerProperties.interceptor.classes") )


  private val consumer = new KafkaConsumer[String, User](props)

  def start() = {

    val outFilename = propertyConfs.getString("kafkaJobProperties.kafkaCommon.output.filename")
    val writer = new BufferedWriter(new FileWriter(outFilename, true))
    var counter = 0
    var keepRunning = true

    try{

      consumer.subscribe(Collections.singletonList(topic))
      val pollDuration =  java.time.Duration.ofSeconds(2)

      while(keepRunning){

        val records : ConsumerRecords[String, User] = consumer.poll(pollDuration)
        val it = records.iterator()
        while(it.hasNext){
          println("============ Begin records from Q =========")
          val record : ConsumerRecord[String, User] = it.next()
          println("Message : ( Key -> " + record.key() + " Value -> " + record.value() + " Topic Name -> " + record.topic() + " Offset -> " + record.offset() + " Partition -> " + record.partition() + " ) ")

          val id = record.value().id.getOrElse(9999999)
          // Added logic to handle null values in CSV file
          val name = {
            if (record.value().name.get.toString == "") "Default Name"
            else record.value().name.get.toString
          }
          println("Writing to file --> " + id + "," + name)
          writer.write(id + " ----> " + name + "\n")

          consumer.commitSync()
        }

        // Logic to stop consumer if there is no new incoming message for 20 seconds (2*10)
        if (records == null || records.isEmpty) {
          counter = counter + 1
          println("counter incremented : " + counter)
          if (counter > 10)
            keepRunning = false
        }
        else
          counter = 0
      }

  }catch {
      case timeOutEx: TimeoutException =>
        println("Timeout ")
        false
      case ex: Exception => ex.printStackTrace()
        println("Got error when reading message ")
        false

    } finally {
      writer.close()
      consumer.close()
    }
  }


}
