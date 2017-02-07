/*
 * Copyright (c) 2010 - 2015 Novus Partners, Inc. (http://www.novus.com)
 * Copyright (c) 2015 - 2016 Rose Toomey (https://github.com/rktoomey) and other individual contributors where noted
 *
 * Module:        salat-util
 * Class:         glitch.scala
 * Last modified: 2016-07-10 23:45:43 EDT
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

package salat.util

import java.lang.reflect.Constructor

/** A Salat transformation error that does not have an underlying cause. */
class SalatGlitch(msg: String) extends Error(msg)

// NOTE: There are places all over this codebase where we use sys.error (RuntimeException) rather
// than throwing a specific subclass of Error or RuntimeException. An outstanding cleanup TODO
// (at risk of breaking production code in the wild) could be to normalize all these ad-hoc errors to
// specific, actionable errors.

/** The target type of the value was incompatible with the type of the field. */
case class IncompatibleTargetFieldType(msg: String, cause: Throwable) extends Error(msg, cause)
object IncompatibleTargetFieldType {
  def apply(msg: String): IncompatibleTargetFieldType = IncompatibleTargetFieldType(msg, null)
}

/**
 * Runtime error indicating that a class defines more than one constructor with args.
 *  @param clazz parameterized class instance
 *  @param cl list of parameterized constructors found for this class
 *  @tparam X any reft
 */
case class TooManyConstructorsWithArgs[X](clazz: Class[X], cl: List[Constructor[X]]) extends SalatGlitch(
  "constructor: clazz=%s ---> expected 1 constructor with args but found %d\n%s".format(clazz, cl.size, cl.mkString("\n"))
)

/**
 * Runtime error indicating that Salat can't identify any constructor for this class.
 *  @param clazz class instance
 */
case class MissingConstructor(clazz: Class[_]) extends SalatGlitch("Couldn't find a constructor for %s".format(clazz.getName))

/**
 * Runtime error indicating that Salat can't find the pickled Scala signature for this class.
 *  @param clazz class instance
 */
case class MissingPickledSig(clazz: Class[_]) extends SalatGlitch("FAIL: class '%s' is missing both @ScalaSig and .class file!".format(clazz))

/**
 * Runtime error indicating that class' pickled Scala signature does not define any top-level classes or objects.
 *  @param clazz class instance
 */
case class MissingExpectedType(clazz: Class[_]) extends SalatGlitch("Parsed pickled Scala signature, but no expected type found: %s"
  .format(clazz))

//case class NestingGlitch(clazz: Class[_], owner: String, outer: String, inner: String) extends Error("Didn't find owner=%s, outer=%s, inner=%s in pickled scala sig for %s"
//  .format(owner, outer, inner, clazz))

case class MissingCaseObjectOverride(path: String, value: Any, ctxName: String) extends SalatGlitch(
  "Ctx='%s' does not define a case object override that can be used with class='%s' and value='%s'".
    format(ctxName, path, value)
)
