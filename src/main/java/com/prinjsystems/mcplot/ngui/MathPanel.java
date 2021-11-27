package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.MathEvaluatorPool;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class MathPanel extends JPanel {
    private List<FunctionCard> functionCards;
    private List<Constant> constants;

    public MathPanel() {
        setLayout(new BorderLayout());
    }

    public void init(PlottingPanel plottingPanel) {
        plottingPanel.setMathPanel(this);
        MathEvaluatorPool.getInstance().setFunctionsDoneTask(plottingPanel::repaint);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        functionCards = new ArrayList<>();
        constants = new ArrayList<>();

        FunctionPanel functionPanel = new FunctionPanel(functionCards, constants, plottingPanel);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.functions"), new JScrollPane(functionPanel));

        ConstantsPanel constantsPanel = new ConstantsPanel(constants);
        tabbedPane.addTab(BUNDLE.getString("workspace.panels.constants"), new JScrollPane(constantsPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void recalculateAllFunctions() {
        functionCards.forEach(functionCard -> functionCard.recalculateFunction(constants));
    }
}
