import json
import time
import random
from kafka import KafkaProducer

producer = KafkaProducer(
    bootstrap_servers="localhost:9092",
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

products = [
    {"id": "p1", "name": "iPhone 15 Pro", "category": "mobile"},
    {"id": "p2", "name": "Samsung Galaxy S23", "category": "mobile"},
    {"id": "p3", "name": "MacBook Air M2", "category": "laptop"}
]

def generate_event(product):
    event_type = random.choice(["CREATE", "UPDATE"])

    return {
        "id": product["id"],
        "name": product["name"],
        "category": product["category"],
        "price": random.randint(800, 2000),
        "rating": round(random.uniform(3.5, 5.0), 1),
        "description": product["name"],
        "timestamp": int(time.time()),
        "eventType": event_type
    }

print("Starting Product Stream...")

while True:
    product = random.choice(products)
    event = generate_event(product)

    producer.send("product-events", event)
    print("Sent:", event)

    time.sleep(2)