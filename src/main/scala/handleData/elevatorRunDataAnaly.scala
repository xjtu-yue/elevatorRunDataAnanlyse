package handleData

import java.text.SimpleDateFormat
import java.util.Date

import DAO.{agePercentOfHourStat, sexPercentOfHourStat}
import org.apache.spark.sql.functions._
import Utils.{getMongodbData, insertRabbitMQ}
import com.google.gson.Gson
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object elevatorRunDataAnaly {
  var sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
  var sdf2=new SimpleDateFormat("yyyy-MM-dd HH:00:00")
  val host="192.168.0.101"
  val port=5672
  val username="admin"
  val password="admin"
  var gson=new Gson()

  def main(args: Array[String]): Unit = {
    /**
      * 获取系统当前时间,按照当前系统时间的前一个小时从mongodb获取数据
      */
    var currentTimestamp=new Date()
    var time1=currentTimestamp.getTime-28800000
    var time2=time1-3600000
    var endTime=sdf2.format(time1)
    var endTime1=sdf2.parse(endTime)
    var startTime=sdf2.format(time2)
    var startTime1=sdf2.parse(startTime)
    var mongodbData=getMongodbData.selectSexData(startTime1,endTime1)

    val conf=new SparkConf().setAppName("elevatorRunDataAnaly").setMaster("local[4]")
    var sc=new SparkContext(conf)
    var sparkSesscion=SparkSession.builder().config(conf).getOrCreate()
    var readDf=sparkSesscion.createDataFrame(sexPercentOfHourStruct.createRow(mongodbData),sexPercentOfHourStruct.schema)

    sexPercentOfHour(sparkSesscion,readDf)

  }

  /**
    * 每天每小时男女人数及比例的分析，
    * 并将结果封装成对象发送到RabbitMQ
    * @param spark
    * @param accessDF
    */

  def sexPercentOfHour(spark:SparkSession,accessDF:DataFrame)={
    //rabbitMQ的相关配置，及获取connection和channel
    var exchange_name="rabbitMQ_sexPercentOfHour"
    var exchange_type="direct"
    var rountingKey="sexPercentOfHour"
    var connectionMQ=insertRabbitMQ.createConnection(host,port,username,password)
    var channel=insertRabbitMQ.createChannel(connectionMQ,exchange_name,exchange_type)
    //创建dataFream
    var tempRDD=accessDF.groupBy(accessDF("createTime").substr(0,10).as("time_day"),accessDF("createTime").substr(11,3).as("time_hour"))
      .agg(sum("maleNum").as("maleNum"),sum("femaleNum").as("femaleNum"),sum("peopleNum").as("peopleNum"))
    tempRDD.createOrReplaceTempView("sexPercentOfHourTable")
    var sqlResult=spark.sql("select time_day,time_hour,peopleNum,maleNum,femaleNum,round(maleNum/peopleNum,2)" +
      " as malePercent,round(femaleNum/peopleNum,2) as femalePercent from sexPercentOfHourTable").toLocalIterator()
    //遍历结果
    while (sqlResult.hasNext){
      var value=sqlResult.next()
      var time_day=value.getAs[String]("time_day")
      var time_hour=value.getAs[String]("time_hour")
      var peopleNum=value.getAs[Long]("peopleNum")
      var maleNum=value.getAs[Long]("maleNum")
      var femaleNum=value.getAs[Long]("femaleNum")
      var malePercent=value.getAs[Double]("malePercent")
      var femalePercent=value.getAs[Double]("femalePercent")
      var sexStat=sexPercentOfHourStat(time_day,time_hour,peopleNum,maleNum,femaleNum,malePercent,femalePercent)
      var message=gson.toJson(sexStat)
      channel.basicPublish(exchange_name,rountingKey,null,message.getBytes)
      println("插入RabbitMQ成功！")
    }

    def agePercentOfHour(spark:SparkSession,accessDF:DataFrame)={
      //rabbitMQ的相关配置，及获取connection和channel
      var exchange_name="rabbitMQ_agePercentOfHour"
      var exchange_type="direct"
      var rountingKey="agePercentOfHour"
      var connectionMQ=insertRabbitMQ.createConnection(host,port,username,password)
      var channel=insertRabbitMQ.createChannel(connectionMQ,exchange_name,exchange_type)
      //创建dataFream
      var tempRDD=accessDF.groupBy(accessDF("createTime").substr(0,10).as("time_day"),accessDF("createTime").substr(11,3).as("time_hour"))
        .agg(sum("peopleNum").as("peopleNum"),sum("zeroAndSix").as("zeroAndSixNum"),sum("sixAndTwelve").as("sixAndTwelveNum"),
          sum("twelveAndEighteen").as("twelveAndEighteenNum"),sum("eighteenAndTwentyFive").as("eighteenAndTwentyFiveNum"),
          sum("twentyFiveAndForty").as("twentyFiveAndFortyNum"),sum("fortyAndFiftyFive").as("fortyAndFiftyFiveNum"),sum("fiftyFive").as("fiftyFiveNum"))
      tempRDD.createOrReplaceTempView("agePercentOfHourTable")
      var sqlResult=spark.sql("select time_day,time_hour,peopleNum,round(zeroAndSixNum/peopleNum,2)" +
        " as zeroAndSixPercent,round(sixAndTwelveNum/peopleNum,2) as sixAndTwelvePercent," +
        "round(twelveAndEighteenNum/peopleNum,2) as twelveAndEighteenPercent,round(eighteenAndTwentyFiveNum/peopleNum,2) as eighteenAndTwentyFivePercent," +
        "round(twentyFiveAndFortyNum/peopleNum,2) as twentyFiveAndFortyPercent,round(fortyAndFiftyFiveNum/peopleNum,2) as fortyAndFiftyFivePercent," +
        "round(fiftyFiveNum/peopleNum,2) as fiftyFivePercent from sexPercentOfHourTable").toLocalIterator()
      //遍历结果
      while (sqlResult.hasNext){
        var value=sqlResult.next()
        var time_day=value.getAs[String]("time_day")
        var time_hour=value.getAs[String]("time_hour")
        var peopleNum=value.getAs[Long]("peopleNum")
        var zeroAndSixPercent=value.getAs[Long]("zeroAndSixPercent")
        var sixAndTwelvePercent=value.getAs[Long]("sixAndTwelvePercent")
        var twelveAndEighteenPercent=value.getAs[Double]("twelveAndEighteenPercent")
        var eighteenAndTwentyFivePercent=value.getAs[Double]("eighteenAndTwentyFivePercent")
        var twentyFiveAndFortyPercent=value.getAs[Double]("twentyFiveAndFortyPercent")
        var fortyAndFiftyFivePercent=value.getAs[Double]("fortyAndFiftyFivePercent")
        var fiftyFivePercent=value.getAs[Double]("fiftyFivePercent")
        var sexStat=agePercentOfHourStat(time_day,time_hour,peopleNum,zeroAndSixPercent,sixAndTwelvePercent,twelveAndEighteenPercent,eighteenAndTwentyFivePercent,twentyFiveAndFortyPercent,fortyAndFiftyFivePercent,fiftyFivePercent)
        var message=gson.toJson(sexStat)
        channel.basicPublish(exchange_name,rountingKey,null,message.getBytes)
        println("插入RabbitMQ成功！")
      }



  }

}
