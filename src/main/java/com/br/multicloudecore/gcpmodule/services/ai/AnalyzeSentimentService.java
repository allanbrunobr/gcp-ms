package com.br.multicloudecore.gcpmodule.services.ai;

import com.br.multicloudecore.gcpmodule.exceptions.SentimentAnalysisException;
import com.google.cloud.language.v2.Document;
import com.google.cloud.language.v2.LanguageServiceClient;
import com.google.cloud.language.v2.LanguageServiceSettings;
import com.google.cloud.language.v2.Sentiment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Service to analyze sentiment of a text.
 */
@Service
public class AnalyzeSentimentService extends AIServiceBase {

  /***
   * Analyze the sentiment of a text.
   *
   * @param text The text to analyze.
   * @return The sentiment of the text.
   */
  @Async
  public CompletableFuture<Sentiment> analyzeSentiment(String text) {
    try {
      LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
          .setCredentialsProvider(getCredentialsProvider())
          .build();

      try (LanguageServiceClient language = LanguageServiceClient.create(settings)) {
        Document doc = Document.newBuilder().setContent(text)
            .setType(Document.Type.PLAIN_TEXT).build();
        Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
        if (sentiment == null) {
          throw new SentimentAnalysisException("Sentiment analysis failed.");
        }
        return CompletableFuture.completedFuture(sentiment);
      }
    } catch (IOException e) {
      throw new SentimentAnalysisException("Error loading credentials", e);
    }
  }
}
