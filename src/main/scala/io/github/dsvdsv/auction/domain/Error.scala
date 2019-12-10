package io.github.dsvdsv.auction.domain

sealed trait Error extends Throwable with Product with Serializable

object Error {
  case class FormatError(reason: String) extends RuntimeException(reason) with Error
  case object SellNotPresentError        extends RuntimeException("Sell's not present") with Error
  case object BuyNotPresentError         extends RuntimeException("Buy's not present") with Error
  case object PriceNoMatchError          extends RuntimeException("b/s prices don't match") with Error
}
