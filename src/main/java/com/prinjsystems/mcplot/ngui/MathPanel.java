package com.prinjsystems.mcplot.ngui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class MathPanel extends JPanel {
    public MathPanel() {
        setLayout(new BorderLayout());
    }

    public void init() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        FunctionPanel functionPanel = new FunctionPanel();
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), functionPanel);

        ConstantsPanel constantsPanel = new ConstantsPanel();
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), constantsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
