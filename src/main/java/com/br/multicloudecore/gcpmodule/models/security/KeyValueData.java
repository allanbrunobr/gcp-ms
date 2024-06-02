package com.br.multicloudecore.gcpmodule.models.security;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class KeyValueData {
    private Map<String, String> data;
}
