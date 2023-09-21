package com.fluidcode.configuration

import com.fluidcode.configuration.Configuration._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, SparkSession}
import java.io.FileNotFoundException
import com.fluidcode.models._
import org.apache.spark.sql.streaming.Trigger




// TODO: move to  the right place (TBD)
case class TableProperties(database: String, table: String, location: String)

// TODO: use one database, need to be re-evaluated after the finishing the modeling exercise
// TODO: class will be renamed: <project_nameConfiguration>
case class Configuration(
                          rootPath: String,
                          database: String,
                          checkpointDir: Path,
                          trigger: Trigger,
                          orderBronzeTable: String,
                          OrdersTable: String,
                          customerTable: String,
                          biggestSpendersTable: String,
                          MostSoldProductTable: String,
                        ) {
  def init(spark: SparkSession, overwrite: Boolean = false): Unit = {
    // TODO: check if init is done successfully
    initDatabase(spark, overwrite)
    initCheckpointDir(overwrite)
    initorderBronzeTable(spark, overwrite)
    initOrdersTable(spark, overwrite)
    initcustomerTable(spark, overwrite)
    initbiggestSpendersTable(spark, overwrite)
    initMostSoldProductTable(spark, overwrite)
  }

  def initDatabase(spark: SparkSession, overwrite: Boolean = false): Boolean = {
    val dbLocation = new Path(s"$rootPath/$database")
    val fs = getFileSystem(new Path(rootPath))
    fs.mkdirs(dbLocation)
    if (overwrite) {
      spark.sql(s"drop database if exists $database cascade")
      // TODO: use logger instead
      println(s"database $database successfully dropped")

      spark.sql(s"create database if not exists $database location '${dbLocation.toUri}'")
      // TODO: use logger instead
      println(s"database $database successfully created")
      true
    }
    else if (!spark.catalog.databaseExists(database)) {
      spark.sql(s"create database if not exists $database location '${dbLocation.toUri}'")
      // TODO: use logger instead
      println(s"database $database successfully created")
      true
    }
    else {
      // TODO: use logger instead
      println(s"database $database already exists")
      false
    }
  }


  def initorderBronzeTable(spark: SparkSession, overwrite: Boolean = false): Boolean = {
    import spark.implicits._
    val location = s"$rootPath/$database/$orderBronzeTable"
    val tableProperties = TableProperties(database, orderBronzeTable, location)
    val emptyConf: Seq[OrderBronze] = Seq()
    createTable(spark, emptyConf.toDF(), tableProperties, partitionColumns = null, overwrite)
  }

  def initOrdersTable(spark: SparkSession, overwrite: Boolean = false): Boolean = {
    import spark.implicits._
    val location = s"$rootPath/$database/$OrdersTable"
    val tableProperties = TableProperties(database, OrdersTable, location)
    val emptyConf: Seq[Orders] = Seq()
    createTable(spark, emptyConf.toDF(), tableProperties, partitionColumns = null, overwrite)
  }

  def initcustomerTable(spark: SparkSession, overwrite: Boolean = false): Boolean = {
    import spark.implicits._
    val location = s"$rootPath/$database/$customerTable"
    val tableProperties = TableProperties(database, customerTable, location)
    val emptyConf: Seq[customers] = Seq()
    createTable(spark, emptyConf.toDF(), tableProperties, partitionColumns = null, overwrite)
  }

  def initbiggestSpendersTable(spark: SparkSession, overwrite: Boolean = false): Boolean = {
    import spark.implicits._
    val location = s"$rootPath/$database/$biggestSpendersTable"
    val tableProperties = TableProperties(database, biggestSpendersTable, location)
    val emptyConf: Seq[BiggestSpenders] = Seq()
    createTable(spark, emptyConf.toDF(), tableProperties, partitionColumns = null, overwrite)
  }

  def initMostSoldProductTable(spark: SparkSession, overwrite: Boolean = false): Boolean = {
    import spark.implicits._
    val location = s"$rootPath/$database/$MostSoldProductTable"
    val tableProperties = TableProperties(database, MostSoldProductTable, location)
    val emptyConf: Seq[MostSoldProduct] = Seq()
    createTable(spark, emptyConf.toDF(), tableProperties, partitionColumns = null, overwrite)
  }



  def initCheckpointDir(overwrite: Boolean): Boolean = {
    mkdir(checkpointDir, overwrite)
  }
}

object Configuration {
  // TODO: names TBD
  val DATABASE = "watcher_db"
  val CHECKPOINT_DIR = "checkpoint_dir"
  val ORDERBRONZE_TABLE = "OrderBronze"
  val ORDERS_TABLE = "Orders_dim"
  val CUSTOMERS_TABLE = "Customers_dim"
  val BIGGESTSPENDERS_TABLE = "biggestSpenders_dim"
  val MOSTSOLDPRODUCT_TABLE = "mostSoldProduct_dim"


  def apply(basePath: String): Configuration = {
    val path = new Path(basePath)
    val fs = getFileSystem(path)
    if (!fs.exists(path)) {
      throw new FileNotFoundException(s"No such file or directory: $path")
    }
    val checkpointDir = new Path(s"${path.toString}/$CHECKPOINT_DIR")
    val trigger = Trigger.Once
    Configuration(
      basePath,
      DATABASE,
      checkpointDir,
      trigger,
      ORDERBRONZE_TABLE,
      ORDERS_TABLE,
      CUSTOMERS_TABLE,
      BIGGESTSPENDERS_TABLE,
      MOSTSOLDPRODUCT_TABLE,
    )
  }

  // TODO: move the right place (TBD)
  def getFileSystem(path: Path): FileSystem = {
    val hadoopConfiguration = SparkSession.getActiveSession.map(x => x.sessionState.newHadoopConf()).get
    path.getFileSystem(hadoopConfiguration)
  }

  def getHadoopConfiguration: org.apache.hadoop.conf.Configuration = {
    SparkSession.getActiveSession.map(x => x.sessionState.newHadoopConf()).get
  }

  def makeQualified(path: Path): String = {
    path.getFileSystem(getHadoopConfiguration).makeQualified(path).toString
  }

  // TODO: user logger inside function
  def createTable(
                   spark: SparkSession,
                   df: DataFrame,
                   tableProperties: TableProperties,
                   partitionColumns: Option[Seq[String]] = null,
                   overwrite: Boolean = false): Boolean = {
    val database = tableProperties.database
    val table = tableProperties.table
    val location = tableProperties.location

    if (!spark.catalog.databaseExists(database)) {
      val dbLocation = new Path(location).getParent.toString
      spark.sql(s"create database if not exists $database location '${dbLocation}'")
    }
    if (!spark.catalog.tableExists(s"$database.$table") || overwrite) {
      persist(df, tableProperties, partitionColumns)
      true
    }
    else {
      false
    }
  }

  def persist(df: DataFrame,
              tableProperties: TableProperties,
              partitionColumns: Option[Seq[String]]): Unit = {
    if (partitionColumns == null) {
      df
        .write
        .format("delta")
        .mode("overwrite")
        .option("path", s"${tableProperties.location}")
        .saveAsTable(s"${tableProperties.database}.${tableProperties.table}")
    }
    else {
      df
        .write
        .format("delta")
        .mode("overwrite")
        .option("path", s"${tableProperties.location}")
        .partitionBy(partitionColumns.get: _*)
        .saveAsTable(s"${tableProperties.database}.${tableProperties.table}")
    }
  }

  // TODO: use logger inside function
  def mkdir(path: Path, overwrite: Boolean = false): Boolean = {
    val fs = getFileSystem(path)
    if (overwrite) {
      fs.delete(path, true)
      fs.mkdirs(path)
    }
    else if (!fs.exists(path)) {
      fs.mkdirs(path)
    }
    else {
      false
    }
  }
}