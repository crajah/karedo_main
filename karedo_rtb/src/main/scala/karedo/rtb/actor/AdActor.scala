package karedo.rtb.actor



import java.io.File
import java.util.concurrent.Executors

import akka.actor.Actor
import karedo.entity.UserPrefData
import karedo.rtb.dsp._
import karedo.rtb.model._
import karedo.rtb.model.AdModel._
import karedo.util.{KO, OK, Result}
import karedo.rtb.model.DbCollections
import karedo.rtb.util.Configurable

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Await, ExecutionContext, Future}
import collection.JavaConverters._
import karedo.rtb.model.BidRequestCommon._
import karedo.rtb.util._
import RTBUtils._

/**
  * Created by crajah on 25/08/2016.
  */
class AdActor
  extends DbCollections
    with Configurable
    with DeviceMakes
    with RtbConstants
    with LoggingSupport {
  var dspDispatchers:Option[List[DspBidDispather]] = None

  preStart

  def preStart = {
    val dspDispatcherConfigs = conf.getConfigList("dsp.dispatchers").asScala.map(c => {
      DspBidDispatcherConfig(c.getString("name"), c.getString("kind"), c.getString("endpoint"))
    }).toList

    val dispatchers:List[DspBidDispather] = dspDispatcherConfigs.map(dc => {
      dc.kind match {
        case "DUMMY" => new DummyDspBidDispatcher(dc)
        case "ORTB2.2" => new ORTB2_2_1DspBidDispatcher(dc)
        case "ORTB2.2.1" => new ORTB2_2_1DspBidDispatcher(dc)
        case _ => new DummyDspBidDispatcher(dc)
      }
    })

    dspDispatchers = if (dispatchers != Nil) Some(dispatchers) else None
  }

  def getAds(request:AdRequest) : List[AdUnit] = {
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

      val adUnits:List[AdUnit] = Try [List[AdUnit]] {
        val userProfile = dbUserProfile.find(request.userId).get
        val userPrefs = dbUserPrefs.find(request.userId).get
        val devReq = request.device

        val make = devReq.make.getOrElse("") match {
          case DEVICE_MAKE_IOS => iOS
          case DEVICE_MAKE_ANDROID => Android
          case _ => iOS
        }

        val userObj: User = User(
          id = request.userId,
          gender = userProfile.gender,
          yob = userProfile.yob,
          geo =
              Some(Geo(
                lat = devReq.lat,
                lon = devReq.lon,
                country = devReq.country
              ))
          )


        def getHashes(in: Option[String]): (Option[String], Option[String]) = in match {
          case Some(d) => (Some(getMD5Hash(d)), Some(getSHA1Hash(d)))
          case _ => (None, None)
        }

        val did:(Option[String], Option[String]) = getHashes(devReq.did)
        val dpid:(Option[String], Option[String]) = getHashes(devReq.dpid)
        val mac:(Option[String], Option[String]) = getHashes(devReq.mac)

        val deviceObj: Device = Device(
          ua = devReq.ua,
          ifa = if(devReq.ifa.isDefined) Some(getMD5Hash(devReq.ifa.get)) else None,
          geo =
            Some(Geo(
              lat = devReq.lat,
              lon = devReq.lon,
              country = devReq.country
            )),
          didmd5 = did._1,
          didsha1 = did._2,
          dpidmd5 = dpid._1,
          dpidsha1 = dpid._2,
          macmd5 = mac._1,
          macsha1 = mac._2,
          ip = if(devReq.xff.isDefined) {
            try {
              val v = devReq.xff.get.split(",")(0)
              val vn = java.net.InetAddress.getByName(v).getHostAddress
              Some(vn)
            } catch {
              case e:Exception => {
                logger.error(s"IP address conversion from XFF header ${devReq.xff} failed", e)
                None
              }
            }
          } else if(devReq.ip.isDefined) devReq.ip else None,
          make = devReq.make,
          model = devReq.model,
          os = devReq.os,
          osv = devReq.osv
        )
        val iabCatsMapObj: Map[String, UserPrefData] = userPrefs.prefs

        val resp:List[List[AdUnit]] = dspDispatchers match {
          case Some(dispatchers) => {
            try {
              val fSeq = Future.sequence(
                dispatchers.map(d =>
                  Future(
                    d.getAds(request.count, userObj, deviceObj, iabCatsMapObj, make)
                  )
                )
              )

              val ads: List[List[AdUnit]] = Await.result(
                fSeq.mapTo[List[List[AdUnit]]], dispatcher_max_wait)

              ads
            } catch {
              case e: Exception => {
                println(s"Dispatchers not found" + "\n" + e.getMessage + "\n" + e.getStackTrace.foldLeft("")((z, b) => z + b.toString + "\n"))
                logger.error(s"Dispatchers not found", e)
                List(List())
              }
            }
          }
          case None => List(List())
        }

        val r = resp

        resp.flatten.sortWith(_.price > _.price).take(request.count)
      } match {
        case Success(s) => s
        case Failure(f) => List()
      }

      adUnits
  }

  /*
  override def receive = {
    case AdRequest(userId, count, lat, lon, deviceTypeString) => {
      val adUnits:List[AdUnit] = Try [Result[String, List[AdUnit]]] {
        val userProfile = dbUserProfile.find(userId).get
        val userPrefs = dbUserPrefs.find(userId).get

        val deviceType = deviceTypeString match {
          case DEVICE_TYPE_IOS => iOS
          case DEVICE_TYPE_ANDROID => Android
          case _ => iOS
        }

        val userObj: User = User(
          id = userId,
          gender = userProfile.gender,
          yob = userProfile.yob,
          geo = Some(Geo(
            lat = lat, lon = lon,
            country = Some("GB")
          )))
        val deviceObj: Device = Device()
        val iabCatsMapObj: Map[String, UserPrefData] = userPrefs.prefs

        val resp:List[List[AdUnit]] = dspDispatchers match {
          case Some(dispatchers) => dispatchers.map(d => d.getAds(count, userObj, deviceObj, iabCatsMapObj, deviceType ))
          case None => List(List())
        }

        val adUnits = resp.flatten.sortWith(_.price > _.price).take(count)

        OK(adUnits)
      } match {
        case Success(s) => s.get
        case Failure(f) => List()
      }

      sender ! OK(AdResponse(adUnits.size, adUnits))
    }
  }
  */

  /*

  def receive1 = {
    case AdRequest(userId, count) => {
      Try [Result[String, Unit]] {
        // @TODO: get UserProfile
        val userProfile = dbUserProfile.find(userId.toString).get

        // @TODO: Fill User Object.
        // @TODO: Send it to auctioneers

        // @TODO: Return AdResponse

        KO("Fake it, till you make it")
      } match {
        case Success(s) =>
        case Failure(f) => {
          // Fake it till you make it.
          val adUnits = scala.collection.mutable.ListBuffer.empty[AdUnit]


          (1 to count).foreach(x => {
            val u = makeAdUrlPair
            adUnits += AdUnit(
              ad_type_IMAGE,
              java.util.UUID.randomUUID.toString,
              java.util.UUID.randomUUID.toString,
              Ad( u._1, u._2, u._3, u._4, None, Some(250), Some(300), None),
              Math.random() * 100,
              Some(List("karedo.co.uk")),
              None,
              None,
              Some(java.util.UUID.randomUUID.toString),
              Some(java.util.UUID.randomUUID.toString),
              300,
              250
            )
          })

          sender ! OK(AdResponse(adUnits.length, adUnits.toList))
        }
      }


    }
  }


  def makeAdUrlPair: (String, String, String, Option[String]) = {

    val ads = Array(
      ("http://karedo.co.uk/ads/c1.jpg",
        "https://www.washingtonpost.com/news/answer-sheet/wp/2015/03/26/no-finlands-schools-arent-giving-up-traditional-subjects-heres-what-the-reforms-will-really-do/",
        "No, Finland isn’t ditching traditional school subjects. Here’s what’s really happening.", Some("Washington Post")),
      ("http://karedo.co.uk/ads/c2.jpg", "http://www.iflscience.com/health-and-medicine/paralyzed-monkeys-walk-again-thanks-brain-implant/",
        "Paralyzed Monkeys Walk Again Thanks To Brain Implant", None),
      ("http://karedo.co.uk/ads/c3.jpg",
        "http://www.financeworld.news/index.php/2016/11/02/rich-bankers-terrified/",
        "Rich Bankers Are Terrified Their Secret Weapon Is Finally Revealed", Some("Finance World")),
      ("http://karedo.co.uk/ads/c4.jpg", "http://www.nytimes.com/2016/11/12/business/economy/donald-trump-trade-tpp-trans-pacific-partnership.html",
        "What Is Lost by Burying the Trans-Pacific Partnership?", Some("New York Times")),
      ("http://karedo.co.uk/ads/c5.jpg", "http://www.bbc.co.uk/news/uk-37951771", "Armistice Day in pictures", Some("BBC")),
      ("http://karedo.co.uk/ads/c6.jpg", "http://www.forbes.com/sites/kathleenchaykowski/2016/11/10/mark-zuckerberg-says-fake-news-on-facebook-did-not-sway-the-u-s-election/#4415191146a5",
        "Mark Zuckerberg Says Fake News On Facebook Did Not Sway The U.S. Election", None),
      ("http://karedo.co.uk/ads/c7.jpg", "https://www.theguardian.com/sport/blog/2016/nov/10/conor-mcgregor-eddie-alvarez-ufc-205",
        "The cheapening of Conor McGregor's once-clever act", Some("Guardian")),
      ("http://karedo.co.uk/ads/c8.jpg", "http://www.nytimes.com/2016/11/12/technology/10-second-videos-from-your-sunglasses-thank-snapchat.html?ref=technology",
        "10-Second Videos From Your Sunglasses. Thank Snapchat.", Some("New York Times"))
    )

    val i:Int = (Math.random() * ads.length).toInt

    ads(i)
  }
  */
}

