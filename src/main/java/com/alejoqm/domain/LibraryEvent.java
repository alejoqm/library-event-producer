package com.alejoqm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class LibraryEvent {

  private Integer id;
  private LibraryEventType libraryEventType;
  private Book book;
}
