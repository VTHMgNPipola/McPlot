package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.math.FunctionEvaluator;
import com.prinjsystems.mcplot.math.PlottableFunction;
import com.prinjsystems.mcplot.math.Variable;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class Workspace extends JFrame {
    private JSplitPane splitPane;

    public Workspace() {
        // Setup JFrame
        super(BUNDLE.getString("workspace.title"));
        setSize(1280, 720);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup components
        PlottingPanel plottingPanel = PlottingPanel.getInstance();

        JTabbedPane actionsPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        JPanel functionsPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbcFunctions = new GridBagConstraints();
        gbcFunctions.gridwidth = 0;
        gbcFunctions.fill = GridBagConstraints.HORIZONTAL;
        gbcFunctions.anchor = GridBagConstraints.FIRST_LINE_START;
        functionsPane.add(new JPanel(), gbcFunctions);

        Map<PlottableFunction, Path2D> functions = new HashMap<>();
        plottingPanel.setFunctions(functions);

        AtomicInteger functionGridIndex = new AtomicInteger();
        JButton createFunction = new JButton(BUNDLE.getString("workspace.actions.createFunction"));
        createFunction.addActionListener(e -> createFunction.getActionMap().get("create-function")
                .actionPerformed(e));
        createFunction.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl N"),
                "create-function");
        createFunction.getActionMap().put("create-function", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gbcFunctions.weightx = 0;
                gbcFunctions.weighty = 0;
                FunctionCard functionCard = new FunctionCard(functionGridIndex.get());
                functions.put(functionCard.getFunction(), null);
                functionsPane.add(functionCard, gbcFunctions, functionGridIndex.getAndIncrement());
                functionsPane.validate();
            }
        });
        functionsPane.add(createFunction, gbcFunctions, functionGridIndex.get());

        gbcFunctions.weightx = 1;
        gbcFunctions.weighty = 1;
        functionsPane.add(new JPanel(), gbcFunctions, functionGridIndex.get() + 1);

        actionsPane.addTab(BUNDLE.getString("workspace.actions.functions"), new JScrollPane(functionsPane));

        JPanel variablesPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbcVariables = new GridBagConstraints();
        gbcVariables.gridwidth = 0;
        gbcVariables.fill = GridBagConstraints.HORIZONTAL;
        gbcVariables.anchor = GridBagConstraints.FIRST_LINE_START;
        variablesPane.add(new JPanel(), gbcVariables);

        List<Variable> variables = new ArrayList<>();
        FunctionEvaluator.setVariableList(variables);

        AtomicInteger variableGridIndex = new AtomicInteger();
        JButton createVariable = new JButton(BUNDLE.getString("workspace.actions.createVariable"));
        createVariable.addActionListener(e -> createVariable.getActionMap().get("create-variable")
                .actionPerformed(e));
        createVariable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl V"),
                "create-variable");
        createVariable.getActionMap().put("create-variable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gbcVariables.weightx = 0;
                gbcVariables.weighty = 0;
                VariableCard variableCard = new VariableCard();
                variables.add(variableCard.getVariable());
                variablesPane.add(variableCard, gbcVariables, variableGridIndex.getAndIncrement());
                variablesPane.validate();
            }
        });
        variablesPane.add(createVariable, gbcVariables, variableGridIndex.get());

        gbcVariables.weightx = 1;
        gbcVariables.weighty = 1;
        variablesPane.add(new JPanel(), gbcVariables, variableGridIndex.get() + 1);

        actionsPane.addTab(BUNDLE.getString("workspace.actions.variables"), new JScrollPane(variablesPane));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionsPane, plottingPanel);
        splitPane.setContinuousLayout(true);
        add(splitPane);

        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        JButton zoomIn = new JButton("+");
        zoomIn.setToolTipText(BUNDLE.getString("workspace.actions.zoomIn"));

        zoomIn.getInputMap().put(KeyStroke.getKeyStroke("+"), "zoomIn");

        Action zoomInAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                plottingPanel.getActionMap().get("zoom-in").actionPerformed(actionEvent);
            }
        };
        zoomIn.getActionMap().put("zoomIn", zoomInAction);
        zoomIn.addActionListener(zoomInAction);
        toolBar.add(zoomIn);

        JButton zoomOut = new JButton("-");
        zoomOut.setToolTipText(BUNDLE.getString("workspace.actions.zoomOut"));

        zoomOut.getInputMap().put(KeyStroke.getKeyStroke("-"), "zoomOut");

        Action zoomOutAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                plottingPanel.getActionMap().get("zoom-out").actionPerformed(actionEvent);
            }
        };
        zoomOut.getActionMap().put("zoomOut", zoomOutAction);
        zoomOut.addActionListener(zoomOutAction);
        toolBar.add(zoomOut);

        JButton moveLeft = new JButton("⯇");
        moveLeft.addActionListener(e -> plottingPanel.getActionMap().get("left").actionPerformed(e));
        toolBar.add(moveLeft);

        JButton moveUp = new JButton("⯅");
        moveUp.addActionListener(e -> plottingPanel.getActionMap().get("up").actionPerformed(e));
        toolBar.add(moveUp);

        JButton moveDown = new JButton("⯆");
        moveDown.addActionListener(e -> plottingPanel.getActionMap().get("down").actionPerformed(e));
        toolBar.add(moveDown);

        JButton moveRight = new JButton("⯈");
        moveRight.addActionListener(e -> plottingPanel.getActionMap().get("right").actionPerformed(e));
        toolBar.add(moveRight);

        add(toolBar, BorderLayout.PAGE_END);
    }

    /**
     * Finishes setting up this frame. Should be called after {@link #setVisible(boolean)}.
     *
     * @param args Command line arguments. List of supported arguments: <ul><li>-maximized/-M: Starts McPlot in a
     *             maximized frame</li></ul>
     */
    public void configure(String[] args) {
        splitPane.setDividerLocation(0.20);
        PlottingPanel.getInstance().resetRanges();
        requestFocus();

        for (String arg : args) {
            if (arg.equals("-maximized") || arg.equals("-M")) {
                setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            }
        }
    }
}
