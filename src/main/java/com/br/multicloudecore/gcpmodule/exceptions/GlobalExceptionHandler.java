package com.br.multicloudecore.gcpmodule.exceptions;

import com.br.multicloudecore.gcpmodule.utils.Constants;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * GlobalExceptionHandler is a class that handles and manages exceptions
 * occurred throughout the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles an exception of type SentimentAnalysisException by creating and
   * returning a ModelAndView object with an error view.
   * The error message from the exception is added as a model attribute.
   *
   * @param ex the SentimentAnalysisException to be handled
   * @return a ModelAndView object with the error view and the error message as a model attribute
   */
  @ExceptionHandler(SentimentAnalysisException.class)
  public ModelAndView handleSentimentAnalysisException(SentimentAnalysisException ex) {
    ModelAndView modelAndView = new ModelAndView(Constants.ERROR_VIEW_NAME);
    modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }

  /**
   * Handles a PlacesSearchException by creating and returning a ModelAndView
   * object with an error view.
   * The error message from the exception is added as a model attribute.
   *
   * @param ex The PlacesSearchException to be handled.
   * @return A ModelAndView object with the error view and the error message as a model attribute.
   */
  @ExceptionHandler(PlacesSearchException.class)
  public ModelAndView handlePlacesSearchException(PlacesSearchException ex) {
    ModelAndView modelAndView = new ModelAndView(Constants.ERROR_VIEW_NAME);
    modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }

  /**
   * Handles a ResultVisionException by creating and returning a ModelAndView object
   * with an error view.
   * The error message from the exception is added as a model attribute.
   *
   * @param ex the ResultVisionException to be handled
   * @return a ModelAndView object with the error view and the error message as a model attribute
   */
  @ExceptionHandler(ResultVisionException.class)
  public ModelAndView handleResultVisionException(ResultVisionException ex) {
    ModelAndView modelAndView = new ModelAndView(Constants.ERROR_VIEW_NAME);
    modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }

  /**
   * Handles a TranslationException by creating and returning a ModelAndView object
   * with an error view.
   * The error message from the exception is added as a model attribute.
   *
   * @param ex the TranslationException to be handled
   * @return a ModelAndView object with the error view and the error message as a model attribute
   */
  @ExceptionHandler(TranslationException.class)
  public ModelAndView handleTranslationException(TranslationException ex) {
    ModelAndView modelAndView = new ModelAndView(Constants.ERROR_VIEW_NAME);
    modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }

  /**
   * Handles an UploadFileToStorageException by creating and returning a ModelAndView
   * object with an error view.
   * The exception message is added as an attribute to the ModelAndView object.
   *
   * @param ex The UploadFileToStorageException to be handled.
   * @return A ModelAndView object with the error view and the error message as a model attribute.
   */
  @ExceptionHandler(UploadFileToStorageException.class)
  public ModelAndView handleUploadFileToStorageException(UploadFileToStorageException ex) {
    ModelAndView modelAndView = new ModelAndView(Constants.ERROR_VIEW_NAME);
    modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }

  /**
   * Handles an exception by creating a ModelAndView object with an error view,
   * setting the error message as a model attribute and returning the ModelAndView object.
   *
   * @param ex the exception to be handled
   * @return a ModelAndView object with the error view and the error message as a model attribute
   */
  @ExceptionHandler(Exception.class)
  public ModelAndView handleException(Exception ex) {
    ModelAndView modelAndView = new ModelAndView(Constants.ERROR_VIEW_NAME);
    modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
    return modelAndView;
  }
}
