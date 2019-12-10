package io.github.dsvdsv.auction.domain

import cats.MonadError
import cats.syntax.all._
import eu.timepit.refined.api.RefType
import io.github.dsvdsv.auction.domain.Error._

class Parser[F[_]](implicit F: MonadError[F, Error]) {

  private def validateDirection(s: String): F[Direction] =
    F.fromOption(Direction.fromTag(s), FormatError(s"Wrong direction format: $s"))

  private def validateVolume(s: String): F[Volume] = {
    val volumeOrErr = Either
      .catchOnly[NumberFormatException](s.toInt)
      .leftMap(_ => FormatError(s"volume: $s should be integer"))
      .flatMap { n =>
        RefType.applyRef[Volume](n).leftMap(FormatError(_))
      }

    volumeOrErr.pure[F].rethrow
  }

  private def validateAmount(s: String): F[Price] = {
    val amountOrErr = Either
      .catchOnly[NumberFormatException](BigDecimal(s))
      .leftMap(_ => FormatError(s"amount: $s should be bigdecimal"))
      .flatMap { n =>
        RefType.applyRef[Price](n).leftMap(FormatError(_))
      }
    amountOrErr.pure[F].rethrow
  }

  def parse(s: String): F[Order] =
    s.split(" ") match {
      case Array(d, v, a) =>
        (validateDirection(d), validateVolume(v), validateAmount(a)).mapN {
          case (direction, volume, amount) => Order.fromTupleIso.get((direction, volume, amount))
        }
      case _ => FormatError(s"Unexpected format of $s").raiseError[F, Order]
    }
}
