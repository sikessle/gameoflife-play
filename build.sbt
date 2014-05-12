name := "GameOfLifePlay"

version := "1.0-SNAPSHOT"

resolvers += "HTWG Resolver" at "http://lenny2.in.htwg-konstanz.de:8081/artifactory/libs-snapshot-local"

resolvers += "Db4o Resolver" at "http://source.db4o.com/maven"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "GameOfLife" % "de.htwg.sa.gameoflife" % "0.0.1-SNAPSHOT"
)

play.Project.playJavaSettings
