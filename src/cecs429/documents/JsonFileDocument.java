package cecs429.documents;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

public class JsonFileDocument implements FileDocument {
    private int mDocumentId;
    private Path mFilePath;

    public JsonFileDocument(int id, Path absoluteFilePath) {
        mDocumentId = id;
        mFilePath = absoluteFilePath;
    }

    @Override
    public Path getFilePath() {
        return this.mFilePath;
    }

    @Override
    public int getId() {
        return this.mDocumentId;
    }

    @Override
    public Reader getContent() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFilePath.toString()));

            Gson gson = new Gson();
            JsonObject corpus = gson.fromJson( bufferedReader, JsonObject.class);
            String content = corpus.getAsJsonObject("content").toString();
            return new StringReader(content);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTitle() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFilePath.toString()));

            Gson gson = new Gson();
            JsonObject corpus = gson.fromJson( bufferedReader, JsonObject.class);
            return corpus.getAsJsonObject("title").toString();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
        return new JsonFileDocument(documentId, absolutePath);
    }
}
