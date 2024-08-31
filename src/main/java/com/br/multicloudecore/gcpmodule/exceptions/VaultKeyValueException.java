package com.br.multicloudecore.gcpmodule.exceptions;

/**
 * Exception class for errors that occur during uploading file to storage.
 */
public class VaultKeyValueException extends RuntimeException {

  /**
   * Exception class for errors that occur during uploading file to storage.
   * This exception is used to represent any issues encountered during
   * uploading file to storage process.
   *
   * @param message A description of the error.
   */
  public VaultKeyValueException(String message) {
    super(message);
  }

  /**
   * Constructs a new uploading file to storage exception with the
   * specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()).
   * @param cause   The cause (which is saved for later retrieval by the getCause() method).
   *            (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public VaultKeyValueException(String message, Throwable cause) {
    super(message, cause);
  }
}
