package com.prinjsystems.mcplot;

public class Language {
    public static final Language[] LANGUAGES = new Language[]{new Language("English (US)", "en-US"),
            new Language("Português (BR)", "pt-BR")};

    private String displayName;
    private String tag;

    public Language(String displayName, String tag) {
        this.displayName = displayName;
        this.tag = tag;
    }

    public static Language getFromTag(String tag) {
        for (Language language : LANGUAGES) {
            if (language.getTag().equals(tag)) {
                return language;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
