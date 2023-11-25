val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "removeGrid",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.sksamuel.scrimage" % "scrimage-core" % "4.1.1",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )

  )
