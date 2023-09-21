// Import des packages nécessaires
import com.fluidcode.configuration.Configuration
import org.apache.spark.sql.SparkSession

// Définition de l'objet Bronze
object Bronze {

  // Définition de la méthode main
  def main(args: Array[String]): Unit = {

    // Initialisation de la session Spark
    val spark: SparkSession = SparkSession.builder()
      .appName("AnalyticsPlatform")
      .getOrCreate()

    // Initialisation de la configuration
    val conf = Configuration(args(0))
    conf.init(spark)

    // Chemin vers le fichier CSV à lire (second argument de la ligne de commande)
    val csvPath = args(1)

    // Création de la couche Bronze
    val bronzeLayer = new BronzeLayer()

    // Appel de la méthode pour créer la table Bronze
    bronzeLayer.createOrderBronzeTable(conf, spark, csvPath)
  }
}
