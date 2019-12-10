package io.github.dsvdsv.auction

import eu.timepit.refined.types.numeric.{PosBigDecimal, PosInt}

package object domain {
  type Volume = PosInt
  type Amount = PosBigDecimal
}
