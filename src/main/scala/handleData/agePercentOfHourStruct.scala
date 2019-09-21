package handleData

import java.util

import DAO.ageDataStat
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

import scala.collection.mutable.ListBuffer


object ageOfHourStruct {
  var schema=StructType(
    Array(
      StructField("createTime",StringType,true),
      StructField("peopleNum",LongType,true),
      StructField("zeroAndSix",LongType,true),
      StructField("sixAndTwelve",LongType,true),
      StructField("twelveAndEighteen",LongType,true),
      StructField("eighteenAndTwentyFive",LongType,true),
      StructField("twentyFiveAndForty",LongType,true),
      StructField("fortyAndFiftyFive",LongType,true),
      StructField("fiftyFive",LongType,true)
    )
  )

  def createRow(list:ListBuffer[ageDataStat]): util.ArrayList[Row] ={
    var dataList=new util.ArrayList[Row]()
    val iterator = list.toIterator
    while (iterator.hasNext){
      var dataStat=iterator.next()
      var createTime=dataStat.createTime
      var peopleNum=dataStat.peopleNum
      var zeroAndSix=dataStat.zeroAndSix
      var sixAndTwelve=dataStat.sixAndTwelve
      var twelveAndEighteen=dataStat.twelveAndEighteen
      var eighteenAndTwentyFive=dataStat.eighteenAndTwentyFive
      var twentyFiveAndForty=dataStat.twentyFiveAndForty
      var fortyAndFiftyFive=dataStat.fortyAndFiftyFive
      var fiftyFive=dataStat.fiftyFive
      dataList.add(Row(createTime,peopleNum,zeroAndSix,sixAndTwelve,twelveAndEighteen,eighteenAndTwentyFive,twentyFiveAndForty,fortyAndFiftyFive,fiftyFive))
    }
    return dataList
  }

}
