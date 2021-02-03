package org.example.entities;

public class ItemNameAlternative {

    private String name;
    private String alternativeFor;

    public ItemNameAlternative(String name, String alternativeFor) {
        this.name = name;
        this.alternativeFor = alternativeFor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlternativeFor() {
        return alternativeFor;
    }

    public void setAlternativeFor(String alternativeFor) {
        this.alternativeFor = alternativeFor;
    }
}
