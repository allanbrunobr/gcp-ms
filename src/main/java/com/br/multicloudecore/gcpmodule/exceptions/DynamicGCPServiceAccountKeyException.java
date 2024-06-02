package com.br.multicloudecore.gcpmodule.exceptions;

/**
 * Exception class for errors that occur during places search.
 */
public class DynamicGCPServiceAccountKeyException extends RuntimeException {

  /**
   * Exception class for errors that occur during places search.
   * This exception is used to represent any issues encountered during
   * the places search process.
   *
   * @param message A description of the error.
   */
  public DynamicGCPServiceAccountKeyException(String message) {
    super(message);
  }

  /**
   * Constructs a new places search exception with the specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()).
   * @param cause   The cause (which is saved for later retrieval by the getCause() method).
   *            (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public DynamicGCPServiceAccountKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
