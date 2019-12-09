package io.github.dsvdsv.auction.domain

import cats.effect.Sync

class Parser[F[_]: Sync] {
  def apply(s: String): F[Order]
}

object Parser {
  private val regex = """([B,S])\s+([0-9])+\s+(?:\d*\.)?\d+""".r
}
