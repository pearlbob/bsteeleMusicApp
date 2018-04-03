package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;
import java.util.Set;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Part {
    private PartType partType;
    private String name;
    private Set<PartSection> sections;

    public PartType getPartType() {
        return partType;
    }

    public void setPartType(PartType partType) {
        this.partType = partType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PartSection> getSections() {
        return sections;
    }

    public void setSections(Set<PartSection> sections) {
        this.sections = sections;
    }
}
