package io.github.dsvdsv.auction.domain

import cats.effect.IO
import fs2.Chunk.ByteVectorChunk
import fs2.Stream
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.scalacheck.Checkers
import scodec.bits.ByteVector

@RunWith(classOf[JUnitRunner])
class ReaderSpec extends FunSpec with Matchers with Checkers {

  it("read from stream") {
    val stream = Stream.chunk(ByteVectorChunk(ByteVector.view(
      "B 100 15.40\n\nB 100 15.30\nS 150 15.30\n\n".getBytes()
    ))).covary[IO]

    val reader = Reader(stream)

    reader.read.unsafeRunSync() should contain allOf ("B 100 15.40", "B 100 15.30", "S 150 15.30")
  }
}
