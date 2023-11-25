import com.sksamuel.scrimage.*
import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.*

import java.lang.Math.sqrt
import scala.util.Random.shuffle
import java.io.File
import javax.imageio.ImageIO.*
import io.AnsiColor._


val thrashPixel: Vector = (-255, -255, -255) // A color we are sure not to encounter

def guessColumnSize(image: ImmutableImage): Int =
  val rows = getRowsVector(image)

  // Result of the AND operation on 100 random rows of the image to extract the grid
  val randomSetOfRows = takeIndices(Range(0, 100).toList, rows)
  val residualRow = randomSetOfRows.foldLeft(randomSetOfRows.head)(andColorProximity).map(proximityToBoolean)

  println(residualRow)

  // Computing the size of each column
  val computedColumnSizes = residualRow.foldLeft(List(0))((acc, x) => {
    x match
      case false => (acc.head + 1) :: acc.tail
      case true => 1 :: acc
  }).tail

  // println(computedColumnSizes)

  // Inferring the size of a column by grouping same results together
  val guessedColumnSize = computedColumnSizes.groupBy(identity).map((k, v) => (k, v.size)).toList.sortWith((a,b) => a(1) > b(1))

  val first = guessedColumnSize(0)
  val second = guessedColumnSize(1)

  // This is a correction that helps in the case where there are enough misses 
  // to make the double columns more frequent that the actual columns
  val correctedNumberOfFirstResult = 
    if second(0) == 2*first(0) then first(1) + 2*second(1) else
      if second(0)*2 == first(0) then 2*first(1) + second(1) else
        first(1)




  val res = if correctedNumberOfFirstResult == first(1) then first(0) else
  ((if second(0)*2 == first(0) then second(0) else first(0), correctedNumberOfFirstResult) :: guessedColumnSize.drop(2)).sortWith((a,b) => a(1) > b(1))(0)(0)

  res

@main def removeGrid(file: String): Unit =
  val imageInput = ImmutableImage.loader().fromFile(file)

  val cellSize = guessColumnSize(imageInput)
  val numberOfColumns = imageInput.width / cellSize
  val numberOfRows = imageInput.height / cellSize
  print(numberOfColumns)

  val rows = getRowsPixel(imageInput)

  val pixels = for
    y <- Range(0, numberOfRows)
    x <- Range(0, numberOfColumns)
  yield vector4DToPixel(computeCellMeanColor(x, y, cellSize, imageInput), x, y)

  val imageOutput = ImmutableImage.create(numberOfColumns, numberOfRows, pixels.toArray)

  val writer = new PngWriter()
  imageOutput.output(writer, new File("test.png"))


  





