package com.br.multicloudecore.gcpmodule.controllers;

import com.br.multicloudecore.gcpmodule.config.Constants;
import com.br.multicloudecore.gcpmodule.exceptions.SentimentAnalysisException;
import com.br.multicloudecore.gcpmodule.models.sentiment.SentimentDescription;
import com.br.multicloudecore.gcpmodule.service.ai.AnalyzeSentimentService;
import com.google.cloud.language.v2.Sentiment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.CompletableFuture;



/**
 * Controlador responsável por lidar com as requisições de análise de sentimento.
 * O SentimentAnalysisController gerencia a análise de sentimentos de textos fornecidos
 * pelos usuários.
 */
@RestController
public class SentimentAnalysisController {

  private final AnalyzeSentimentService analyzeSentimentService;

  /**
   * Constrói um novo controlador de análise de sentimentos.
   *
   * @param analyzeSentimentService O serviço de análise de sentimentos a ser
   *                                utilizado pelo controlador.
   */
  public SentimentAnalysisController(AnalyzeSentimentService analyzeSentimentService) {
    this.analyzeSentimentService = analyzeSentimentService;
  }

  /**
   * Analyzes the sentiment of a given text.
   *
   * @param dados The text to analyze.
   * @return The ModelAndView object that contains the sentiment analysis result.
   */
  @PostMapping("/sentimentAnalysis")
  public ModelAndView sentimentAnalysis(@RequestParam("textToAnalyze") String dados) {
    ModelAndView resultView = new ModelAndView(
                "ai/sentiment-analysis/sentiment-analysis-result");
    try {
      CompletableFuture<Sentiment> sentimentFuture = analyzeSentimentService
              .analyzeSentiment(dados);
      Sentiment sentiment = sentimentFuture.get();

      SentimentDescription sentimentDescription = new SentimentDescription();
      String description = sentimentDescription
              .getSentimentDescription(sentiment.getScore(), sentiment.getMagnitude());

      resultView.addObject("sentiment", sentiment);
      resultView.addObject("description", description);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      resultView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE,
              "Thread interrupted while analyzing sentiment.");
      resultView.setViewName(Constants.ERROR_VIEW_NAME);
    } catch (SentimentAnalysisException e) {
      resultView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE,
              "Sentiment analysis failed.");
      resultView.setViewName(Constants.ERROR_VIEW_NAME);
    } catch (Exception e) {
      resultView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE,
              "An unexpected error occurred.");
      resultView.setViewName(Constants.ERROR_VIEW_NAME);
    }
    return resultView;
  }
}

