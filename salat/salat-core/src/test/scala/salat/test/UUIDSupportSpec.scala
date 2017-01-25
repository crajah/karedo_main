/*
 * Copyright (c) 2010 - 2015 Novus Partners, Inc. (http://www.novus.com)
 * Copyright (c) 2015 - 2016 Rose Toomey (https://github.com/rktoomey) and other individual contributors where noted
 *
 * Module:        salat-core
 * Class:         UUIDSupportSpec.scala
 * Last modified: 2016-07-10 23:49:08 EDT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *           Project:  http://github.com/salat/salat
 *              Wiki:  http://github.com/salat/salat/wiki
 *             Slack:  https://scala-salat.slack.com
 *      Mailing list:  http://groups.google.com/group/scala-salat
 *     StackOverflow:  http://stackoverflow.com/questions/tagged/salat
 *
 */
package salat.test

import com.mongodb.casbah.Imports._
import salat._
import salat.test.global._
import salat.test.model._

class UUIDSupportSpec extends SalatSpec {

  "a grater" should {

    "support serializing and deserializing a random uuid" in {
      val uuid = java.util.UUID.randomUUID
      val o = Olive(uuid)
      val dbo: MongoDBObject = grater[Olive].asDBObject(o)
      //      log.info(MapPrettyPrinter(dbo))
      dbo must havePair("awl" -> uuid)

      val coll = MongoConnection()(SalatSpecDb)("uuid_test_1")
      val wr = coll.insert(dbo)
      val o_* = grater[Olive].asObject(coll.findOne().get)
      o_* must_== o
    }

    "support serializing and deserializing a msb/lsb uuid" in {
      val uuid = new java.util.UUID(123L, 456L)
      val o = Olive(uuid)
      val dbo: MongoDBObject = grater[Olive].asDBObject(o)
      //      log.info(MapPrettyPrinter(dbo))
      dbo must havePair("awl" -> uuid)

      val coll = MongoConnection()(SalatSpecDb)("uuid_test_2")
      val wr = coll.insert(dbo)
      val o_* = grater[Olive].asObject(coll.findOne().get)
      o_* must_== o
    }

    "support serializing and deserializing a uuid with name from bytes" in {
      val uuid = java.util.UUID.nameUUIDFromBytes("pierced".getBytes)
      val o = Olive(uuid)
      val dbo: MongoDBObject = grater[Olive].asDBObject(o)
      //      log.info(MapPrettyPrinter(dbo))
      dbo must havePair("awl" -> uuid)

      val coll = MongoConnection()(SalatSpecDb)("uuid_test_3")
      val wr = coll.insert(dbo)
      val o_* = grater[Olive].asObject(coll.findOne().get)
      o_* must_== o
    }

  }

}
