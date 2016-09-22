package karedo.rtb.model

import java.util.UUID

/**
  * Created by crajah on 03/09/2016.
  */
case class AdRequest(userId:UUID, count:Int)

case class AdResponse(ads:List[AdUnit])

case class AdUnit(
                   impid:String,
                   price:Double, // In USD/M
                   adid:String,
                   nurl:String,
                   adm:Option[String],
                   adomain:Option[List[String]],
                   // ONly in 2.3.1 onwards
                   bundle:Option[String],
                   iurl:Option[String],
                   cid:Option[String],
                   crid:Option[String],
                   // Only in 2.3.1 onwards
                   cat:Option[List[String]],
                   attr:Option[List[Int]],
                   // Only in 2.3.1 onwards
                   api:Option[Int],
                   protocol:Option[Int],
                   qagmediarating:Option[Int],

                   dealid:Option[String],
                   w:Int,
                   h:Int
                 )