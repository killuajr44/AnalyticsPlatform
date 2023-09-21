name := "AnalyticsPlatform"

version := "0.1"

scalaVersion := "2.12.10" // Scala 2.12 est compatible avec Spark 3.x

val sparkVersion = "3.0.3" // Version de Spark
val deltaVersion = "0.8.0" // Version de Delta Lake compatible avec Spark 3.0.x

// Dépendances
libraryDependencies ++= Seq(
  // Dépendances pour Spark
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",

  // Dépendance pour Delta Lake
  "io.delta" %% "delta-core" % deltaVersion,

  // Dépendances pour les tests
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

// Options supplémentaires pour le build
//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
