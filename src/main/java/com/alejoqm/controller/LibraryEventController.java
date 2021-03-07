package com.alejoqm.controller;

import com.alejoqm.domain.LibraryEvent;
import com.alejoqm.domain.LibraryEventType;
import com.alejoqm.exception.LibraryEventException;
import com.alejoqm.services.LibraryEventService;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryEventController {

  private LibraryEventService libraryEventService;

  public LibraryEventController(LibraryEventService libraryEventService) {
    this.libraryEventService = libraryEventService;
  }

  @PostMapping("/v1/libraryEvent")
  public ResponseEntity<LibraryEvent> create(@RequestBody LibraryEvent libraryEvent)
      throws LibraryEventException {
    libraryEvent.setLibraryEventType(LibraryEventType.CREATE);
    libraryEventService.sent(libraryEvent);
    return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
  }

  @PutMapping("/v1/libraryEvent")
  public ResponseEntity<LibraryEvent> update(@RequestBody LibraryEvent libraryEvent)
      throws LibraryEventException {

    if(libraryEvent.getId() == null) {
      throw new LibraryEventException(HttpStatus.BAD_REQUEST, "Id is required.");
    }

    libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
    libraryEventService.sent(libraryEvent);
    return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
  }
}
