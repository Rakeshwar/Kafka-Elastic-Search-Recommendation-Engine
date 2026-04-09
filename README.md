# 🔍 Kafka · Elasticsearch · Recommendation Engine

> A production-grade, real-time recommendation and search engine built with Apache Kafka, Elasticsearch, Apache Spark, and a polyglot microservices architecture (Java · Python · Scala).

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Python](https://img.shields.io/badge/Python-3.x-blue?style=flat-square&logo=python)](https://www.python.org/)
[![Scala](https://img.shields.io/badge/Scala-2.13-red?style=flat-square&logo=scala)](https://www.scala-lang.org/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black?style=flat-square&logo=apachekafka)](https://kafka.apache.org/)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-teal?style=flat-square&logo=elasticsearch)](https://www.elastic.co/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat-square&logo=docker)](https://docs.docker.com/compose/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

---

## 📑 Table of Contents

1. [About the Project](#-about-the-project)
2. [What Problem Does It Solve?](#-what-problem-does-it-solve)
3. [Tech Stack](#-tech-stack)
4. [System Requirements](#-system-requirements)
5. [Architecture Overview](#-architecture-overview)
6. [Data Flow](#-data-flow)
7. [Project Structure](#-project-structure)
8. [Module Breakdown](#-module-breakdown)
   - [producer](#1-producer-javapython)
   - [spark-pipelines](#2-spark-pipelines-scala)
   - [search-service](#3-search-service-java--spring-boot)
   - [dockerConfiguration](#4-dockerconfiguration)
9. [API Reference](#-api-reference)
10. [Local Setup with Docker](#-local-setup-with-docker)
11. [Environment Variables](#-environment-variables)
12. [Extending the System](#-extending-the-system)
13. [Roadmap](#-roadmap)
14. [Contributing](#-contributing)
15. [License](#-license)

---

## 📖 About the Project

This project is a **scalable, event-driven recommendation engine** that combines the power of:

- **Apache Kafka** for fault-tolerant, high-throughput event streaming
- **Apache Spark** for distributed stream processing and ML pipeline execution
- **Elasticsearch** for sub-millisecond full-text search, indexing, and vector-based recommendation queries
- **Spring Boot** for serving search and recommendation APIs

It demonstrates a complete end-to-end data pipeline: from raw user/product events entering the system, through real-time transformation via Spark, to indexed Elasticsearch documents that power search and recommendation APIs.

This is ideal as a reference architecture for e-commerce platforms, content platforms, or any system that needs **real-time personalization at scale**.

---

## 🎯 What Problem Does It Solve?

In a modern data-intensive product (e-commerce, content streaming, etc.), two core needs emerge:

**1. Product Search** — users need fast, relevant search results filtered by category, price, and keyword.

**2. Product Recommendations** — users need personalized "you might also like" results based on item similarity or behavioral signals.

Solving these at scale requires:
- Decoupled ingestion (producers shouldn't wait for consumers)
- A streaming layer that transforms raw events in real time
- An indexing layer that serves queries at millisecond latency

This project solves all three by wiring together Kafka → Spark → Elasticsearch → REST API in a containerized microservices architecture.

---

## 🛠 Tech Stack

| Layer | Technology | Language |
|---|---|---|
| Event Ingestion | Apache Kafka + ZooKeeper | — |
| Event Producer | Kafka Producer Client | Java / Python |
| Stream Processing | Apache Spark Structured Streaming | Scala |
| Storage & Search | Elasticsearch | — |
| API Layer | Spring Boot REST | Java |
| Containerization | Docker + Docker Compose | — |
| CI/CD | GitHub Actions | — |

---

## 💻 System Requirements

Before running locally, ensure the following are installed:

| Requirement | Minimum Version | Notes |
|---|---|---|
| Docker Desktop | 24.x+ | Includes Docker Compose v2 |
| Docker Compose | 2.x+ | Use `docker compose` (not `docker-compose`) |
| RAM | 8 GB minimum | Kafka + Spark + ES is memory-intensive; 16 GB recommended |
| Disk Space | 5 GB free | For Docker images and Elasticsearch data |
| CPU | 4 cores | Spark benefits greatly from multiple cores |
| JDK (optional) | 17 (Eclipse Temurin) | Only needed for local builds outside Docker |
| Python (optional) | 3.8+ | Only needed if running producer scripts natively |

> **Note:** All services are fully containerized. You do **not** need Kafka, Elasticsearch, or Spark installed locally — Docker handles everything.

---

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Data Producers                           │
│         Java Producer  ·  Python Producer  ·  Sample Data      │
└───────────────────────────────┬─────────────────────────────────┘
                                │  user events / product events
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                     Apache Kafka Cluster                       │
│                                                               │
│   ┌─────────────────┐    ┌──────────────────────────────┐    │
│   │    ZooKeeper    │    │   Kafka Broker (port 9092)   │    │
│   │ (coordination)  │◄──►│   Topics: events, products   │    │
│   └─────────────────┘    └──────────────────────────────┘    │
└───────────────────────────────┬───────────────────────────────┘
                                │  stream of records
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                     Spark Pipelines (Scala)                    │
│                                                               │
│   Structured Streaming · Transformations · ML Features        │
│   Reads from Kafka topic → transforms → writes to ES         │
└───────────────────────────────┬───────────────────────────────┘
                                │  indexed documents
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                      Elasticsearch (port 9200)                 │
│                                                               │
│   Indices: products, recommendations, user-events            │
│   Full-text search · Similarity queries · Aggregations       │
└───────────────────────────────┬───────────────────────────────┘
                                │  query / fetch results
                                ▼
┌───────────────────────────────────────────────────────────────┐
│               search-service  (Spring Boot · port 8080)        │
│                                                               │
│   GET /search/product      →  full-text + filter search      │
│   GET /recommend/product   →  content-based recommendations  │
└───────────────────────────────┬───────────────────────────────┘
                                │  JSON response
                                ▼
                          API Consumers
                    (Frontend · Mobile · B2B)
```

---

## 🔄 Data Flow

```
User/System Activity
        │
        │  (click, view, purchase, product update)
        ▼
┌──────────────┐
│   Producer   │  ──── publishes event to Kafka topic ────►
└──────────────┘
                                                    ┌──────────┐
                                                    │  Kafka   │
                                                    │  Topic   │
                                                    └────┬─────┘
                                                         │ consumed by
                                                         ▼
                                               ┌──────────────────┐
                                               │  Spark Pipeline  │
                                               │  - Parse JSON    │
                                               │  - Enrich/Filter │
                                               │  - Compute ML    │
                                               │    features      │
                                               └────────┬─────────┘
                                                        │ writes to
                                                        ▼
                                               ┌──────────────────┐
                                               │  Elasticsearch   │
                                               │  - Index docs    │
                                               │  - Store vectors │
                                               └────────┬─────────┘
                                                        │ queried by
                                                        ▼
                                               ┌──────────────────┐
                                               │  search-service  │
                                               │  Spring Boot API │
                                               └────────┬─────────┘
                                                        │
                                                        ▼
                                                  API Response
                                          (search results / recommendations)
```

**Sequence summary:**

1. An event (product view, purchase, new product listing) is published by the **Producer** to a Kafka topic.
2. **Spark Pipelines** consume the Kafka topic in a structured streaming job, clean/enrich the data, and write it into Elasticsearch.
3. **Elasticsearch** indexes the documents, enabling both keyword search and similarity-based recommendation lookups.
4. The **search-service** Spring Boot API exposes REST endpoints that query Elasticsearch and return ranked results to callers.

---

## 📁 Project Structure

```
Kafka-Elastic-Search-Recommendation-Engine/
│
├── .github/
│   └── workflows/                  # GitHub Actions CI/CD pipeline definitions
│
├── dockerConfiguration/            # Docker Compose and service config files
│   ├── docker-compose.yml          # Orchestrates all services
│   ├── kafka/                      # Kafka broker config
│   ├── elasticsearch/              # ES cluster settings
│   └── zookeeper/                  # ZooKeeper settings
│
├── producer/                       # Event producer module
│   ├── src/                        # Java producer source
│   ├── generate_sample_data.py     # Python script to seed demo data
│   └── pom.xml / requirements.txt
│
├── spark-pipelines/                # Apache Spark streaming jobs (Scala)
│   ├── src/
│   │   └── main/scala/
│   │       ├── KafkaConsumer.scala     # Reads from Kafka topic
│   │       ├── Transformer.scala       # Data transformation logic
│   │       └── ElasticWriter.scala     # Writes processed data to ES
│   └── build.sbt
│
├── search-service/                 # Spring Boot REST API (Java)
│   ├── src/
│   │   └── main/java/
│   │       ├── controller/         # REST controllers
│   │       ├── service/            # Business logic (search + recommend)
│   │       ├── model/              # Data models / DTOs
│   │       └── config/             # ES client configuration
│   ├── target/                     # Compiled JAR (used by Dockerfile)
│   └── pom.xml
│
├── .gitignore
├── Dockerfile                      # Builds the search-service container
└── README.md
```

---

## 🧩 Module Breakdown

### 1. `producer` (Java/Python)

**Purpose:** Simulates or bridges real user and product events into Kafka.

**What it does:**
- Connects to the Kafka broker at `localhost:9092` (or the Docker network hostname)
- Serializes event payloads (product views, clicks, new listings) as JSON records
- Publishes them to a configured Kafka topic (e.g., `product-events`)
- The Python script `generate_sample_data.py` is a utility to seed the pipeline with realistic mock data for local testing

**Why it matters:** The producer is the entry point of the entire pipeline. It decouples event sources from processing logic — the downstream Spark job doesn't care who produced the event or when.

---

### 2. `spark-pipelines` (Scala)

**Purpose:** The heart of the real-time data processing layer.

**What it does:**
- Uses **Apache Spark Structured Streaming** to subscribe to Kafka topics
- Parses raw JSON events into typed Spark DataFrames
- Applies transformations: field normalization, null handling, feature extraction
- Optionally computes ML-derived features (e.g., TF-IDF vectors, item embeddings) that feed content-based recommendations
- Writes processed records into Elasticsearch using the Elasticsearch-Hadoop (ES-Spark) connector

**Why it matters:** Spark enables the pipeline to handle very high event throughput with exactly-once semantics. Instead of a simple pass-through consumer, the Spark layer makes the data **richer and more queryable** before it ever reaches Elasticsearch. This is where recommendation logic (feature engineering, scoring signals) lives.

**Key design choices:**
- Uses **Structured Streaming** (not legacy DStreams) for better SQL-style APIs and end-to-end fault tolerance
- Writes in micro-batches to Elasticsearch to balance latency and throughput
- Scala is chosen for its native compatibility with Spark's API and strong typing

---

### 3. `search-service` (Java · Spring Boot)

**Purpose:** The public-facing API that clients use to search products and fetch recommendations.

**What it does:**
- Exposes two REST endpoints (see [API Reference](#-api-reference) below)
- Queries Elasticsearch using the Java High-Level REST Client
- `GET /search/product` — performs full-text + filtered search against the product index
- `GET /recommend/product` — uses content-based similarity (ES `more_like_this` or `knn` query) to return related products given a `productId`
- Packaged as a fat JAR via Maven and run on **Eclipse Temurin JDK 17** inside Docker

**Why it matters:** By keeping the API layer separate from the processing layer, the service can be scaled independently. You can run many replicas of `search-service` behind a load balancer without scaling Spark or Kafka.

**Dockerfile (root-level):**
```dockerfile
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY search-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### 4. `dockerConfiguration`

**Purpose:** Defines the complete local environment using Docker Compose.

**Services it spins up:**
| Service | Image | Port |
|---|---|---|
| ZooKeeper | confluentinc/cp-zookeeper | 2181 |
| Kafka Broker | confluentinc/cp-kafka | 9092 |
| Elasticsearch | docker.elastic.co/elasticsearch | 9200 |
| search-service | Built from root Dockerfile | 8080 |
| Producer | Built from producer/ | — |
| Spark Pipelines | Built from spark-pipelines/ | — |

---

## 📡 API Reference

Base URL: `http://localhost:8080`

---

### `GET /search/product`

Search for products by keyword with optional filters.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `query` | string | ✅ Yes | Search term (e.g. `laptop`, `running shoes`) |
| `price` | number | ❌ No | Maximum price filter |
| `category` | string | ❌ No | Category filter (e.g. `electronics`, `apparel`) |

**Example:**
```bash
curl "http://localhost:8080/search/product?query=laptop&price=1500&category=electronics"
```

**Response (200 OK):**
```json
{
  "results": [
    {
      "productId": "P1234",
      "name": "Dell XPS 15 Laptop",
      "category": "electronics",
      "price": 1299.99,
      "score": 0.98
    }
  ],
  "total": 1
}
```

---

### `GET /recommend/product`

Get content-based product recommendations for a given product.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `productId` | string | ✅ Yes | The product ID to base recommendations on |

**Example:**
```bash
curl "http://localhost:8080/recommend/product?productId=P1234"
```

**Response (200 OK):**
```json
{
  "productId": "P1234",
  "recommendations": [
    { "productId": "P5678", "name": "MacBook Pro 14", "score": 0.91 },
    { "productId": "P9012", "name": "HP Spectre x360",  "score": 0.87 }
  ]
}
```

---

## 🐳 Local Setup with Docker

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- At least **8 GB RAM** allocated to Docker (Settings → Resources)
- Ports `9092`, `9200`, `8080`, and `2181` free on your machine

---

### Step 1 — Clone the repository

```bash
git clone https://github.com/Rakeshwar/Kafka-Elastic-Search-Recommendation-Engine.git
cd Kafka-Elastic-Search-Recommendation-Engine
```

---

### Step 2 — Build the search-service JAR

```bash
cd search-service
mvn clean package -DskipTests
cd ..
```

> This creates `search-service/target/*.jar`, which the root `Dockerfile` expects.

---

### Step 3 — Start all services

```bash
cd dockerConfiguration
docker compose up --build
```

This will:
- Pull all required Docker images
- Start ZooKeeper → Kafka → Elasticsearch in dependency order
- Build and start the search-service and producer containers
- Start the Spark pipeline job

Allow **60–90 seconds** for Elasticsearch to reach a healthy state before sending requests.

---

### Step 4 — Verify services are running

```bash
# Check Kafka broker is up
docker compose ps

# Check Elasticsearch health
curl http://localhost:9200/_cluster/health?pretty

# Check search-service is responding
curl http://localhost:8080/search/product?query=test
```

---

### Step 5 — Seed with sample data (optional)

```bash
# From the project root
python producer/generate_sample_data.py
```

This sends a batch of mock product and event records to the Kafka topic, triggering the Spark pipeline to index them in Elasticsearch.

---

### Step 6 — Test the API

```bash
# Search
curl "http://localhost:8080/search/product?query=laptop"

# Recommendations
curl "http://localhost:8080/recommend/product?productId=12345"
```

---

### Stopping the stack

```bash
docker compose down           # Stop services, keep volumes
docker compose down -v        # Stop services AND remove volumes (fresh start)
```

---

## ⚙️ Environment Variables

Key variables you can override in `dockerConfiguration/docker-compose.yml` or via `.env`:

| Variable | Default | Description |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | Kafka broker address |
| `ELASTICSEARCH_HOST` | `elasticsearch` | ES hostname inside Docker network |
| `ELASTICSEARCH_PORT` | `9200` | ES HTTP port |
| `KAFKA_TOPIC_PRODUCTS` | `product-events` | Kafka topic name for product events |
| `ES_INDEX_PRODUCTS` | `products` | Elasticsearch index name |
| `SPRING_PROFILES_ACTIVE` | `docker` | Spring Boot active profile |

---

## 🚀 Extending the System

This project is designed as a **reference architecture**. Here are the most impactful ways to extend it:

**Recommendation algorithms**
- Add collaborative filtering (ALS via Spark MLlib) in the Spark pipeline
- Replace `more_like_this` with dense vector k-NN search using Elasticsearch's `knn` query and product embeddings

**Observability**
- Add Kibana (`docker.elastic.co/kibana/kibana`) for index visualization and dashboards
- Add Prometheus + Grafana for Kafka and JVM metrics

**Security**
- Enable Elasticsearch security with TLS and API keys
- Add Spring Security to the search-service for JWT-based auth

**Scaling**
- Increase Kafka partition count for parallelism
- Add more Spark executors via `spark.executor.instances` in the Spark config
- Deploy to Kubernetes using Helm charts for Kafka (Strimzi) and Elasticsearch (ECK)

**CI/CD**
- Extend `.github/workflows/` to add automated tests, Docker image builds, and pushes to a registry on every PR

---

## 🗺 Roadmap

- [ ] Add collaborative filtering model via Spark MLlib
- [ ] Integrate Kibana dashboard for real-time pipeline monitoring
- [ ] Add user-event tracking topic (clicks, purchases) to Kafka
- [ ] Implement JWT authentication on search-service
- [ ] Kubernetes deployment manifests (Helm charts)
- [ ] Benchmark suite for throughput and latency profiling

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'feat: add your feature'`
4. Push to your branch: `git push origin feature/your-feature-name`
5. Open a Pull Request describing the change

Please ensure your code is well-tested and follows the existing module structure.

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for details.

---

## 👤 Author

**Rakeshwar**
GitHub: [@Rakeshwar](https://github.com/Rakeshwar)

---

> Built with ❤️ using Apache Kafka · Apache Spark · Elasticsearch · Spring Boot
