package karedo.rtb.model


/**
  * Created by crajah on 11/08/2016.
  *
  * Contains the case classes for OpenRTB version 2.3.1
  */

object BidJson {
  import spray.json._

  import DefaultJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  case class BidRequest(id:String, imp:Imp, site:Option[Site] = None, app:Option[App] = None, device:Device, user:User,
                        test:Option[Int] = Some(0), at:Option[Int] = Some(2), tmam:Option[Int] = Some(200),
                        allimp:Option[Int] = Some(0), cur:Option[String] = Some("USD"), bcat:Option[String] = None)

  case class Imp(id:String, banner:Option[Banner] = None, video:Option[Video] = None, native:Option[Native] = None,
                 instl:Option[Int] = Some(1), bidfloor:Option[Double] = Some(5.0), secure:Option[Int] = Some(0),
                 pmp:Option[Pmp] = None)

  case class Banner(w:Int = 300, h:Int = 250, wmax:Option[Int] = None, hmax:Option[Int] = None,
                    wmin:Option[Int] = None, hmin:Option[Int] = None, id:Option[String] = None,
                    pos:Option[Int] = Some(0), btype:Option[List[Int]] = None, battr:Option[List[Int]] = None,
                    mimes:Option[List[String]] = Some(List("image/jpg", "image/png", "image/gif")),
                    topframe:Option[Int] = Some(1), expdir:Option[List[Int]] = Some(List(1,2,3,4)))

  case class Video(mimes:Option[List[String]] = Some(List("video/mp4")), minduration:Option[Int] = Some(5),
                   maxduration:Option[Int] = Some(30),
                   protocols:Option[List[Int]] = Some(List(3)), w:Int = 300, h:Int = 250,
                   startdelay:Option[Int] = Some(0), linearity:Option[Int] = Some(1),
                   battr:Option[List[Int]] = None,
                   minbitrate:Option[Int] = Some(500), maxbitrate:Option[Int] = Some(3000),
                   boxingallowed:Option[Int] = Some(0), delivery:Option[List[Int]] = Some(List(1, 2)),
                   pos:Option[Int] = Some(0),
                   companionad:Option[List[Banner]] = Some(List(Banner())),
                   api:Option[List[Int]] = Some(List(5)), companiontype:Option[List[Int]] = Some(List(1)) )

  case class Native(request:String, ver:Option[String] = None, api:Option[List[Int]] = None,
                    battr:Option[List[Int]] = None)

  // Site or App => Not both
  case class Site(id:String, name:Option[String] = Some("karedo"), domain:Option[String] = Some("karedo.co.uk"),
                  cat:Option[List[String]] = None, sectioncat:Option[List[String]] = None,
                  pagecat:Option[List[String]] = None, page:Option[String] = Some("http://www.karedo.co.uk/main"),
                  ref:Option[String] = None, search:Option[String] = None, mobile:Option[Int] = Some(1),
                  privacypolicy:Option[Int] = Some(0), publisher:Option[Publisher] = None,
                  content:Option[Content] = None, keywords:Option[String] = None)

  // Site or App => Not both
  case class App(id:String, name:Option[String] = Some("karedo"), bundle:Option[String] = Some("karedo"),
                 domain:Option[String] = Some("karedo.co.uk"), storeurl:Option[String] = None,
                 cat:Option[List[String]] = None, sectioncat:Option[List[String]] = None,
                 pagecat:Option[List[String]] = None, ver:Option[String] = Some("1.0"),
                 privacypolicy:Option[Int] = Some(0), paid:Option[Int] = Some(0),
                 publisher:Option[Publisher] = None,
                 content:Option[Content] = None, keywords:Option[String] = None)

  case class Publisher(id:String, name:Option[String] = Some("karedo"),
                       cat:Option[List[String]] = None, domain:Option[String] = Some("karedo.co.uk"))

  case class Content(id:String, episode:Option[Int] = None, title:Option[String] = None,
                     series:Option[String] = None, season:Option[String] = None,
                     producer:Option[Producer] = None, url:Option[String] = None,
                     cat:Option[List[String]] = None, videoquality:Option[Int] = None,
                     context:Option[Int] = None, contentrating:Option[String] = None,
                     userrating:Option[String] = None, qagmediarating:Option[Int] = None,
                     keywords:Option[String] = None, livestream:Option[Int] = None,
                     sourcerelationship:Option[Int] = None, len:Option[Int] = None,
                     language:Option[String] = None, embeddable:Option[Int] = None)

  case class Producer(id:String, name:Option[String] = Some("karedo"),
                      cat:Option[List[String]] = None, domain:Option[String] = Some("karedo.co.uk"))

  case class Device(ua:Option[String] = None, geo:Option[Geo] = None, dnt:Option[Int] = Some(1),
                    lmt:Option[Int] = Some(1), ip:Option[String] = Some("127.0.0.1"),
//                    ipv6:Option[String] = None,
                    devicetype:Option[Int] = Some(1),
                    make:Option[String] = None, model:Option[String] = None,
                    os:Option[String] = None, osv:Option[String] = None, hwv:Option[String] = None,
                    h:Option[Int] = Some(600), w:Option[Int] = Some(300), ppi:Option[Int] = Some(200),
                    pxratio:Option[Double] = Some(1.17),
                    js:Option[Int] = Some(0),
//                    flashver:Option[String] = None,
                    language:Option[String] = None,
//                    carrier:Option[String] = None, connectiontype:Option[Int] = None,
                    ifa:Option[String] = None,
//                   IMEI - Not included.
//                    didsha1:Option[String] = None, didmd5:Option[String] = None,
                    pdidsha1:Option[String] = None, dpidmd5:Option[String] = None,
                    macsha1:Option[String] = None, macmd5:Option[String] = None)


  case class Geo(lat:Double, lon:Double, $type:Int = 1, country:String = "GB",
                 region:Option[String] = None, regionfips104:Option[String] = None,
                 metro:Option[String] = None, city:Option[String] = None, zip:Option[String] = None,
                 utoffset:Option[Int] = None)

  case class User(id:String, buyeruid:String, yob:Option[Int] = None,
                  gender:Option[String] = None, keywords:Option[String] = None,
                  customdata:Option[String] = None, geo:Option[Geo] = None,
                  data:Option[List[Data]] = None)

  case class Data(id:Option[String] = None, name:Option[String] = None,
                  segment:Option[List[Segment]] = None)

  case class Segment(id:Option[String] = None, name:Option[String] = None, value:Option[String] = None)

  case class Regs(coppa:Option[Int] = None)

  case class Pmp(private_auction:Option[Int] = None, deals:Option[List[Deal]] = None)

  case class Deal(id:String, bidfloor:Option[Double] = Some(0), bidfloorcur:Option[String] = Some("USD"),
                  at:Option[Int] = None, wseat:Option[List[String]] = None,
                  wadomain:Option[List[String]] = None)



  case class BidResponse(id:String, seatbid:List[SeatBid], bidid:String, cur:Option[String] = Some("USD"),
                         customdata:Option[String], nbr:Option[Int])

  case class SeatBid(bid:List[Bid], seat:String, group:Option[Int])

  case class Bid(id:String, impid:String, price:Double, adid:String, nurl:String, adm:Option[String],
                 adomain:Option[List[String]], bundle:Option[String], iurl:Option[String],
                 cid:Option[String], crid:Option[String],
                 cat:Option[List[String]], attr:Option[List[Int]], api:Option[Int],
                 protocol:Option[Int], qagmediarating:Option[Int], dealid:Option[String],
                 w:Int, h:Int, exp:Option[Int])



  implicit val deal = jsonFormat6(Deal)
  implicit val pmp = jsonFormat2(Pmp)
  implicit val regs = jsonFormat1(Regs)
  implicit val segment = jsonFormat3(Segment)
  implicit val data = jsonFormat3(Data)
  implicit val geo = jsonFormat10(Geo)
  implicit val user = jsonFormat8(User)
  implicit val device = jsonFormat22(Device)
  implicit val producer = jsonFormat4(Producer)
  implicit val content = jsonFormat19(Content)
  implicit val publisher = jsonFormat4(Publisher)
  implicit val app = jsonFormat14(App)
  implicit val site = jsonFormat14(Site)
  implicit val native = jsonFormat4(Native)
  implicit val banner = jsonFormat13(Banner)
  implicit val video = jsonFormat17(Video)
  implicit val imp = jsonFormat8(Imp)


  implicit val bid = jsonFormat20(Bid)
  implicit val seatBid = jsonFormat3(SeatBid)

  implicit val bidRequest:RootJsonFormat[BidRequest] = jsonFormat12(BidRequest)

  implicit val bidResponse:RootJsonFormat[BidResponse] = jsonFormat6(BidResponse)
}