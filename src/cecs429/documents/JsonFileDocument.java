package cecs429.documents;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
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
        return new StringReader(getJsonData("body"));
    }

    @Override
    public String getTitle() {
        return getJsonData("title");
    }

    private String getJsonData(String s) {
        String content = null;
        try (JsonReader reader = new JsonReader(new BufferedReader(new FileReader(mFilePath.toString())))) {
            reader.beginObject();
            while (reader.hasNext()) {
                if (reader.nextName().equals(s)) {
                    content = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            System.out.println(e);

        }
        return content;
    }


    public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
        return new JsonFileDocument(documentId, absolutePath);
    }
}
