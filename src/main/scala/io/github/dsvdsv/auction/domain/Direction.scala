package io.github.dsvdsv.auction.domain

import cats.Eq
import cats.instances.string._
import cats.syntax.eq._

sealed abstract class Direction(val tag: String) extends Product with Serializable

object Direction {
  case object Buy  extends Direction("B")
  case object Sell extends Direction("S")

  val all = List(Buy, Sell)

  def fromTag(s: String) =
    all.find(_.tag === s)

  def unsafeFromTag(s: String) =
    fromTag(s).getOrElse(throw new NoSuchElementException(s"Direction: Invalid tag: '$s'"))

  implicit val eqDirection: Eq[Direction] =
    Eq.fromUniversalEquals
}
