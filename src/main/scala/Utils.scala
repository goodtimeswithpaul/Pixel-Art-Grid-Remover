import com.sksamuel.scrimage.*
import com.sksamuel.scrimage.pixels.Pixel
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}

import java.lang.Math.sqrt
import scala.util.Random.shuffle
import java.io.File
import scala.annotation.tailrec

type Vector = (Int, Int, Int)
type Vector4D = (Int, Int, Int, Int)

def getNRandomInts(min: Int, max: Int, n: Int): List[Int] =
  shuffle(Range(min, max).toList).take(n)

def takeIndices[A](indices: List[Int], list: List[A]): List[A] =
  def takeIndicesCore(indicesLeft: List[Int], acc: List[A]): List[A] =
    indicesLeft match
      case Nil => acc.reverse
      case i :: xs => 
        takeIndicesCore(xs, list(i) :: acc)

  takeIndicesCore(indices, Nil)

def getRowsPixel(image: ImmutableImage): List[List[Pixel]] =
  def pixels = image.pixels().toList
  def rowSize = image.width
  @tailrec
  def getRowsCore(pixelsLeft: List[Pixel], rowsAcc: List[List[Pixel]]): List[List[Pixel]] =
    pixelsLeft match
      case Nil => rowsAcc.reverse
      case _ =>   getRowsCore(pixelsLeft.drop(rowSize), pixelsLeft.take(rowSize) :: rowsAcc)

  getRowsCore(pixels, Nil)

def getRowsVector(image: ImmutableImage): List[List[Vector]] =
  getRowsPixel(image).map(row => row.map(pixelToVector))


def computeCellMeanColor(xGrid: Int, yGrid: Int, cellSize: Int, image: ImmutableImage): Vector4D =
  val marginPercentage = 0.66
  val halfMargin = ((marginPercentage / 2) * cellSize).toInt

  val xImage = xGrid * cellSize + halfMargin
  val yImage = yGrid * cellSize + halfMargin

  val pixels =
    for
      y <- Range(yImage, (yGrid +1) * cellSize - halfMargin)
      x <- Range(xImage, (xGrid +1) * cellSize - halfMargin)
    yield pixelToVector4D(image.pixel(x, y))

//  println(s"${Range(xImage, xImage + cellSize - halfMargin)}")
//  println(s"${Range(yImage, yImage + cellSize - halfMargin)}")
//  println(halfMargin)
//  println(cellSize)

  val sum = pixels.foldLeft(new Vector4D(0, 0, 0, 0))(vector4DAdd)
  
  sum match
    case (a, b, c ,d) => (a / pixels.size, b / pixels.size, c / pixels.size, d / pixels.size)

def colorMean(color1: Vector4D, color2: Vector4D): Vector4D =
  val a = (color1(0) + color2(0)) / 2
  val r = (color1(1) + color2(1)) / 2
  val g = (color1(2) + color2(2)) / 2
  val b = (color1(3) + color2(3)) / 2
  (a, r, g, b)

def vector4DAdd(v1: Vector4D, v2: Vector4D): Vector4D =
  (v1(0) + v2(0), v1(1) + v2(1), v1(2) + v2(2), v1(3) + v2(3))


def euclideanDistance(v1: Vector, v2: Vector): Double =
  sqrt((v2(0)-v1(0))*(v2(0)-v1(0)) + (v2(1)-v1(1))*(v2(1)-v1(1)) + ((v2(2)-v1(2))*(v2(2)-v1(2))))

def pixelToVector(p: Pixel): Vector =
  (p.red(), p.green(), p.blue())

def pixelToVector4D(p: Pixel): Vector4D =
  (p.alpha() ,p.red(), p.green(), p.blue())

def vector4DToPixel(v: Vector4D, x: Int, y: Int): Pixel =
  new Pixel(x, y, v(1), v(2), v(3), v(0))

def proximityToBoolean(p: Vector): Boolean =
  if euclideanDistance(p, thrashPixel) < 1 then false else true

def andColorProximity(row1: List[Vector], row2: List[Vector]): List[Vector] =
  val threshold = 200
  row1.zip(row2).map({case (v1, v2) => if euclideanDistance(v1, v2) < threshold then v1 else thrashPixel})