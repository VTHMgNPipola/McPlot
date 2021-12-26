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

package com.vthmgnpipola.mcplot.ngui.components;

import com.vthmgnpipola.mcplot.Language;
import com.vthmgnpipola.mcplot.ngui.Workspace;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.Main.updateLookAndFeel;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_LANGUAGE;
import static com.vthmgnpipola.mcplot.PreferencesHelper.KEY_OPEN_MAXIMIZED;
import static com.vthmgnpipola.mcplot.PreferencesHelper.PREFERENCES;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_DARK_LAF;
import static com.vthmgnpipola.mcplot.PreferencesHelper.VALUE_LIGHT_LAF;

public class GeneralSettingsPanel extends JPanel {
    private static final Map<String, LookAndFeelPair> LAF_PAIRS = new HashMap<>();

    static {
        LAF_PAIRS.put(VALUE_LIGHT_LAF, new LookAndFeelPair(BUNDLE.getString("settings.general.laf.light"),
                VALUE_LIGHT_LAF));
        LAF_PAIRS.put(VALUE_DARK_LAF, new LookAndFeelPair(BUNDLE.getString("settings.general.laf.dark"),
                VALUE_DARK_LAF));
    }

    public GeneralSettingsPanel(Workspace workspace) {
        setLayout(new MigLayout("insets 15", "[]15", "[]10"));

        JLabel languageLabel = new JLabel(BUNDLE.getString("settings.general.language"));
        add(languageLabel);
        JComboBox<Language> language = new JComboBox<>(Language.LANGUAGES);
        add(language, "growx, wrap");
        Language selectedLanguage = Language.getFromTag(PREFERENCES.get(KEY_LANGUAGE,
                Locale.getDefault().toLanguageTag()));
        language.setSelectedItem(Objects.requireNonNullElse(selectedLanguage, Language.LANGUAGES[0]));
        language.addActionListener(e -> PREFERENCES.put(KEY_LANGUAGE,
                ((Language) Objects.requireNonNull(language.getSelectedItem())).getTag()));

        JLabel lafLabel = new JLabel(BUNDLE.getString("settings.general.laf"));
        add(lafLabel);
        JComboBox<LookAndFeelPair> laf = new JComboBox<>(LAF_PAIRS.values().toArray(new LookAndFeelPair[0]));
        add(laf, "growx, wrap");
        LookAndFeelPair selectedLaf = LAF_PAIRS.get(PREFERENCES.get(KEY_LAF, VALUE_LIGHT_LAF));
        laf.setSelectedItem(Objects.requireNonNullElse(selectedLaf, Language.LANGUAGES[0]));
        laf.addActionListener(e -> {
            String tag = ((LookAndFeelPair) Objects.requireNonNull(laf.getSelectedItem())).tag;
            PREFERENCES.put(KEY_LAF, tag);
            updateLookAndFeel(tag);
        });

        JCheckBox openMaximized = new JCheckBox(BUNDLE.getString("settings.general.openMaximized"),
                PREFERENCES.getBoolean(KEY_OPEN_MAXIMIZED, false));
        add(openMaximized, "span");
        openMaximized.addChangeListener(e -> PREFERENCES.putBoolean(KEY_OPEN_MAXIMIZED, openMaximized.isSelected()));
    }

    private record LookAndFeelPair(String description, String tag) {
        @Override
        public String toString() {
            return description;
        }
    }
}
