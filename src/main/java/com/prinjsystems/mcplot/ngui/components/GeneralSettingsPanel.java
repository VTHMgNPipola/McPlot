package com.prinjsystems.mcplot.ngui.components;

import com.prinjsystems.mcplot.Language;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;
import static com.prinjsystems.mcplot.PreferencesHelper.KEY_LANGUAGE;
import static com.prinjsystems.mcplot.PreferencesHelper.KEY_OPEN_MAXIMIZED;
import static com.prinjsystems.mcplot.PreferencesHelper.PREFERENCES;

public class GeneralSettingsPanel extends JPanel {
    public GeneralSettingsPanel() {
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

        JCheckBox openMaximized = new JCheckBox(BUNDLE.getString("settings.general.openMaximized"),
                PREFERENCES.getBoolean(KEY_OPEN_MAXIMIZED, false));
        add(openMaximized, "span");
        openMaximized.addChangeListener(e -> PREFERENCES.putBoolean(KEY_OPEN_MAXIMIZED, openMaximized.isSelected()));
    }
}
