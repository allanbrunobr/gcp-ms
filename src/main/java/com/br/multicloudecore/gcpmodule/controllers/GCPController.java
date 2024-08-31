package com.br.multicloudecore.gcpmodule.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GCPController {

    @GetMapping(value = "/")
    public ModelAndView loginGCP() {
        return new ModelAndView("login-gcloud");
    }

    @GetMapping(value = "/signUpFirebase")
    public ModelAndView signUpFirebase() {
        return new ModelAndView("signUpFirebase");
    }

    @GetMapping(value = "/indexGCP")
    public ModelAndView mainGCP() {
        return new ModelAndView("indexGCP");
    }

    @GetMapping(value = "/visionFaceDetection")
    public ModelAndView visionFaceDetection() {
        return new ModelAndView("ai/vision/vision-face");
    }

    @GetMapping("/visionLandmarkDetection")
    public ModelAndView visionLandmarkDetection() {
        return new ModelAndView("ai/vision/vision-landmark");
    }

    @GetMapping("/sentiment")
    public ModelAndView sentiment() {
        return  new ModelAndView("ai/sentiment-analysis/sentiment-analysis");
    }

    @GetMapping("/logout-gcp")
    public ModelAndView logoutCP() {
        return new ModelAndView("login-gcloud");
    }

    @GetMapping("/trancribeMedicalText")
    public ModelAndView trancribeMedicalText() {
        return new ModelAndView("ai/transcribe-medical/transcribe-medical");
    }

}
