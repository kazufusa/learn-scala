package dev.zio.quickstart.users

import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder
import zio.json.JsonDecoder
import zio.json.DeriveJsonDecoder

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}
