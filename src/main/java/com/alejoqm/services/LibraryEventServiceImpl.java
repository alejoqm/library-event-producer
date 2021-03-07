package com.alejoqm.services;

import com.alejoqm.domain.LibraryEvent;
import com.alejoqm.exception.LibraryEventException;
import com.alejoqm.producer.LibraryEventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LibraryEventServiceImpl implements LibraryEventService{

  private LibraryEventProducer libraryEventProducer;

  public LibraryEventServiceImpl(LibraryEventProducer libraryEventProducer) {
    this.libraryEventProducer = libraryEventProducer;
  }

  @Override
  public void sent(LibraryEvent libraryEvent) throws LibraryEventException {
    try {
      this.libraryEventProducer.sendLibraryEventAsync(libraryEvent);
    } catch (Exception e) {
      throw new LibraryEventException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
