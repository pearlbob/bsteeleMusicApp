package com.bsteele.bsteeleMusicApp.client.songs;

import static java.util.Objects.hash;

/**
 * A version identifier for multiple instances of a given section.
 */
public class SectionVersion implements Comparable<SectionVersion> {

    private Section section;

    SectionVersion(Section section) {
        this(section,0, 0);
    }

    SectionVersion(Section section, int version, int sourceLength) {
        this.section = section;
        this.version = version;
        this.sourceLength = sourceLength;
    }

    /**
     * Return the generic section for this section version.
     * @return the generic section
     */
    public final Section getSection() {
        return section;
    }

    /**
     *  Return the numeric count for this section version.
     * @return the numeric count
     */
    public final int getVersion() {
        return version;
    }

    /**
     * The character length used to parse this section version from the original source.
     * @return the original character length.
     */
    public final int getParseLength() {
        return sourceLength;
    }

    /**
     * The external facing string that represents the section version to the user.
     * @return the string
     */
    @Override
    public String toString() {
        //  note: designed to go to the user display
        return getSection().getAbbreviation() + (version > 0 ? Integer.toString(version) : "");
    }

    @Override
    public int hashCode() {
        //  do not include source length
        return hash(getSection(), version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SectionVersion other = (SectionVersion) obj;

        //  do not include source length
        return this.getSection() == other.getSection()
                && this.version == other.version;
    }

    @Override
    public int compareTo(SectionVersion o) {
        if (getSection() != o.getSection()) {
            return getSection().compareTo(o.getSection());
        }

        if (version != o.version) {
            return version < o.version ? -1 : 1;
        }
        return 0;
    }

    private int version;
    private int sourceLength;
}
