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

object Main extends ZIOAppDefault:

  val helloEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.in("hello").get.out(stringBody)

  val lengthEndpoint: PublicEndpoint[String, Unit, Int, Any] =
    endpoint.in("length").post.in(stringBody).out(plainBody[Int])

  val swaggerEndpoints: List[ServerEndpoint[Any, Task]] =
    SwaggerInterpreter()
      .fromServerEndpoints[Task](
        List(
          helloEndpoint.zServerLogic(_ => ZIO.succeed("Hello, world!")),
          lengthEndpoint.zServerLogic(input => ZIO.succeed(input.length))
        ),
        "ZIO Tapir OpenAPI Explorer",
        "1.0"
      )

  val swaggerRoute: Http[Any, Throwable, Request, Response] =
    ZioHttpInterpreter().toHttp(swaggerEndpoints)

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    Server.start(8080, swaggerRoute)
