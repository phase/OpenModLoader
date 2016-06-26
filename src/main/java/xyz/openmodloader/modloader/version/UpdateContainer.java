package xyz.openmodloader.modloader.version;

public interface UpdateContainer {
    Version getLatestVersion();

    String[] getChangelog(Version version);
}
