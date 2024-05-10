package com.br.multicloudecore.gcpmodule.exceptions;

/**
 * Exception class for errors that occur during sentiment analysis.
 */
public class SentimentAnalysisException extends RuntimeException {

  /**
   * Exception class for errors that occur during sentiment analysis.
   * This exception is used to represent any issues encountered during
   * the sentiment analysis process.
   *
   * @param message A description of the error.
   */
  public SentimentAnalysisException(String message) {
    super(message);
  }

  /**
   * Constructs a new sentiment analysis exception with the specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()).
   * @param cause   The cause (which is saved for later retrieval by the getCause() method).
   *            (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public SentimentAnalysisException(String message, Throwable cause) {
    super(message, cause);
  }
}
