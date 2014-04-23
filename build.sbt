name := "GameOfLifePlay"

version := "1.0-SNAPSHOT"

resolvers += "HTWG Resolver" at "http://lenny2.in.htwg-konstanz.de:8081/artifactory/libs-snapshot-local"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "GameOfLife" % "de.htwg.sa.gameoflife" % "0.0.1-SNAPSHOT"
)     

play.Project.playJavaSettings
