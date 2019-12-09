package io.github.dsvdsv.auction

import eu.timepit.refined._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric._
import eu.timepit.refined.types.numeric.{PosBigDecimal, PosInt}

package object domain {
  type Volume = PosInt And Greater[W.`0.0`.T]
  type Amount = PosBigDecimal And Greater[W.`0.0`.T]
}
