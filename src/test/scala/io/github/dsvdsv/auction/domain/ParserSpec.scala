package io.github.dsvdsv.auction.domain

import cats.data.EitherT
import cats.effect.SyncIO
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.scalacheck.Checkers

@RunWith(classOf[JUnitRunner])
class ParserSpec extends FunSpec with Matchers with Checkers {

  val parser = new Parser[EitherT[SyncIO, Error, ?]]

  it("success parse") {
    val res = parser.parse("B 100 15.40").value.unsafeRunSync()

    res should be ('right)
  }

  it("wrong string") {
    val res = parser.parse("B 100 15a.40").value.unsafeRunSync()

    res should be ('left)
  }
}
