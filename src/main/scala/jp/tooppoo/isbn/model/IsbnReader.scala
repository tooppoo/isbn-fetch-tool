package jp.tooppoo.isbn.model

import scala.io.Source

trait IsbnReader {
  def open[T](callback: Seq[String] => T): T
}

class FileReader(src: String) extends IsbnReader {
  def open[T](callback: Seq[String] => T): T = {
    val file = Source.fromFile(src, "utf-8")

    try {
      val rows = file.getLines.toArray

      callback(rows)
    } finally {
      file.close
    }
  }
}

object IsbnReader {
  def fromFile[T](src: String) = new FileReader(src).open[T] _
}