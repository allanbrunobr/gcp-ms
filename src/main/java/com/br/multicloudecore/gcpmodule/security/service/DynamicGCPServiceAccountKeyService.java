package com.br.multicloudecore.gcpmodule.security.service;

import com.br.multicloudecore.gcpmodule.exceptions.DynamicGCPServiceAccountKeyException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.Optional;

@Service
public class DynamicGCPServiceAccountKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicGCPServiceAccountKeyService.class);
    private final VaultTemplate vaultTemplate;

    @Value("${spring.cloud.vault.roleset}")
    private String roleset;

    public DynamicGCPServiceAccountKeyService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public Optional<String> getGcpServiceAccountKey() {
        try {
            VaultResponseSupport<GcpServiceAccountKey> response = vaultTemplate.read("gcp/roleset/" + this.roleset + "/key", GcpServiceAccountKey.class);
            if (response != null && response.getData() != null) {
                GcpServiceAccountKey accessToken = response.getData();
                if (accessToken != null) {
                    return Optional.of(accessToken.getPrivate_key_data());
                } else {
                    throw new DynamicGCPServiceAccountKeyException("GCP access token is null");
                }
            } else {
                throw new DynamicGCPServiceAccountKeyException("Failed to retrieve GCP access token from Vault");
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to retrieve GCP access token from Vault", e);
        }
        return Optional.empty().toString().describeConstable();
    }

    @Getter
    @Setter
    public static class GcpServiceAccountKey {
        private String private_key_data;

    }
}

