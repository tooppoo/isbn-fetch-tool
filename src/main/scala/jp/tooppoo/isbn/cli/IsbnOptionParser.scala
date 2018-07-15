package jp.tooppoo.isbn.cli


object IsbnOptionParser {
  type InvalidConfigMessage = String

  case class Config(apiKey: Option[String] = None, source: String = "") {
    def build(): Either[Seq[InvalidConfigMessage], Config] = {
      if (source.isEmpty) {
        Left(Seq(
          "source file path is required"
        ))
      } else {
        Right(this)
      }
    }
  }

  private val keyPattern = """--key=(.+)""".r

  def parse(args: Seq[String]): Config = {
    args.foldLeft[Config](Config()) { (conf, param) =>
      param match {
        case keyPattern(key) => conf.copy(apiKey = Some(key))
        case _ if conf.source.isEmpty => conf.copy(source = param)
        case _ => conf
      }
    }
  }
}
