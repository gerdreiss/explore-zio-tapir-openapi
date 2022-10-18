val scala3Version = "3.2.0"

val tapirVersion = "1.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "explore-zio-tapir-openapi",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
    )
  )
