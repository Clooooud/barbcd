package io.github.clooooud.barbcd.data.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GBookApi {

    private PublicCredentials publicCredentials;

    public GBookApi(PublicCredentials publicCredentials) {
        this.publicCredentials = publicCredentials;
    }

    public Volume.VolumeInfo getBook(String isbn) throws IOException {
        Books booksService = getBooksService();
        Books.Volumes.List request = booksService.volumes().list("isbn:" + isbn);
        request.getRequestHeaders().set("x-goog-api-key", this.publicCredentials.getApiKey());

        Volumes volumes = request.execute();

        if (volumes.getTotalItems() <= 0) {
            return null;
        }

        return volumes.getItems().get(0).getVolumeInfo();
    }

    private Books getBooksService() {
        try {
            return new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), null)
                    .setApplicationName("barbcd")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
