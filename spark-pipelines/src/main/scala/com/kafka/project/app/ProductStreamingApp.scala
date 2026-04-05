package com.kafka.project.app

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types._


object ProductStreamingApp {

  def main(args: Array[String]) : Unit = {

    val spark = SparkSession.builder()
      .appName("Product Streaming App")
      .master("local[*]")
      .getOrCreate()

    //Define Schema
    val schema = new StructType()
      .add("id", StringType)
      .add("name", StringType)
      .add("category", StringType)
      .add("price", DoubleType)
      .add("rating", DoubleType)
      .add("description", StringType)
      .add("timestamp", LongType)
      .add("eventType", StringType)

    //Read From Kafka
    val kafkaDF = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "product-events")
      .load()

    //convert kafka value -> json
    val parsedDF = kafkaDF.selectExpr("CAST(value AS STRING)")
      .select(from_json(col("value"), schema).as("data"))
      .select("data.*")

    //write to elastic Search
    val query = parsedDF.writeStream
      .format("org.elasticsearch.spark.sql")
      .option("checkpointLocation", "/tmp/checkpoints/product")
      .option("es.nodes", "localhost")
      .option("es.port", 9200)
      .option("es.nodes.wan.only", "true")
      .option("es.mapping.id", "id") // IMPORTANT (upsert)
      .start("product-index")

    query.awaitTermination()

  }
}
