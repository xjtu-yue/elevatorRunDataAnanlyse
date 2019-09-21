package handleData

import java.util

import DAO.genderDataStat
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql._
import scala.collection.mutable.ListBuffer

object sexPercentOfHourStruct {
  /**
    * 自定义schema
    */
  var schema=StructType(
    Array(
      StructField("createTime",StringType,true),
      StructField("maleNum",LongType,true),
      StructField("femaleNum",LongType,true),
      StructField("peopleNum",LongType,true)

    )
  )

  /**
    * 动态创建schema
    * @param list
    * @return
    */

  def createRow(list:ListBuffer[genderDataStat]): util.ArrayList[Row] ={
    var dataList=new util.ArrayList[Row]()
    val iterator = list.toIterator
    while (iterator.hasNext){
      val dataStat = iterator.next()
      var createTime=dataStat.createTime
      var maleNum=dataStat.maleNum
      var femaleNum=dataStat.femaleNum
      var peopeleNum=dataStat.peopleNum
      dataList.add(Row(createTime,maleNum,femaleNum,peopeleNum))
    }
    return dataList
  }

}
