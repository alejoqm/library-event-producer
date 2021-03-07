package com.alejoqm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alejoqm.domain.Book;
import com.alejoqm.domain.LibraryEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                  "spring.kafka.admin.properties.bootstrap.servers = ${spring.embedded.kafka.brokers}"})
public class LibraryEventControllerTest {

  private static String JSON_BOOK = "{\"id\":null,\"libraryEventType\":\"CREATE\",\"book\":{\"name\":\"Name\",\"author\":\"Author\"}}";
  private static String JSON_BOOK_UPDATE = "{\"id\":1,\"libraryEventType\":\"UPDATE\",\"book\":{\"name\":\"Name\",\"author\":\"Author\"}}";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  private Consumer<Integer, String> consumer;

  @BeforeEach
  public void setup() {
    Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("Group1", "true", embeddedKafkaBroker));

    consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
    embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
  }

  @AfterEach
  public void tearDown() {
    consumer.close();
  }

  @Test
  public void postLibraryEvent() {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(null)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("content-type", MediaType.APPLICATION_JSON_VALUE);

    HttpEntity<LibraryEvent> entity = new HttpEntity<>(libraryEvent, httpHeaders);

    ResponseEntity<LibraryEvent> response = testRestTemplate
        .exchange("/v1/libraryEvent", HttpMethod.POST, entity, LibraryEvent.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
    assertEquals(JSON_BOOK, consumerRecord.value());
  }

  @Test
  public void putLibraryEvent() {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(1)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("content-type", MediaType.APPLICATION_JSON_VALUE);

    HttpEntity<LibraryEvent> entity = new HttpEntity<>(libraryEvent, httpHeaders);

    ResponseEntity<LibraryEvent> response = testRestTemplate
        .exchange("/v1/libraryEvent", HttpMethod.PUT, entity, LibraryEvent.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
    assertEquals(JSON_BOOK_UPDATE, consumerRecord.value());
  }

}
