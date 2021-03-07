package com.alejoqm.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {

  private static final String TOPIC_NAME = "library-events";

  @Bean
  public NewTopic libraryEvents() {
    return TopicBuilder.name(TOPIC_NAME).partitions(3).replicas(1).build();
  }

}
