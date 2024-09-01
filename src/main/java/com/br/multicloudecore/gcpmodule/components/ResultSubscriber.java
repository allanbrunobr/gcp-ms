package com.br.multicloudecore.gcpmodule.components;

import com.br.multicloudecore.gcpmodule.events.EventBus;
import com.br.multicloudecore.gcpmodule.models.vision.facerecognition.FaceDetectionMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
  private static final Logger logger = LoggerFactory.getLogger(ResultSubscriber.class);

  private final EventBus eventBus;
  private final ObjectMapper objectMapper;

  @Value("${project.id}")
  private String projectId;

  @Value("${gcp.pubsub.filevisioned.subscription}")
  private String subscriptionId;

  public ResultSubscriber(EventBus eventBus, ObjectMapper objectMapper) {
    this.eventBus = eventBus;
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void init() {
    subscribeToTopic();
  }

  private void subscribeToTopic() {
    ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

    MessageReceiver receiver = (message, consumer) -> {
      String payload = message.getData().toStringUtf8();
      logger.info("Received message: ID={}", message.getMessageId());

      try {
        FaceDetectionMessage faceDetectionMessage = objectMapper.readValue(payload, FaceDetectionMessage.class);
        eventBus.publish(faceDetectionMessage);
        consumer.ack();
      } catch (IOException e) {
        logger.error("Error processing message: {}", e.getMessage());
        logger.debug("Problematic payload: {}", payload);
        consumer.nack();
      }
    };

    Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
    subscriber.startAsync().awaitRunning();

    logger.info("Listening for messages on {}", subscriptionName);
  }
}
