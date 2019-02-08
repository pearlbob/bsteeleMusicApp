package com.bsteele.bsteeleMusicApp.shared.songs;

import java.util.Set;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Part {
    private PartType partType;
    private String name;
    private Set<PartSection> sections;

    public final PartType getPartType() {
        return partType;
    }

    public final void setPartType(PartType partType) {
        this.partType = partType;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final Set<PartSection> getSections() {
        return sections;
    }

    public final void setSections(Set<PartSection> sections) {
        this.sections = sections;
    }
}
