package co.edu.univalle.NiceLook.Service;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import co.edu.univalle.NiceLook.DTO.GoogleUser;

@Service
public class GoogleAuthService {

    private final String CLIENT_ID = "1055219399395-41dgigof08dichfdip9uf0f5affo5vcp.apps.googleusercontent.com";

    public GoogleUser verifyToken(String idTokenString) throws Exception {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        )
        .setAudience(Collections.singletonList(CLIENT_ID))
        .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken != null) {

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            GoogleUser user = new GoogleUser();
            user.setEmail(email);
            user.setNombre(name);

            return user;

        } else {
            throw new Exception("Token inválido");
        }
    }
}