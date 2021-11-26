package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.nmath.Constant;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class MathPanel extends JPanel {
    private List<Constant> constants;

    public MathPanel() {
        setLayout(new BorderLayout());
    }

    public void init() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        FunctionPanel functionPanel = new FunctionPanel();
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), new JScrollPane(functionPanel));

        constants = new ArrayList<>();
        ConstantsPanel constantsPanel = new ConstantsPanel(constants);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), new JScrollPane(constantsPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
