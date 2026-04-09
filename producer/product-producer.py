import json
import time
import random
from kafka import KafkaProducer

# Kafka Producer
producer = KafkaProducer(
    bootstrap_servers="localhost:9092",
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

# Expanded Categories
CATEGORIES = {
    "mobile": ["iPhone", "Samsung Galaxy", "OnePlus", "Pixel", "Realme"],
    "laptop": ["MacBook", "Dell XPS", "HP Pavilion", "Lenovo ThinkPad", "Asus ZenBook"],
    "electronics": ["Sony Headphones", "JBL Speaker", "Smart TV", "Camera", "Bluetooth Earbuds"],
    "fashion": ["T-Shirt", "Jeans", "Sneakers", "Jacket", "Watch"],
    "home": ["Sofa", "Dining Table", "Bed", "Chair", "Cupboard"],
    "books": ["Fiction Book", "Science Book", "History Book", "Programming Book"],
    "sports": ["Football", "Cricket Bat", "Tennis Racket", "Running Shoes"]
}

# Generate large product catalog
def generate_products(n=2000):
    products = []

    for i in range(1, n + 1):
        category = random.choice(list(CATEGORIES.keys()))
        base_name = random.choice(CATEGORIES[category])

        product = {
            "id": f"p{i}",
            "name": f"{base_name} {random.randint(1, 100)}",
            "category": category
        }

        products.append(product)

    return products


products = generate_products(1500)

# Event Types with realistic distribution
EVENT_TYPES = ["CREATE", "UPDATE", "DELETE"]

# Price logic based on category
def generate_price(category):
    if category == "mobile":
        return random.randint(500, 1500)
    elif category == "laptop":
        return random.randint(800, 3000)
    elif category == "electronics":
        return random.randint(100, 2000)
    elif category == "fashion":
        return random.randint(20, 300)
    elif category == "home":
        return random.randint(100, 2000)
    elif category == "books":
        return random.randint(5, 100)
    elif category == "sports":
        return random.randint(20, 500)
    return random.randint(50, 500)


# Generate event
def generate_event(product):
    event_type = random.choices(
        EVENT_TYPES,
        weights=[0.2, 0.7, 0.1]  # CREATE less, UPDATE more, DELETE rare
    )[0]

    event = {
        "id": product["id"],
        "name": product["name"],
        "category": product["category"],
        "timestamp": int(time.time()),
        "eventType": event_type
    }

    # Only include full data for CREATE/UPDATE
    if event_type != "DELETE":
        event.update({
            "price": generate_price(product["category"]),
            "rating": round(random.uniform(2.5, 5.0), 1),
            "description": f"{product['name']} in {product['category']}"
        })

    return event


print("Starting Advanced Product Stream...")

while True:
    product = random.choice(products)
    event = generate_event(product)

    producer.send("product-events", event)

    print(f"{event['eventType']} → {event['id']} | {event['category']} | {event.get('price', 'NA')}")

    time.sleep(random.uniform(0.3, 1.5))