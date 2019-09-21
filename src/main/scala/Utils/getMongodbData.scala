package Utils


import java.text.SimpleDateFormat
import java.util.Date

import DAO.{ageDataStat, genderDataStat}
import com.mongodb.casbah.{MongoClient, MongoDB}
import com.mongodb.{BasicDBObject, DBObject, MongoCredential, ServerAddress}

import scala.collection.mutable.ListBuffer

object getMongodbData {
  var sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
  var sdf2=new SimpleDateFormat("yyyy-MM-dd HH:00:000")
  def createDataBase(url:String, port: Int, dbName: String, loginName: String, password: String): MongoDB ={
    var server = new ServerAddress(url, port)
    //注意：MongoCredential中有6种创建连接方式，这里使用MONGODB_CR机制进行连接。如果选择错误则会发生权限验证失败
    var credentials = MongoCredential.createCredential(loginName, dbName, password.toCharArray)
//    MongoClient
    var mongoClient = MongoClient(server, List(credentials))
    mongoClient.getDB(dbName)
  }

  def selectSexData(startTime:Date,endTime:Date): ListBuffer[genderDataStat] ={
    val collection = createDataBase("192.168.0.132",27017,"ddzl","mydb","000").getCollection("recognize_result")
    var query=new BasicDBObject()
    query.put("createTime",new BasicDBObject("$gte",startTime).append("$lt",endTime))
    query.put("task",new BasicDBObject("$eq","gender"))
    val result = collection.find(query).limit(100)
    var listResult=new ListBuffer[genderDataStat]
    while (result.hasNext){
      val value:DBObject = result.next()
      println("task:"+(value.get("task")))
      var result1=value.get("result")
      var sexField=result1.toString.split(",")
      var maleNum=sexField(0).substring(5,sexField(0).size).toLong
      var femaleNum=sexField(1).substring(7,sexField(1).size).toLong
      var peopeleNum=maleNum+femaleNum
      var createTimeTmp=value.get("createTime")
      var createTime=sdf.format(createTimeTmp)
      listResult.append(genderDataStat(createTime,maleNum,femaleNum,peopeleNum))

    }
    return listResult

  }

  def selectAgeData(startTime:Date,endTime:Date):ListBuffer[ageDataStat] ={
    val collection=createDataBase("192.168.0.132",27017,"ddzl","mydb","000").getCollection("recognize_result")
    var query=new BasicDBObject()
    query.put("createTime",new BasicDBObject(("$gte"),startTime).append("$lt",endTime))
    query.put("task",new BasicDBObject("$eq","age"))
    var resultData=collection.find(query).limit(10)
    var listBuffer=new ListBuffer[ageDataStat]
    while (resultData.hasNext){
//      zeroAndSix:Long,sixAndTwelve:Long,twelveAndEighteen:Long,eighteenAndTwentyFive:Long,twentyFiveAndForty:Long,fortyAndFiftyFive:Long,fiftyFive:Long
      var value=resultData.next()
      var createTime=sdf.format(value.get("createTime"))
      var result=value.get("result").toString.split(",")
      var zeroAndSix=result(4).substring(6,result(4).size).toLong
      var sixAndTwelve=result(5).substring(7,result(5).size).toLong
      var twelveAndEighteen=result(6).substring(8,result(6).size).toLong
      var eighteenAndTwentyFive=result(7).substring(8,result(7).size).toLong
      var twentyFiveAndForty=result(8).substring(8,result(8).size).toLong
      var fortyAndFiftyFive=result(9).substring(8,result(9).size).toLong
      var fiftyFive=result(3).substring(6,result(3).size).toLong
      var peopleNum=zeroAndSix+sixAndTwelve+twelveAndEighteen+eighteenAndTwentyFive+twentyFiveAndForty+fortyAndFiftyFive+fiftyFive
      listBuffer.append(ageDataStat(createTime,peopleNum,zeroAndSix,sixAndTwelve,twelveAndEighteen,eighteenAndTwentyFive,twentyFiveAndForty,fortyAndFiftyFive,fiftyFive))
      println("fortyAndFiftyFive:"+zeroAndSix)
    }
    return listBuffer


  }

  def main(args: Array[String]): Unit = {
    var currentTimestamp=new Date()
    var time1=currentTimestamp.getTime
    var time2=time1-3600000
    var endTime=sdf2.format(time1)
    var endTime1=sdf2.parse(endTime)
    var startTime=sdf2.format(time2)
    var startTime1=sdf2.parse(startTime)
    println("startTime:"+startTime1+"endTime:"+endTime1)
//    selectSexData(startTime1,endTime1)
    selectAgeData(startTime1,endTime1)


  }

}
