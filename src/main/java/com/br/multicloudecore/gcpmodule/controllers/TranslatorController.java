package com.br.multicloudecore.gcpmodule.controllers;

import com.br.multicloudecore.gcpmodule.utils.Constants;
import com.br.multicloudecore.gcpmodule.exceptions.TranslationException;
import com.br.multicloudecore.gcpmodule.service.ai.TranslatorService;
import com.google.cloud.translate.v3.Translation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class represents a controller for handling translation-related requests.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class TranslatorController {

  private final TranslatorService translatorService;

  /**
   * Constrói um novo controlador de tradução.
   *
   * @param translatorService O serviço de tradução a ser utilizado pelo controlador.
   */
  public TranslatorController(TranslatorService translatorService) {
    this.translatorService = translatorService;
  }

  /**
   * Returns a ModelAndView object for the "/translator" endpoint.
   * The ModelAndView object contains the view name "translator/translator".
   *
   * @return a ModelAndView object for the "/translator" endpoint
   */
  @GetMapping("/translator")
  public ModelAndView translator() {
    return new ModelAndView("translator/translator");
  }

  /**
   *    * This method returns a ResponseEntity object containing a list of supported languages.
   * It is responsible for handling the "/languages" endpoint.
   *
   * @return a ResponseEntity object containing a list of supported languages
   * @throws IOException if an error occurs while retrieving the supported languages
   */
  @GetMapping("/languages")
  public ResponseEntity<List<String>> languages() throws IOException {
    List<String> languages = translatorService
               .getSupportedLanguages();
    return ResponseEntity.ok().body(languages);
  }

  @PostMapping("/translatorText")
  public ResponseEntity<Map<String, String>> executeTranslatorText(
          @RequestBody Map<String, String> request) {
    String textToTranslate = request.get("textToTranslate");
    String targetLanguageCode = request.get("targetLanguageCode");

    Map<String, String> response = new HashMap<>();
    StringBuilder translatedTextBuilder = new StringBuilder();
    try {
      CompletableFuture<List<Translation>> translatedTextAsync =
              translatorService.translateTextAsync(textToTranslate, targetLanguageCode);
      List<Translation> translatedTextList = translatedTextAsync.get();

      for (Translation translation : translatedTextList) {
        translatedTextBuilder.append(translation.getTranslatedText()).append(" ");
      }
      response.put("translatedResult", translatedTextBuilder.toString().trim());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      response.put("error", "Error while waiting for translation to complete");
    } catch (Exception e) {
      response.put("error", "Error while translating text: " + e.getMessage());
    }

    return ResponseEntity.ok(response);
  }
}