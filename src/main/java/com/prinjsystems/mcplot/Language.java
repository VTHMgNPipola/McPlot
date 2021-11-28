/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2021  VTHMgNPipola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
