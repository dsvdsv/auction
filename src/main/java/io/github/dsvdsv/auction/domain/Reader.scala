package io.github.dsvdsv.auction.domain

import java.nio.file.Path

import cats.effect.{Blocker, ContextShift, Sync}
import fs2.{io, text, Stream}

class Reader[F[_]: Sync] private (stream: Stream[F, Byte]) {
  def apply(): F[List[String]] =
    stream.through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty)
      .compile
      .toList
}

object Reader {
  private val defaultChunkSize = 4096

  def fromStdin[F[_]: Sync: ContextShift](chunkSize: Int = defaultChunkSize): Reader[F] =
    Reader[F](
      Stream.resource(Blocker[F]).flatMap(io.stdin[F](chunkSize, _))
    )

  def fromFile[F[_]: Sync: ContextShift](path: Path, chunkSize: Int = defaultChunkSize): Reader[F] =
    Reader[F](
      Stream.resource(Blocker[F]).flatMap(io.file.readAll[F](path, _, chunkSize))
    )

  def apply[F[_]: Sync](stream: Stream[F, Byte]): Reader[F] =
    new Reader[F](stream)
}
