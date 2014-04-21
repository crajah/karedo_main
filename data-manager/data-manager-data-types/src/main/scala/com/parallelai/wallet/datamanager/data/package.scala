package com.parallelai.wallet.datamanager

import java.util.UUID

package object data {

  def applicationIdFromString(appId: String) : UUID = UUID.fromString(appId)
  def userIdFromString(userId: String) : UUID = UUID.fromString(userId)
}
