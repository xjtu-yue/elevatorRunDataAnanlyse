package DAO

case class sexPercentOfHourStat(time_day:String,time_hour:String,peopleNum:Long,maleNum:Long,femaleNum:Long,malePercent:Double,femalePercent:Double)
case class agePercentOfHourStat(time_day:String,time_hour:String,peopleNum:Long,zeroAndSixPercent:Double,sixAndTwelvePercent:Double,twelveAndEighteenPercent:Double,eighteenAndTwentyFivePercent:Double,twentyFiveAndFortyPercent:Double,fortyAndFiftyFivePercent:Double,fiftyFivePercent:Double)
case class genderDataStat(createTime:String,maleNum:Long,femaleNum:Long,peopleNum:Long)
case class ageDataStat(createTime:String,peopleNum:Long,zeroAndSix:Long,sixAndTwelve:Long,twelveAndEighteen:Long,eighteenAndTwentyFive:Long,twentyFiveAndForty:Long,fortyAndFiftyFive:Long,fiftyFive:Long)



