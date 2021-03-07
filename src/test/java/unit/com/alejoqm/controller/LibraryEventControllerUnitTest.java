package com.alejoqm.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alejoqm.domain.Book;
import com.alejoqm.domain.LibraryEvent;
import com.alejoqm.exception.LibraryEventException;
import com.alejoqm.services.LibraryEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

  private static String URL = "/v1/libraryEvent";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LibraryEventService libraryEventService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testPostLibraryEvent() throws Exception, LibraryEventException {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(null)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

        mockMvc.perform(post(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(libraryEvent)))
        .andExpect(status().isCreated());
  }

  @Test
  public void tesPutLibraryEvent() throws Exception {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(1)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

    mockMvc.perform(put(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(libraryEvent)))
        .andExpect(status().isOk());
  }

  @Test
  public void updateLibraryEvent_withNullLibraryEventId() throws Exception {
    LibraryEvent libraryEvent = LibraryEvent.builder()
        .id(null)
        .book(Book.builder().name("Name").author("Author").build())
        .build();

    mockMvc.perform(put(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(libraryEvent)))
        .andExpect(status().isBadRequest());
  }

}
