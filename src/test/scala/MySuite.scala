import com.sksamuel.scrimage.ImmutableImage

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class MySuite extends munit.FunSuite {
  // test("column size is correctly guessed") {
  //   var failNumber = 0f
  //   val n = 10
  //   for (n <- Range(0, n)) do
  //     val res = guessColumnSize(ImmutableImage.loader().fromFile("assets/totoro.jpeg"))
  //     if res != 21 then {
  //       failNumber += 1
  //       println("result was " + res)
  //     }

    
  //   println("Algorith succeeded in " + (1 - (failNumber / n))*100 + "% of the tests")
  // }

  // test("remove grid"){
  //   removeGrid("assets/totoro.jpeg")
  // }

  test("remove grid"){
    removeGrid("assets/pikachu.jpeg")
  }

  // test("remove grid"){
  //   removeGrid("assets/crocmou.jpeg")
  // }
  
}
