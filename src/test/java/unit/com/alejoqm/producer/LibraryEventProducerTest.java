package com.alejoqm.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alejoqm.domain.Book;
import com.alejoqm.domain.LibraryEvent;
import com.alejoqm.exception.LibraryEventException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerTest {

  public static final String LIBRARY_EVENTS = "library-events";
  public static final int INT = 1;
  @Spy
  private ObjectMapper objectMapper;

  @InjectMocks
  private LibraryEventProducer libraryEventProducer;

  @Mock
  private KafkaTemplate<Integer, String> kafkaTemplate;

  @Test
  public void testLibraryEventWithListenerFuture_failure() {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(null)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

    SettableListenableFuture settableListenableFuture = new SettableListenableFuture();
    settableListenableFuture.setException(new RuntimeException("Exception Calling kafka"));

    when(kafkaTemplate.sendDefault(isA(Integer.class), isA(String.class)))
        .thenReturn(settableListenableFuture);

    assertThrows(Exception.class, () -> libraryEventProducer.sendLibraryEventAsync(libraryEvent));
  }

  @Test
  public void testPostLibraryEvent_success() throws Exception, LibraryEventException {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(INT)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

    SettableListenableFuture future = new SettableListenableFuture();

    ProducerRecord<Integer, String> producerRecord = new ProducerRecord(LIBRARY_EVENTS,
        libraryEvent.getId(),
        objectMapper.writeValueAsBytes(libraryEvent));

    RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", INT),
        INT,
        INT, INT, 1l, INT, INT);

    SendResult<Integer, String> result = new SendResult<>(producerRecord, recordMetadata);

    future.set(result);


    when(kafkaTemplate.sendDefault(isA(Integer.class), isA(String.class)))
        .thenReturn(future);

    ListenableFuture<SendResult<Integer, String>> result1 = libraryEventProducer.sendLibraryEventAsync(libraryEvent);
    assertEquals(INT, result1.get().getRecordMetadata().partition());
  }
}
