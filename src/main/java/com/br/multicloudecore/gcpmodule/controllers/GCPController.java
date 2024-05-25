package com.br.multicloudecore.gcpmodule.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GCPController {

    @GetMapping("/")
    public ModelAndView loginGCP() {
        return new ModelAndView("login-gcloud");
    }

    @GetMapping("/signUpFirebase")
    public ModelAndView signUpFirebase() {
        return new ModelAndView("signUpFirebase");
    }

    @GetMapping("/indexGCP")
    public ModelAndView mainGCP() {
        return new ModelAndView("indexGCP");
    }

    @GetMapping("/visionFaceDetection")
    public ModelAndView visionFaceDetection() {
        return new ModelAndView("ai/vision/vision-face");
    }

    @RequestMapping("/visionLandmarkDetection")
    public ModelAndView visionLandmarkDetection() {
        return new ModelAndView("ai/vision/vision-landmark");
    }

    @RequestMapping("/sentiment")
    public ModelAndView sentiment() {
        return  new ModelAndView("ai/sentiment-analysis/sentiment-analysis");
    }

    @GetMapping("/logout-gcp")
    public ModelAndView logoutCP() {
        return new ModelAndView("login-gcloud");
    }
}
