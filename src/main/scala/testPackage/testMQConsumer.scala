package testPackage

import Utils.insertRabbitMQ
import com.rabbitmq.client.{AMQP, DefaultConsumer, Envelope}

object testMQConsumer {
  def main(args: Array[String]): Unit = {
    val host="192.168.0.101"
    val port=5672
    val username="admin"
    val password="admin"
    var connection=insertRabbitMQ.createConnection(host,port,username,password)
    var channel=insertRabbitMQ.createChannel(connection,"rabbit_direct","direct")
    var queueName=channel.queueDeclare().getQueue
    channel.queueBind(queueName,"rabbit_direct","error")
    var consumer=new DefaultConsumer(channel){
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        val iterator = body.iterator
        while (iterator.hasNext){
          var value=iterator.next()
          println("value:"+value)
        }
      }
    }
    channel.basicConsume(queueName,true,consumer)
  }

}
