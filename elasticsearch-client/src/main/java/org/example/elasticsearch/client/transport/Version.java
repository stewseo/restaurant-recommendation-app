package org.example.elasticsearch.client.transport;

import org.example.elasticsearch.client.*;

import java.io.*;
import java.util.*;

public class Version {

    private final int major;
    private final int minor;
    private final int maintenance;
    private final boolean isPreRelease;

    /**
     * Parse a version string formatted using the standard Maven version format.
     *
     * @return the version, or {@code null} if the version could not be parsed.
     */
    public static Version parse(String version) {
        int hyphen = version.indexOf('-');
        boolean isPreRelease;
        if (hyphen >= 0) {
            version = version.substring(0, hyphen);
            isPreRelease = true;
        }
        else {
            isPreRelease = false;
        }

        String[] bits = version.split("\\.");
        try {
            int major = (bits.length >= 1) ? Integer.parseInt(bits[0]) : 0;
            int minor = (bits.length >= 2) ? Integer.parseInt(bits[1]) : 0;
            int maintenance = (bits.length >= 3) ? Integer.parseInt(bits[2]) : -1;
            return new Version(major, minor, maintenance, isPreRelease);
        }
        catch(NumberFormatException ex) {
            return null;
        }
    }

    public Version(int major, int minor, int maintenance, boolean isPreRelease) {
        this.major = major;
        this.minor = minor;
        this.maintenance = maintenance;
        this.isPreRelease = isPreRelease;
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int maintenance() {
        return maintenance;
    }

    public boolean isPreRelease() {
        return isPreRelease;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Version)) return false;
        Version that = (Version) other;
        return (major == that.major &&
                minor == that.minor &&
                maintenance == that.maintenance &&
                isPreRelease == that.isPreRelease);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, maintenance, isPreRelease);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(major);
        s.append('.');
        s.append(minor);
        if (maintenance != -1) {
            s.append('.');
            s.append(maintenance);
        }
        if (isPreRelease) {
            s.append('p');
        }
        return s.toString();
    }


    public static final Version VERSION;

    static {
        Version version = null;
        InputStream in = ApiClient.class.getResourceAsStream("version.properties");
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
                String versionStr = properties.getProperty("version");
                if (versionStr != null) {
                    version = Version.parse(versionStr);
                }
            } catch (Exception e) {
                // Failed to read version.properties file
            }
        }
        VERSION = version;
    }
}