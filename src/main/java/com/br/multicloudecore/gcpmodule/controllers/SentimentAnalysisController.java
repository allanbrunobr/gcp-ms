package com.br.multicloudecore.gcpmodule.controllers;

import com.br.multicloudecore.gcpmodule.exceptions.SentimentAnalysisException;
import com.br.multicloudecore.gcpmodule.models.sentiment.SentimentDescription;
import com.br.multicloudecore.gcpmodule.service.ai.AnalyzeSentimentService;
import com.google.cloud.language.v2.Sentiment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class SentimentAnalysisController {

  private final AnalyzeSentimentService analyzeSentimentService;

  public SentimentAnalysisController(AnalyzeSentimentService analyzeSentimentService) {
    this.analyzeSentimentService = analyzeSentimentService;
  }

  @PostMapping("/sentimentAnalysis")
  public ResponseEntity<?> sentimentAnalysis(@RequestBody TextRequest request) {
    String textToAnalyze = request.getTextToAnalyze();
    if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("Text cannot be empty.");
    }

    try {
      CompletableFuture<Sentiment> sentimentFuture = analyzeSentimentService.analyzeSentiment(textToAnalyze);
      Sentiment sentiment = sentimentFuture.get();

      SentimentDescription sentimentDescription = new SentimentDescription();
      String description = sentimentDescription.getSentimentDescription(sentiment.getScore(), sentiment.getMagnitude());

      return ResponseEntity.ok(description);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ResponseEntity.status(500).body("Thread interrupted while analyzing sentiment.");
    } catch (SentimentAnalysisException e) {
      return ResponseEntity.status(500).body("Sentiment analysis failed.");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred.");
    }
  }

  @Setter
  @Getter
  public static class TextRequest {
    private String textToAnalyze;

  }
}
