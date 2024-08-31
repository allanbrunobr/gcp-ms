package com.br.multicloudecore.gcpmodule.components;

import com.br.multicloudecore.gcpmodule.events.EventBus;
import com.br.multicloudecore.gcpmodule.exceptions.ResultVisionException;
import com.br.multicloudecore.gcpmodule.models.vision.facerecognition.FaceDetectionMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ResultSubscriber class is responsible for subscribing to different Google Pub/Sub
 * subscriptions and processing the messages received. It utilizes the EventBus class to publish
 * events based on the subscription ID.
 *
 * <p>The class requires an instance of the EventBus class to be passed in the constructor.
 * The subscribe() method is used to subscribe to a specific subscription and
 * process the received messages.
 *
 * <p>Sample usage:
 * <pre>{@code
 * EventBus eventBus = new EventBus();
 * ResultSubscriber subscriber = new ResultSubscriber(eventBus);
 * subscriber.init();
 * }</pre>
 */
@Component
public class ResultSubscriber {

  private EventBus eventBus;

  /**
   * Constrói um novo assinante de resultados.
   *
   * @param eventBus O barramento de eventos para se inscrever.
   * @param simpMessagingTemplate  O template de mensagens simp para enviar mensagens
   *                               para o WebSocket.
   */
  public ResultSubscriber(EventBus eventBus, SimpMessagingTemplate simpMessagingTemplate) {
    this.eventBus = eventBus;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  /**
   * Initializes the ResultSubscriber by subscribing to a specific Google Pub/Sub
   * subscription and processing the received messages. This method is annotated
   * with @PostConstruct, which means that it will be called automatically after
   * the ResultSubscriber bean has been constructed
   * and all the dependencies have been injected.
   *
   * <p>The method retrieves the project ID and subscription ID and
   * then calls the subscribe() method to perform the subscription
   * and message processing.
   *
   * @see ResultSubscriber#subscribe(String, String)
   */
  @PostConstruct
  public void init() {
    String projectId = "app-springboot-project";
    String subscriptionId = "filevisioned-topic-sub";
    subscribe(projectId, subscriptionId);
  }

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(ResultSubscriber.class);


  /**
   * Subscribes to a specific Google Pub/Sub subscription and processes the received messages.
   *
   * @param projectId       the ID of the Google Cloud project
   * @param subscriptionId  the ID of the Pub/Sub subscription
   */
  public void subscribe(String projectId, String subscriptionId) {
    try {
      CredentialsProvider credentialsProvider = FixedCredentialsProvider
              .create(GoogleCredentials.fromStream(new FileInputStream(
          "src/main/resources/keys/appubsub-admin-springboot-project-03da67dd523b.json")));


      ProjectSubscriptionName subscriptionName = ProjectSubscriptionName
                    .of(projectId, subscriptionId);

      if (subscribers.containsKey(subscriptionId)) {
        logger.warn("Já existe um assinante para a assinatura: {}", subscriptionId);
        return;
      }
      Subscriber subscriber = Subscriber
                    .newBuilder(subscriptionName, (MessageReceiver) (message, consumer) -> {
                      String jsonData = message.getData().toStringUtf8();
                      try {
                        FaceDetectionMessage faceDetectionMessage = objectMapper
                                .readValue(jsonData, FaceDetectionMessage.class);
                        eventBus.publish(faceDetectionMessage);
                        simpMessagingTemplate.convertAndSend("/topic/analysisResult",
                                faceDetectionMessage.toString());
                      } catch (JsonProcessingException e) {
                        throw new ResultVisionException("Error on processing message: "
                                + jsonData + " - " + e.getMessage(), e);
                      }
                      consumer.ack();
                    }).setCredentialsProvider(credentialsProvider).build();

      subscribers.putIfAbsent(subscriptionId, subscriber);
      subscriber.startAsync().awaitRunning();
    } catch (IOException e) {
      logger.error("Erro ao criar o assinante: {}", e.getMessage(), e);
    }
  }
}

