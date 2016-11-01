package karedo.actors.transfer

import karedo.actors.{APIResponse, Error, KaredoAuthentication}
import karedo.entity._
import karedo.util.Util.now
import karedo.util._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
  * Created by pakkio on 10/8/16.
  */


trait Kar183_putTransfer_actor
  extends DbCollections
  with KaredoAuthentication
  with KaredoJsonHelpers
  with KaredoConstants
  with KaredoUtils
{

  override val logger = LoggerFactory.getLogger(classOf[Kar183_putTransfer_actor])

  def exec(
            deviceId: Option[String],
           request: Kar183Req
          ): Result[Error, APIResponse] = {
    val accountId = request.account_id
    val applicationId = request.application_id
    val sessionId = Some(request.session_id)


    logger.info(s"OK\nAccountId: $accountId\ndeviceId: $deviceId\napplicationId: $applicationId\nsessionId: $sessionId")

    authenticate(accountId, deviceId, applicationId, sessionId, allowCreation = false)(
      (uapp: Result[String, UserApp], uAccount: Result[String, UserAccount], code: Int) => {

        Try[Result[Error, APIResponse]] {
          val senderAccount = uAccount.get
          val senderProfile = dbUserProfile.find(senderAccount.id).get
          val sender_id = senderAccount.id
          val sender_name = s"${senderProfile.last_name.get}, ${senderProfile.first_name.get}"
          val sender_msisdn = senderAccount.findActiveMobile.get.msisdn

          // IF we got till here. Sender account exists.

          val karedos:Long = appKaredos_to_karedos(request.app_karedos)
          val sale_type = TRANS_TYPE_TRANSFER
          val trans_status = TRANS_STATUS_OPEN

          val receiverFirstName = request.receiver.first_name
          val receiverLastName = request.receiver.last_name
          val receiver_name = s"${receiverLastName}, ${receiverFirstName}"
          val receiver_msisdn = request.receiver.msisdn

          val sale_id = getNewSaleCode

          dbUserMobile.find(receiver_msisdn) match {
            case OK(merchantMobile) => {
              // Merchant Mobile - Set up real Sale
              val receiver_id = dbUserAccount.find(merchantMobile.account_id).get.id

              val origSale = Sale(id = sale_id,
                // Merchant - ID, Name, MSISDN
                receiver_id = receiver_id, receiver_name = receiver_name, receiver_msisdn = receiver_msisdn,
                // Customer - ID, Name, MSISDN
                sender_id = sender_id, sender_name = sender_name, sender_msisdn = sender_msisdn,
                // Karedos, Sale Type, Status
                karedos = karedos, sale_type = TRANS_TYPE_TRANSFER, status = TRANS_STATUS_OPEN,
                // Timestamps
                ts_created = now, ts_updated = now
              )

              dbSale.insertNew(origSale)

              moveKaredosBetweenAccounts(sender_id, receiver_id, Some(karedos),
                s"Tranfer setup from ${sender_id} to ${receiver_id} for ${karedos} Karedos") match {
                case KO(e) => logger.error(s"Unable to Tranfer setup from ${sender_id} to ${receiver_id} for ${karedos} Karedos", e)
                case OK(_) =>
              }

              val completeSale = origSale.copy(status = TRANS_STATUS_COMPLETE, ts_updated = now, ts_completed = Some(now))

              dbSale.update(completeSale)

              sendSMS(sender_msisdn, s"Success. Transfer of ${request.app_karedos} Karedos to ${receiver_msisdn} (${receiver_name}) Completed.")
              sendSMS(receiver_msisdn, s"Success. Transfer of ${request.app_karedos} Karedos from ${sender_msisdn} (${sender_name}) Completed.")

              OK(APIResponse("", HTTP_OK_200))
            }
            case KO(_) => {
              // Unknown Mobile - Set up a MobileSale
              val receiver_id = ""

              val origSale = Sale(id = sale_id,
                // Merchant - ID, Name, MSISDN
                receiver_id = receiver_id, receiver_name = receiver_name, receiver_msisdn = receiver_msisdn,
                // Customer - ID, Name, MSISDN
                sender_id = sender_id, sender_name = sender_name, sender_msisdn = sender_msisdn,
                // Karedos, Sale Type, Status
                karedos = karedos, sale_type = TRANS_TYPE_TRANSFER, status = TRANS_STATUS_OPEN,
                // Timestamps
                ts_created = now, ts_updated = now
              )

              dbSale.insertNew(origSale)

              dbMobileSale.find(receiver_msisdn) match {
                case OK(mobileSale) => {
                  val saleList = mobileSale.sale_ids

                  dbMobileSale.update(mobileSale.copy(sale_ids = saleList ++ List(sale_id)))
                }
                case KO(_) => {
                  dbMobileSale.insertNew(MobileSale(receiver_msisdn, List(sale_id)))
                }
              }

              sendSMS(sender_msisdn, s"${receiver_msisdn} (${receiver_name}) is not yet a member of Karedo. Transfer of ${request.app_karedos} Karedos will complete after they sign up")
              sendSMS(receiver_msisdn, s"${sender_msisdn} (${sender_name}) has sent you ${request.app_karedos} Karedos. Please sign up for Karedo to receive it.")

              OK(APIResponse("", HTTP_OK_200))
            }
          }
        } match {
          case Success(x) => x
          case Failure(error) => KO(Error(error.toString))
        }
      }
    )
  }
}