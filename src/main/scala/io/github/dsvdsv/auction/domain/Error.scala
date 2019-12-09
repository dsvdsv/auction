package io.github.dsvdsv.auction.domain

sealed trait Error extends Throwable with Product with Serializable

object Error {
  case class FormatError(reason: String) extends RuntimeException(reason) with Error
}
