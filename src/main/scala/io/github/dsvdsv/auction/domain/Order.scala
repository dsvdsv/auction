package io.github.dsvdsv.auction.domain

case class Order(
    direction: Direction,
    volume: Volume,
    amount: Amount
)
