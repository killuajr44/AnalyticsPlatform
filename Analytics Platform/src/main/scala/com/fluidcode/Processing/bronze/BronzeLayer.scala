//package com.fluidcode.processing.bronze

import com.fluidcode.configuration.Configuration
import org.apache.spark.sql.{Encoders, SparkSession}

class BronzeLayer {

  def createOrderBronzeTable(conf: Configuration, spark: SparkSession, path: String): Unit = {

    val orderBronzeData = spark.read
      .option("header", "true")
      .option("delimiter", ";") // Utilise le point-virgule comme délimiteur
      .csv(path)


    orderBronzeData
      .write
      .format("delta")
      .mode("append")
      .save(s"${conf.rootPath}/${conf.database}/${conf.orderBronzeTable}")
  }
}
