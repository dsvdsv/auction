package io.github.dsvdsv.auction.domain

import cats.data.EitherT
import cats.effect.SyncIO
import fs2.Chunk.ByteVectorChunk
import fs2.Stream
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.scalacheck.Checkers
import scodec.bits.ByteVector

@RunWith(classOf[JUnitRunner])
class DeciderSpec extends FunSpec with Matchers with Checkers {

  val validInput = "B 20 15.40\nB 100 15.40\n\nB 100 15.30\nS 150 15.30\n\nS 200 15.20"
  val invalidInput = "B 100 10.00\nS 150 10.10"

  type Stack[A] = EitherT[SyncIO, Error, A]

  it("success choose") {
    val stream = Stream.chunk(ByteVectorChunk(ByteVector.view(
      validInput.getBytes()
    ))).covary[Stack]

    val decider = Decider.fromStream(stream)

    val res = decider.chooseOptimal.value.unsafeRunSync()

    res should be (Right(Result(150, BigDecimal(15.30))))
  }

  it("fail when try to choose") {
    val stream = Stream.chunk(ByteVectorChunk(ByteVector.view(
      invalidInput.getBytes()
    ))).covary[Stack]

    val decider = Decider.fromStream(stream)

    val res = decider.chooseOptimal.value.unsafeRunSync()

    res should be ('left)
  }
}
