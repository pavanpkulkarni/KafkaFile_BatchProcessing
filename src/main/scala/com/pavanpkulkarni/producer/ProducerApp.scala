package com.pavanpkulkarni.producer

object ProducerApp {

    def main(args: Array[String]) {
        val producer = new KafkaFileProducer(args(0), args(1))
        producer.send()
    }
}
