package io.github.dsvdsv.auction.domain

import cats.data._
import cats.effect.Sync
import cats.instances.bigDecimal._
import cats.instances.int._
import cats.instances.list._
import cats.instances.order._
import cats.syntax.all._
import cats.{MonadError, Order => CatsOrder}
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import fs2.Stream

import scala.collection.immutable.TreeSet

class Decider[F[_]] private (parser: Parser[F], reader: Reader[F])(implicit F: MonadError[F, Error]) {
  def chooseOptimal: F[Result] =
    for {
      lines  <- reader.read
      orders <- lines.traverse(parser.parse(_))

      // схлопываем дубликаты
      sells  <- mkSells(orders)
      buys   <- mkBuys(orders)

      result <- choose(sells, buys)
    } yield result

  // дублирование можно убрать при помощи shapeless
  private def mkBuys(orders: List[Order]): F[NonEmptySet[Order.Buy]] = {
    val treeSet = orders.collect { case x: Order.Buy  => x }
      .foldLeft(TreeSet.empty[Order.Buy]){ (set, n) =>
        set.find(_.price === n.price)
          .fold(set + n) { origin =>
            set + origin.copy(volume = origin.volume |+| n.volume)
          }
      }

    F.fromOption(NonEmptySet.fromSet(treeSet), Error.BuyNotPresentError)
  }

  private def mkSells(orders: List[Order]): F[NonEmptySet[Order.Sell]] = {
    val treeSet = orders.collect { case x: Order.Sell  => x }
      .foldLeft(TreeSet.empty[Order.Sell]){ (set, n) =>
        set.find(_.price === n.price)
          .fold(set + n) { origin =>
            set + origin.copy(volume = origin.volume |+| n.volume)
          }
      }

    F.fromOption(NonEmptySet.fromSet(treeSet), Error.SellNotPresentError)
  }

  private def choose(sells: NonEmptySet[Order.Sell], buys: NonEmptySet[Order.Buy])(implicit ordPrice: CatsOrder[Price], ordVolume: CatsOrder[Volume]): F[Result] = {
    if (ordPrice.gt(sells.head.price, buys.head.price)) {
      Error.PriceNoMatchError.raiseError[F, Result]
    } else {
      val price: BigDecimal = ordPrice.min(sells.head.price, buys.head.price)
      val buyVolumes = buys.map(_.volume).reduce
      Result(ordVolume.min(sells.head.volume, buyVolumes), price).pure[F]
    }
  }
}

object Decider {
  def fromReader[F[_]: MonadError[?[_], Error]](reader: Reader[F]): Decider[F] =
    new Decider[F](new Parser[F](), reader)

  def fromStream[F[_]: Sync: MonadError[?[_], Error]](stream: Stream[F, Byte]): Decider[F] =
    new Decider[F](new Parser[F](), Reader[F](stream))

}
