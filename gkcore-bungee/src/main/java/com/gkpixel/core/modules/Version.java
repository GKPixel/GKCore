package com.gkpixel.core.modules;

public class Version implements Comparable<Version> {
    public String version;

    public Version(String version) {
        if (version == null)
            throw new IllegalArgumentException("Version can not be null");
        if (!version.matches("[0-9]+(\\.[0-9]+)*(-beta[0-9]+)?"))
            throw new IllegalArgumentException("Invalid version format");
        this.version = version;
    }

    public final String get() {
        return this.version;
    }

    public boolean isSmallerThan(String target) {
        return compareTo(new Version(target)) < 0;
    }

    public boolean isSmallerThanOrEquals(String target) {
        return compareTo(new Version(target)) <= 0;
    }

    public boolean isGreaterThan(String target) {
        return compareTo(new Version(target)) > 0;
    }

    public boolean isGreaterThanOrEquals(String target) {
        return compareTo(new Version(target)) >= 0;
    }

    public boolean isEquals(String target) {
        return compareTo(new Version(target)) == 0;
    }

    @Override
    public int compareTo(Version that) {
        if (that == null)
            return 1;
        String[] thisParts = this.get().replaceAll("-beta[0-9]+", "").split("\\.");
        String[] thatParts = that.get().replaceAll("-beta[0-9]+", "").split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        return this.compareTo((Version) that) == 0;
    }

}