package xyz.openmodloader.modloader;

public class Version {

    private final int major, minor, patch;

    public Version(String version) {
        String[] parts = version.split("\\.");
        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
    }

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean atLeast(Version b) {
        if (major > b.major) {
            return true;
        } else if (minor > b.minor) {
            return true;
        }
        return major == b.major && minor == b.minor && patch >= b.patch;
    }
}
