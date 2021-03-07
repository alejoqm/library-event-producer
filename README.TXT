# Library Event Producer
Library to sent books information to Kafka topic using a REST API.

## Kafka Server

```bash
./zookeeper-server-start.sh ../config/zookeeper.properties
```

```bash
Start up kafka: ./kafka-server-start.sh ../config/server.properties
```

## Usage
Run LibraryEventProducerApplication Spring boot project.

```bash
curl --location --request POST 'localhost:4420/v1/libraryEvent' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": 1,
    "book": {
        "name": "The Castle",
        "author": "Franz Kafka"
    }

}'
```

## Dependencies

Kafka 2-12-2.6.0
Java 8
Spring boot 2.4.1
Lombok

## Author

Alejoqm

