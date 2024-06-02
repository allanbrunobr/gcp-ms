package com.br.multicloudecore.gcpmodule.security.service;

import com.br.multicloudecore.gcpmodule.models.security.KeyValueData;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.Map;

@Service
public class VaultKeyValueService {

    private final VaultTemplate vaultTemplate;

    public VaultKeyValueService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public String getSecretValue(String key) {
        VaultResponseSupport<KeyValueData> response = vaultTemplate.read("secret/gcp", KeyValueData.class);
        if (response != null
                && response.getData() != null) {
                return response.getData().getData().get(key);
            }

        throw new RuntimeException("Failed to retrieve secret value from Vault");
    }

    public static class KeyValueData {
        private Map<String, String> data;

        public Map<String, String> getData() {
            return data;
        }

        public void setData(Map<String, String> data) {
            this.data = data;
        }
    }
}
