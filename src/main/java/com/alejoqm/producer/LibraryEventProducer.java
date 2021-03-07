package com.alejoqm.producer;

import com.alejoqm.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class LibraryEventProducer {

  private KafkaTemplate<Integer, String> kafkaTemplate;
  private ObjectMapper objectMapper;

  public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void sentLibraryEventSync(LibraryEvent libraryEvent)
      throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
    Integer key = libraryEvent.getId();
    String value = objectMapper.writeValueAsString(libraryEvent);
    try {
      kafkaTemplate.sendDefault(key, value).get(3, TimeUnit.SECONDS);
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      log.error("Error sending the message {} {} {}", key, value, e.getMessage());
      throw e;
    }
  }

  public ListenableFuture sendLibraryEventAsync(LibraryEvent libraryEvent) {
    ListenableFuture<SendResult<Integer, String>> listenableFuture = null;
    try {
      ProducerRecord<Integer, String> producerRecord = buildProducerRecord(libraryEvent.getId(),
          objectMapper.writeValueAsString(libraryEvent), "library-events");

      listenableFuture = kafkaTemplate.send(producerRecord);
      listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
        @Override
        public void onFailure(Throwable throwable) {
          handleFailure(libraryEvent, throwable);
        }

        @Override
        public void onSuccess(SendResult<Integer, String> result) {
          handleSuccess(libraryEvent, result);
        }
      });
    } catch (JsonProcessingException e) {
      log.error("Error sending message {}", e.getMessage());
    }
    return listenableFuture;
  }

  private void handleSuccess(LibraryEvent libraryEvent, SendResult<Integer, String> result) {
    log.info("Message sent for key {} and value {}, the partition is {}", libraryEvent.getId(),
        libraryEvent.getBook().toString(), result.getRecordMetadata().partition());
  }

  private void handleFailure(LibraryEvent libraryEvent, Throwable ex) {
    log.error("Error sending message {} {} throwing exception {}", libraryEvent.getId(),
        libraryEvent.getBook().toString(), ex.getMessage());
  }

  private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
    List<Header> recordHeaders = Arrays
        .asList(new RecordHeader("event-source", "scanner".getBytes()));

    return new ProducerRecord<>(topic, null, key, value, recordHeaders);
  }

}
