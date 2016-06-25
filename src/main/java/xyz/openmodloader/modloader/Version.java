package xyz.openmodloader.modloader;

public class Version {

    private final int major, minor, patch;
    private final String tag;

    public Version(String version) {
        String[] parts0 = version.split("-");
        String[] parts = parts0[0].split("\\.");
        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
        if (parts0.length > 1)
            this.tag = parts0[1];
        else
            this.tag = null;
    }

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    public Version(int major, int minor, int patch, String tag) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.tag = tag;
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

    public String getTag() {
        return tag;
    }

    public boolean atLeast(Version b) {
        if (major > b.major) {
            return true;
        } else if (minor > b.minor) {
            return true;
        }
        return major == b.major && minor == b.minor && patch >= b.patch;
    }

    @Override
    public String toString() {
        return getMajor() + "." + getMinor() + "." + getPatch();
    }
}
