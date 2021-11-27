package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class MathPanel extends JPanel {
    private List<Function> functions;
    private List<Constant> constants;

    public MathPanel() {
        setLayout(new BorderLayout());
    }

    public void init(PlottingPanel plottingPanel) {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        functions = new ArrayList<>();
        constants = new ArrayList<>();

        FunctionPanel functionPanel = new FunctionPanel(functions, constants, plottingPanel);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), new JScrollPane(functionPanel));

        ConstantsPanel constantsPanel = new ConstantsPanel(constants);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), new JScrollPane(constantsPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
