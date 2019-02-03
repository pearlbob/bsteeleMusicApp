package com.bsteele.bsteeleMusicApp.client.songs;

import javax.validation.constraints.NotNull;

import static java.util.Objects.hash;

/**
 * A version identifier for multiple numerical variations of a given section.
 */
public class SectionVersion implements Comparable<SectionVersion> {

    /**
     * A convenience constructor for a section without numerical variation.
     *
     * @param section the type of {@link Section} for this new section version.
     */
    public SectionVersion(@NotNull Section section) {
        this(section, 0);
    }

    /**
     * A constructor for the section version variation's representation.
     *
     * @param section     the type of {@link Section} for this new section version.
     * @param version     the number used to identify this variation.  By convention,
     *                   a zero value will not be expressed to the user and thus will be the default variation.
     */
    SectionVersion(@NotNull Section section, int version) {
        this.section = section;
        this.version = version;
        name = section.getAbbreviation() + (version > 0 ? Integer.toString(version) : "");
    }

    /**
     * Return the generic section for this section version.
     *
     * @return the generic section
     */
    public final Section getSection() {
        return section;
    }

    /**
     * Return the numeric count for this section version.
     *
     * @return the numeric count
     */
    public final int getVersion() {
        return version;
    }


    /**
     * Gets the internal name that will identify this specific section and version
     *
     * @return the name of the section version
     */
    public final String getId() {
        return name;
    }

    /**
     * The external facing string that represents the section version to the user.
     *
     * @return the string
     */
    @Override
    public String toString() {
        //  note: designed to go to the user display
        return name + ":";
    }

    /**
     * Gets a more formal name for the section version that can be presented to the user.
     *
     * @return the formal name
     */
    public String getFormalName() {
        //  note: designed to go to the user display
        return section.getFormalName() + (version > 0 ? Integer.toString(version) : "") + ":";
    }

    /**
     * The Java object hash code for this object.
     *
     * @return the hashcode
     */
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


    private final Section section;
    private final int version;
    private final String name;
}
