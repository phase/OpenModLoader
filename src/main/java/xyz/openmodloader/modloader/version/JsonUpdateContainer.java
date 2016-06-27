package xyz.openmodloader.modloader.version;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUpdateContainer implements UpdateContainer {
    private Version latestVersion;
    private Map<Version, String[]> changelogs;
    private final URL url;

    public JsonUpdateContainer(URL url) {
        this.url = url;
    }

    @Override
    public Version getLatestVersion() {
        if (latestVersion == null) {
            retrieveData();
        }
        return latestVersion;
    }

    @Override
    public String[] getChangelog(Version version) {
        if (changelogs == null) {
            retrieveData();
        }
        return changelogs.get(version);
    }

    private void retrieveData() {
        try {
            InputStream stream = url.openStream();
            JsonObject object = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            this.latestVersion = new Version(object.get("version").getAsString());
            this.changelogs = new HashMap<>();
            object = object.get("changelogs").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                JsonArray array = entry.getValue().getAsJsonArray();
                String[] changelog = new String[array.size()];
                for (int i = 0; i < changelog.length; i++) {
                    changelog[i] = array.get(i).getAsString();
                }
                this.changelogs.put(new Version(entry.getKey()), changelog);
            }
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
