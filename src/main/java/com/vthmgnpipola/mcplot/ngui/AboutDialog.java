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

package com.vthmgnpipola.mcplot.ngui;

import java.awt.Font;
import java.awt.Window;
import java.text.MessageFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;
import static com.vthmgnpipola.mcplot.Main.VERSION;

public class AboutDialog extends MDialog {
    private final Window parent;

    public AboutDialog(Window parent) {
        super(parent, BUNDLE.getString("about.title"), ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.parent = parent;
    }

    @Override
    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new MigLayout("insets 40, flowy"));

        JLabel mcplot = new JLabel("McPlot");
        contentPane.add(mcplot, "alignx center");
        mcplot.setFont(new Font("Helvetica", Font.BOLD, 40));

        JLabel description = new JLabel(BUNDLE.getString("about.description"));
        contentPane.add(description, "alignx center");

        JLabel license = new JLabel(BUNDLE.getString("about.license"));
        contentPane.add(license, "alignx center");

        JLabel version = new JLabel(MessageFormat.format(BUNDLE.getString("about.version"), VERSION));
        contentPane.add(version, "alignx center");

        setContentPane(contentPane);
    }
}
