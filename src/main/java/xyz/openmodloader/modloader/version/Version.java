package xyz.openmodloader.modloader.version;

public class Version {

    private final int major, minor, patch;
    private final String tag;

    public Version(String version) {
        if (!version.matches("[0-9]+\\.[0-9]+\\.[0-9]+(|-\\w+)"))
            throw new IllegalArgumentException("Version string does not match SemVer specification");
        String[] parts0 = version.split("-");
        String[] parts = parts0[0].split("\\.");
        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
        if (parts0.length > 1)
            this.tag = parts0[1];
        else
            this.tag = "";
    }

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, "");
    }

    public Version(int major, int minor, int patch, String tag) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.tag = tag == null ? "" : tag;
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
        return getMajor() + "." + getMinor() + "." + getPatch() + (tag == null ? "" :  "-" + tag);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + patch;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Version other = (Version) obj;
        if (major != other.major)
            return false;
        if (minor != other.minor)
            return false;
        if (patch != other.patch)
            return false;
        return true;
    }
}
