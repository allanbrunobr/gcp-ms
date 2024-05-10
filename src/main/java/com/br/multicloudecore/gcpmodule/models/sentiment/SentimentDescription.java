package com.br.multicloudecore.gcpmodule.models.sentiment;

import lombok.NoArgsConstructor;

/**
 * Represents a class that provides sentiment description based on a given score and magnitude.
 */
@NoArgsConstructor
public class SentimentDescription {

  /**
   * Returns the description of a sentiment based on the given score and magnitude.
   *
   * @param score     The sentiment score.
   * @param magnitude The sentiment magnitude.
   * @return The description of the sentiment.
   */
  public String getSentimentDescription(double score, double magnitude) {
    String scoreDescription = getScoreDescription(score);
    String magnitudeDescription = getMagnitudeDescription(magnitude);

    return "O sentimento expresso é " + scoreDescription + " com uma intensidade " + magnitudeDescription + ".";
  }

  /**
   * Returns the description of a sentiment score.
   *
   * @param score The sentiment score.
   * @return The description of the sentiment score.
   */
  private String getScoreDescription(double score) {
    if (score >= -1 && score < -0.5) {
      return "muito negativo";
    } else if (score >= -0.5 && score < 0) {
      return "negativo";
    } else if (score == 0) {
      return "neutro";
    } else if (score > 0 && score <= 0.5) {
      return "positivo";
    } else {
      return "muito positivo";
    }
  }

  /**
   * Returns the description of a sentiment magnitude.
   *
   * @param magnitude The sentiment magnitude.
   * @return The description of the sentiment magnitude.
   */
  private String getMagnitudeDescription(double magnitude) {
    if (magnitude >= 0 && magnitude < 0.5) {
      return "fraca";
    } else if (magnitude >= 0.5 && magnitude < 1) {
      return "moderada";
    } else if (magnitude >= 1 && magnitude < 2) {
      return "forte";
    } else if (magnitude >= 2 && magnitude < 3) {
      return "muito forte";
    } else {
      return "extremamente forte";
    }
  }
}
