package xyz.openmodloader.modloader.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class JsonUpdateContainer implements UpdateContainer {
    private final Version latestVersion;
    private final Map<Version, String[]> changelogs;

    public JsonUpdateContainer(InputStream stream) {
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
    }

    @Override
    public Version getLatestVersion() {
        return latestVersion;
    }

    @Override
    public String[] getChangelog(Version version) {
        return changelogs.get(version);
    }
}
