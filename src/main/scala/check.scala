import cats.effect.IO
import cats.implicits._

object check {
  def apply[A](result: A): Assert[A] = {
    println(result)
    new Assert(result)
  }

  def potentiallyFailing[A](code: => A): Assert[A] = {
    val result = retry(IO(code), 100).unsafeRunSync()
    println(result)
    new Assert(result)
  }

  class Assert[A](result: A) {
    def expect(expected: A): A = {
      assert(result == expected)
      result
    }

    def expect(checkResult: A => Boolean): A = {
      assert(checkResult(result))
      result
    }
  }

  private def retry[A](action: IO[A], maxRetries: Int): IO[A] = {
    List.range(0, maxRetries).foldLeft(action)((program, _) => program.orElse(action))
  }
}