package com.fluidcode.processing.silver

import com.fluidcode.configuration.Configuration
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
//import com.fluidcode.processing.silver.CommentsTableUtils._
class ordersTable(spark: SparkSession, conf: Configuration) {
  def createOrdersTable(conf: Configuration, spark: SparkSession, path: String): Unit = {

    val ordersData = spark.read
      .option("header", "true")
      .option("delimiter", ";") // Utilise le point-virgule comme délimiteur
      .csv(path)


  }
}