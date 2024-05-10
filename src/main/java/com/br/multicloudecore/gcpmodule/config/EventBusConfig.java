package com.br.multicloudecore.gcpmodule.config;

import com.br.multicloudecore.gcpmodule.events.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do EventBus.
 */
@Configuration
public class EventBusConfig {

  /**
   * Creates a new instance of EventBus.
   *
   * @return a new instance of EventBus
   */
  @Bean
    public EventBus eventBus() {
    return new EventBus();
  }
}