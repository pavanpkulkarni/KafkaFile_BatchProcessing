package com.pavanpkulkarni.consumer

object ConsumerApp {

  def main(args: Array[String]) {
    val consumer = new KafkaFileConsumer(args(0), args(1))
    consumer.start()
  }
}
