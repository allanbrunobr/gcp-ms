package com.br.multicloudecore.gcpmodule.exceptions;

/**
 * Exception class for errors that occur during processing message from PubSub.
 */
public class ResultVisionException extends RuntimeException {

  /**
   * Exception class for errors that occur during processing message from PubSub.
   * This exception is used to represent any issues encountered during
   * the processing message from PubSub process.
   *
   * @param message A description of the error.
   */
  public ResultVisionException(String message) {
    super(message);
  }

  /**
   * Constructs a new processing message from PubSub exception with the
   * specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()).
   * @param cause   The cause (which is saved for later retrieval by the getCause() method).
   *            (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public ResultVisionException(String message, Throwable cause) {
    super(message, cause);
  }
}
