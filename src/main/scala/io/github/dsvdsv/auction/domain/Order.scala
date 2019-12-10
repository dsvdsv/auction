package io.github.dsvdsv.auction.domain

import cats.instances.bigDecimal._
import cats.{Order => CatsOrder}
import eu.timepit.refined.cats._
import monocle.Iso

sealed trait Order extends Product with Serializable {
  def direction: Direction
  def volume: Volume
  def price: Price
}

object Order {
  case class Sell(volume: Volume, price: Price) extends Order {
    def direction: Direction = Direction.Sell
  }

  object Sell {
    implicit val sellOrder: CatsOrder[Sell] =
      CatsOrder.reverse(CatsOrder.by(s => s.price))
  }

  case class Buy(volume: Volume, price: Price) extends Order {
    def direction: Direction = Direction.Buy
  }

  object Buy {
    implicit val buyOrder: CatsOrder[Buy] =
      CatsOrder.by(b => b.price)
  }

  val fromTupleIso =
    Iso[(Direction, Volume, Price), Order] {
      case (Direction.Buy, volume, amount)  => Buy(volume, amount)
      case (Direction.Sell, volume, amount) => Sell(volume, amount)
    }(o => (o.direction, o.volume, o.price))
}
