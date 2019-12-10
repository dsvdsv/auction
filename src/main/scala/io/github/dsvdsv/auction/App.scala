package io.github.dsvdsv.auction

import java.nio.file.Paths

import cats.data.EitherT
import cats.effect._
import cats.implicits._
import io.github.dsvdsv.auction.domain._

object App extends IOApp {
  type Stack[A] = EitherT[IO, Error, A]

  def run(args: List[String]): IO[ExitCode] = {
    val reader = args.headOption.fold(Reader.fromStdin[Stack]()) { name =>
      Reader.fromFile[Stack](Paths.get(name))
    }

    val decider = Decider.fromReader(reader)

    decider.chooseOptimal.value
      .flatMap {
        case Right(Result(v, p))           => IO(println(s"$v $p"))
        case Left(Error.PriceNoMatchError) => IO(println("0 n/a"))
        case Left(ex)                      => IO(println(ex.getMessage))
      }
      .as(ExitCode.Success)

  }
}
