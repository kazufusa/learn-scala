package dev.zio.quickstart

import zio._
import dev.zio.quickstart.models.Employee
import dev.zio.quickstart.models.Role
import caliban.GraphQL.graphQL
import caliban.{RootResolver, ZHttpAdapter}
import dev.zio.quickstart.models.Queries
import zhttp.http._
import zhttp.service.Server

object Main extends ZIOAppDefault {
  val employees = List(
    Employee("Alex", Role.DevOps),
    Employee("Maria", Role.SoftwareDeveloper),
    Employee("James", Role.SiteReliabilityEngineer),
    Employee("Peter", Role.SoftwareDeveloper),
    Employee("Julia", Role.SiteReliabilityEngineer),
    Employee("Robert", Role.DevOps)
  )

  def run =
    graphQL(
      RootResolver(
        Queries(
          args => employees.filter(e => args.role == e.role),
          args => employees.find(e => args.name == e.name)
        )
      )
    ).interpreter.flatMap { interpreter =>
      Server
        .start(
          port = 8088,
          http = Http.route {
            case _ -> !! / "api" / "graphql" => ZHttpAdapter.makeHttpService(interpreter)
          }
        )
        .forever
    }
}
