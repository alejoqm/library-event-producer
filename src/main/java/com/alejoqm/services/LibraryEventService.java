package com.alejoqm.services;

import com.alejoqm.domain.LibraryEvent;
import com.alejoqm.exception.LibraryEventException;

public interface LibraryEventService {
  void sent(LibraryEvent libraryEvent) throws LibraryEventException;
}
