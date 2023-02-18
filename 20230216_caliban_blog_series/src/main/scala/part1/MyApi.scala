package part1

import part1.Data._
import caliban.schema.Schema
import java.net.URL
import caliban.schema.ArgBuilder
import caliban.GraphQL.graphQL
import caliban.CalibanError
import scala.util.Try
import zio.IO
import zio.UIO
import caliban.RootResolver

object MyApi {

  // support for URL
  implicit val urlSchema: Schema[Any, URL] = Schema.stringSchema.contramap(_.toString)
  implicit val urlArgBuilder: ArgBuilder[URL] = ArgBuilder.string.flatMap(url =>
    Try(new URL(url)).fold(_ => Left(CalibanError.ExecutionError(s"invalid URL $url")), Right(_))
  )

  // API definition
  case class FindPugArgs(name: String)
  case class AddPugArgs(pug: Pug)
  case class EditPugPictureArgs(name: String, pictureUrl: URL)
  case class Queries(
      findPug: FindPugArgs => IO[PugNotFound, Pug],
      randomPugPicture: UIO[String]
  )
  case class Mutations(
      addPug: AddPugArgs => UIO[Unit],
      editPugPicture: EditPugPictureArgs => IO[PugNotFound, Unit]
  )

  // resolvers
  val queries = Queries(
    args => PugService.dummy.findPug(args.name),
    PugService.dummy.randomPugPicture
  )
  val mutations = Mutations(
    args => PugService.dummy.addPug(args.pug),
    args => PugService.dummy.editPugPicture(args.name, args.pictureUrl)
  )

  // interpreter
  val interpreter = graphQL(RootResolver(queries, mutations)).interpreter
}
