package com.pavanpkulkarni.producer

import java.io.File
import java.util.{Properties, UUID}

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import com.pavanpkulkarni.schema.User

import scala.io.Source
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger


class KafkaFileProducer(val topic: String, val propertyFile: String) {


  val logger = Logger.getLogger( getClass.getName )
  var propertyFileName = ConfigFactory.parseFile( new File(propertyFile) )
  val propertyConfs = ConfigFactory.load( propertyFileName )

  logger.info( "Property Details : " + propertyConfs.getConfig( "kafkaJobProperties" ) )

  private val props = new Properties()
  props.put("bootstrap.servers", propertyConfs.getString("kafkaJobProperties.kafkaCommon.bootstrap.servers"))
  props.put("schema.registry.url", propertyConfs.getString("kafkaJobProperties.kafkaCommon.schema.registry.url"))
  props.put("key.serializer", classOf[StringSerializer].getCanonicalName)
  props.put("value.serializer", classOf[KafkaAvroSerializer].getCanonicalName)
  props.put("client.id", UUID.randomUUID().toString)
  props.put("batch.size", propertyConfs.getString("kafkaJobProperties.producerProperties.batch.size"))

  if(propertyConfs.getString("kafkaJobProperties.kafkaCommon.enable.monitoring").equalsIgnoreCase("yes"))
    props.put("interceptor.classes", propertyConfs.getString("kafkaJobProperties.producerProperties.interceptor.classes") )

  private val producer =   new KafkaProducer[String,User](props)

  def send(): Unit = {
    try {
      val rand = new scala.util.Random(44343)
      val id = rand.nextInt()

      val inputFilename = propertyConfs.getString("kafkaJobProperties.kafkaCommon.input.filename")
      val lines = Source.fromFile(inputFilename).getLines()
      lines.foreach { line =>

        val record: Array[String] = line.split(",").map(_.trim)

        val itemToSend = User(record(0).toInt, record(1))

        println(s"Producer sending data ${itemToSend.toString}")
        producer.send(new ProducerRecord[String, User](topic, itemToSend))
        producer.flush()

      }

    } catch {
      case ex: Exception =>
        println(ex.printStackTrace().toString)
        ex.printStackTrace()
    }
  }
}
