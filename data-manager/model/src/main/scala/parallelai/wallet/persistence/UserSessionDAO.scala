package parallelai.wallet.persistence

import java.util.UUID

import parallelai.wallet.entity.UserSession

trait UserSessionDAO {
  def validateSession(userSession: UserSession): Boolean
  def createNewSession(userId: UUID, applicationId: UUID): UserSession
  def deleteSession(sessionId: UUID)
}