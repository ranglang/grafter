package org.zalando.conf4s

import shapeless._

trait FromConfig[A, B] {

  def apply(a: A): B
}

object FromConfig {

  def create[A, B](a: A)(implicit fc: FromConfig[A, B]): B = fc.apply(a)

  implicit def hnilFromConfigA[A]: FromConfig[A, HNil] = new FromConfig[A, HNil] {
    def apply(a: A): HNil =  HNil
  }

  implicit def aFromConfigHCons[A, H, T <: HList](implicit
    hFromConfigA: FromConfig[A, H],
    tFromConfigA: FromConfig[A, T]
  ): FromConfig[A, H :: T] = new FromConfig[A, H :: T] {

    def apply(a: A): H :: T = {
      val h = hFromConfigA(a)
      val t = tFromConfigA(a)
      h :: t
    }
  }

  implicit def genericFromConfig[A, B, ReprB](implicit
    gen: Generic.Aux[B, ReprB],
    reprbFromConfigA: FromConfig[A, ReprB]
  ): FromConfig[A, B] = new FromConfig[A, B] {

    def apply(a: A): B = {
      val internal = reprbFromConfigA(a)
      gen.from(internal)
    }
  }
}
