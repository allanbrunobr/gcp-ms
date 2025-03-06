package com.br.multicloudecore.gcpmodule.services.ai;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public abstract class AIServiceBase {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceBase.class);

    @Value("${gcp.credentials.path}")
    private String credentialsFilePath;

    protected CredentialsProvider getCredentialsProvider() throws IOException {
        try {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(credentialsFilePath));
            return FixedCredentialsProvider.create(credentials);
        } catch (IOException e) {
            logger.error("Error loading credentials from file: {}", credentialsFilePath, e);
            throw e;
        }
    }
}
