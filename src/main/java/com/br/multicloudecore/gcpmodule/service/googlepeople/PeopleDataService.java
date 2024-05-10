package com.br.multicloudecore.gcpmodule.service.googlepeople;

import com.br.multicloudecore.gcpmodule.models.people.PersonInfo;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class PeopleDataService {
    private static final String PERSON_FIELDS = "names,photos";

    public PersonInfo getUserInfo(String accessToken) throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null));
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(credentials);

        PeopleService peopleService = new PeopleService.Builder(httpTransport, jsonFactory, adapter)
                .setApplicationName("Seu Aplicativo")
                .build();

        Person profile = peopleService.people().get("people/me")
                .setPersonFields(PERSON_FIELDS)
                .execute();

        String name = profile.getNames().get(0).getDisplayName();
        String photoUrl = profile.getPhotos().get(0).getUrl();

        return new PersonInfo(name, photoUrl);
    }
}

