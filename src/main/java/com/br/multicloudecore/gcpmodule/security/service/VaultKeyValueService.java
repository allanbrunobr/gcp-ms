package com.br.multicloudecore.gcpmodule.security.service;

import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.Map;
import java.util.Optional;

@Service
public class VaultKeyValueService {

    private final VaultTemplate vaultTemplate;

    public VaultKeyValueService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public String getSecretValue(String key) {
        VaultResponseSupport<KeyValueData> response = vaultTemplate.read("secret/data/gcp", KeyValueData.class);
        return response == null ? null : Optional.ofNullable(response.getData())
                .map(KeyValueData::getData)
                .map(data -> data.get(key))
                .orElse(null);
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
