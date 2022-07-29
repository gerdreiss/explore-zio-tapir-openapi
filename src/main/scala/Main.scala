import sttp.tapir.PublicEndpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import sttp.tapir.ztapir.*

import zhttp.http.Http
import zhttp.http.Request
import zhttp.http.Response
import zhttp.service.Server

import zio.*
import sttp.tapir.Endpoint

object Main extends ZIOAppDefault:

  val PORT = 8080

  val helloEndpoint: PublicEndpoint[Unit, String, String, Any] =
    endpoint.in("hello").get.out(stringBody).errorOut(stringBody)

  val lengthEndpoint: PublicEndpoint[String, String, Int, Any] =
    endpoint.in("length").post.in(stringBody).out(plainBody[Int]).errorOut(stringBody)

  val serverEndpoints: List[ServerEndpoint[Any, Task]] =
    List(
      helloEndpoint.zServerLogic(_ => ZIO.succeed("Hello, world!")),
      lengthEndpoint.zServerLogic(input => ZIO.succeed(input.length))
    )

  val swaggerEndpoints: List[ServerEndpoint[Any, Task]] =
    SwaggerInterpreter()
      .fromEndpoints[Task](
        List(helloEndpoint, lengthEndpoint),
        "ZIO Tapir OpenAPI Explorer",
        "1.0"
      )

  val httpApp: Http[Any, Throwable, Request, Response] =
    ZioHttpInterpreter().toHttp(serverEndpoints ++ swaggerEndpoints)

  override def run: ZIO[Any, Any, Any] =
    Server.start(PORT, httpApp)
