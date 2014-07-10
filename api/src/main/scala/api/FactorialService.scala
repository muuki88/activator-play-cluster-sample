package api

object FactorialService {

  case class Compute(n: Int)
  case class Result(result: BigInt)
}