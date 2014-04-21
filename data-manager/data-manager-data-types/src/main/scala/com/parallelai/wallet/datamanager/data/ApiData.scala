package com.parallelai.wallet.datamanager.data

import java.util.UUID
import com.wordnik.swagger.annotations.{ApiModelProperty, ApiModel}
import scala.annotation.meta.field


@ApiModel(description = "The registration request. To be valid at least one of the MSISDN or the Email needs to be provided")
case class RegistrationRequest(
                                @(ApiModelProperty@field)(value = "unique identifier for the application")
                                applicationId: UUID,
                                @(ApiModelProperty@field)(value = "User msisdn (optional)")
                                msisdn: Option[String],
                                @(ApiModelProperty@field)(value = "User email (optional)")
                                email: Option[String])

@ApiModel(description = "The registration validation.")
case class RegistrationValidation(
                                   @(ApiModelProperty@field)(value = "unique identifier for the application. Need to be the same of provided for registration")
                                   applicationId: UUID,
                                   @(ApiModelProperty@field)(value = "the validation code")
                                   validationCode: String)

@ApiModel(description = "The registration response.")
case class RegistrationResponse(
                                 @(ApiModelProperty@field)(value = "unique identifier for the application. Need to be the same of provided for registration")
                                 applicationId: UUID,
                                 @(ApiModelProperty@field)(value = "the channel used to send the validation code. At the moment can be just 'msisdn' or 'email'")
                                 channel: String,
                                 @(ApiModelProperty@field)(value = "the address the validation code has been sent to")
                                 address: String)

@ApiModel(description = "The registration validation response.")
case class RegistrationValidationResponse(
                                           @(ApiModelProperty@field)(value = "unique identifier for the application")
                                           applicationId: UUID,
                                           @(ApiModelProperty@field)(value = "useless")
                                           useless: String,
                                           @(ApiModelProperty@field)(value = "unique identifier for the user")
                                           userID: UUID)

@ApiModel(description = "User settings.")
case class UserSettings(maxAdsPerDay: Int)

@ApiModel(description = "User personal info.")
case class UserInfo(fullName: String, email: Option[String], msisdn: Option[String], address: Option[String], postCode: Option[String], country: Option[String])

@ApiModel(description = "The user profile.")
case class UserProfile(info: UserInfo, settings: UserSettings)
