package jp.tooppoo.isbn

import jp.tooppoo.isbn.service.BookLoadService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Main extends App {
  val isbnList = Source.stdin.getLines.toArray

  for { output <- BookLoadService.withGoogle.load(isbnList) }
    println(output)
}
