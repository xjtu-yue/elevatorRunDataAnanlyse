package Utils

import com.rabbitmq.client.AMQP.Exchange
import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}

object insertRabbitMQ {
  def createConnection(host:String,port:Int,username: String,password: String): Connection ={
    var factory=new ConnectionFactory
    factory.setHost(host)
    factory.setPort(port)
    factory.setUsername(username)
    factory.setPassword(password)
    var connection=factory.newConnection()
    return connection
  }
  def createChannel(connection: Connection,exchange_name:String,exchange_type:String): Channel ={
    var channel=connection.createChannel()
    channel.exchangeDeclare(exchange_name,exchange_type)
    return channel
  }

  def main(args: Array[String]): Unit = {
    val host="192.168.0.101"
    val port=5672
    val username="admin"
    val password="admin"
    var routingKey1 = "error";
    var message1 = "error infomations....";
    var routingKey2 = "warning";
    var message2 = "warning infomations....";
    var routingKey3 = "info";
    var message3 = "info infomations....";
    var connection=createConnection(host,port,username,password)
    var channel=createChannel(connection,"rabbit_direct","direct")
    for (i <-0 to 100){
      channel.basicPublish("rabbit_direct",routingKey1,null,message1.getBytes())
      channel.basicPublish("rabbit_direct",routingKey2,null,message2.getBytes())
      channel.basicPublish("rabbit_direct",routingKey3,null,message3.getBytes())
    }
    channel.close()
    connection.close()
  }


}
