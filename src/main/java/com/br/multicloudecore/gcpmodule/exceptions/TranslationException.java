package com.br.multicloudecore.gcpmodule.exceptions;

/**
 * Exception class for errors that occur during translation.
 */
public class TranslationException extends RuntimeException {

  /**
   * Exception class for errors that occur during translation.
   * This exception is used to represent any issues encountered during
   * the translation process.
   *
   * @param message A description of the error.
   */
  public TranslationException(String message) {
    super(message);
  }

  /**
   * Constructs a new translation exception with the specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()).
   * @param cause   The cause (which is saved for later retrieval by the getCause() method).
   *            (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }
}
