package com.prinjsystems.mcplot.gui;

import com.prinjsystems.mcplot.math.FunctionEvaluator;
import com.prinjsystems.mcplot.math.PlottableFunction;
import com.prinjsystems.mcplot.math.Variable;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class Workspace extends JFrame {
    private static final Workspace INSTANCE = new Workspace();

    private JSplitPane splitPane;

    private JPanel functionsPane;
    private AtomicInteger functionGridIndex;
    private Map<PlottableFunction, Path2D> functions;
    private List<FunctionCard> functionCards;

    private JPanel variablesPane;
    private AtomicInteger variableGridIndex;
    private List<Variable> variables;
    private List<VariableCard> variableCards;

    private Workspace() {
        // Setup JFrame
        super(BUNDLE.getString("workspace.title"));
        setSize(1280, 720);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup components
        PlottingPanel plottingPanel = PlottingPanel.getInstance();

        JTabbedPane actionsPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        /*
        Functions pane
         */
        functionsPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbcFunctions = new GridBagConstraints();
        gbcFunctions.gridwidth = 0;
        gbcFunctions.fill = GridBagConstraints.HORIZONTAL;
        gbcFunctions.anchor = GridBagConstraints.FIRST_LINE_START;
        functionsPane.add(new JPanel(), gbcFunctions);

        functions = new HashMap<>();
        plottingPanel.setFunctions(functions);

        functionCards = new ArrayList<>();

        functionGridIndex = new AtomicInteger();
        JButton createFunction = new JButton(BUNDLE.getString("workspace.actions.createFunction"));
        createFunction.addActionListener(e -> createFunction.getActionMap().get("create-function")
                .actionPerformed(e));
        createFunction.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl N"),
                "create-function");
        createFunction.getActionMap().put("create-function", new AbstractAction() {
            private static final long serialVersionUID = 5490534849088567L;

            @Override
            public void actionPerformed(ActionEvent e) {
                gbcFunctions.weightx = 0;
                gbcFunctions.weighty = 0;
                FunctionCard functionCard = new FunctionCard(functionGridIndex.get(), new PlottableFunction());
                functions.put(functionCard.getFunction(), null);
                functionCards.add(functionCard);
                functionsPane.add(functionCard, gbcFunctions, functionGridIndex.getAndIncrement());
                functionsPane.validate();
            }
        });
        functionsPane.add(createFunction, gbcFunctions, functionGridIndex.get());

        gbcFunctions.weightx = 1;
        gbcFunctions.weighty = 1;
        functionsPane.add(new JPanel(), gbcFunctions, functionGridIndex.get() + 1);

        actionsPane.addTab(BUNDLE.getString("workspace.actions.functions"), new JScrollPane(functionsPane));

        /*
        Variables pane
         */
        variablesPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbcVariables = new GridBagConstraints();
        gbcVariables.gridwidth = 0;
        gbcVariables.fill = GridBagConstraints.HORIZONTAL;
        gbcVariables.anchor = GridBagConstraints.FIRST_LINE_START;
        variablesPane.add(new JPanel(), gbcVariables);

        variables = new ArrayList<>();
        FunctionEvaluator.setVariableList(variables);

        variableCards = new ArrayList<>();

        variableGridIndex = new AtomicInteger();
        JButton createVariable = new JButton(BUNDLE.getString("workspace.actions.createVariable"));
        createVariable.addActionListener(e -> createVariable.getActionMap().get("create-variable")
                .actionPerformed(e));
        createVariable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl V"),
                "create-variable");
        createVariable.getActionMap().put("create-variable", new AbstractAction() {
            private static final long serialVersionUID = -8620701650644805231L;

            @Override
            public void actionPerformed(ActionEvent e) {
                gbcVariables.weightx = 0;
                gbcVariables.weighty = 0;
                VariableCard variableCard = new VariableCard(new Variable());
                variables.add(variableCard.getVariable());
                variableCards.add(variableCard);
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

        /*
        Bottom toolbar
         */
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        JButton zoomIn = new JButton("+");
        zoomIn.setToolTipText(BUNDLE.getString("workspace.actions.zoomIn"));

        zoomIn.getInputMap().put(KeyStroke.getKeyStroke("+"), "zoomIn");

        Action zoomInAction = new AbstractAction() {
            private static final long serialVersionUID = 2698683814900947736L;

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
            private static final long serialVersionUID = 6251288571572112778L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                plottingPanel.getActionMap().get("zoom-out").actionPerformed(actionEvent);
            }
        };
        zoomOut.getActionMap().put("zoomOut", zoomOutAction);
        zoomOut.addActionListener(zoomOutAction);
        toolBar.add(zoomOut);

        JButton moveLeft = new JButton("⯇");
        moveLeft.setToolTipText(BUNDLE.getString("workspace.actions.moveLeft"));
        moveLeft.addActionListener(e -> plottingPanel.getActionMap().get("left").actionPerformed(e));
        toolBar.add(moveLeft);

        JButton moveUp = new JButton("⯅");
        moveUp.setToolTipText(BUNDLE.getString("workspace.actions.moveUp"));
        moveUp.addActionListener(e -> plottingPanel.getActionMap().get("up").actionPerformed(e));
        toolBar.add(moveUp);

        JButton moveDown = new JButton("⯆");
        moveDown.setToolTipText(BUNDLE.getString("workspace.actions.moveDown"));
        moveDown.addActionListener(e -> plottingPanel.getActionMap().get("down").actionPerformed(e));
        toolBar.add(moveDown);

        JButton moveRight = new JButton("⯈");
        moveRight.setToolTipText(BUNDLE.getString("workspace.actions.moveRight"));
        moveRight.addActionListener(e -> plottingPanel.getActionMap().get("right").actionPerformed(e));
        toolBar.add(moveRight);

        JButton plot = new JButton("↻");
        plot.setToolTipText(BUNDLE.getString("workspace.actions.plot"));
        plot.addActionListener(e -> {
            for (FunctionCard functionCard : functionCards) {
                functionCard.updateFunction();
            }
            for (VariableCard variableCard : variableCards) {
                variableCard.updateVariable();
            }
            plottingPanel.plot();
        });
        toolBar.add(plot);

        add(toolBar, BorderLayout.PAGE_END);

        /*
        Menu bar
         */
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu(BUNDLE.getString("workspace.menu.file"));

        JMenuItem saveMenuItem = new JMenuItem(BUNDLE.getString("workspace.menu.file.save"));
        saveMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            String fileFormat = "mcw";
            fileChooser.setFileFilter(new FileNameExtensionFilter(BUNDLE.getString("workspace.menu.file.fileFormat"),
                    fileFormat));
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
                File file = new File(selectedFile
                        + (selectedFile.endsWith("." + fileFormat) ? "" : "." + fileFormat));
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                    oos.writeObject(functions);
                    oos.writeObject(variables);
                    setTitle(BUNDLE.getString("workspace.title") + " - " + fileChooser.getSelectedFile().getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(saveMenuItem);

        JMenuItem openMenuItem = new JMenuItem(BUNDLE.getString("workspace.menu.file.open"));

        openMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter(BUNDLE.getString("workspace.menu.file.fileFormat"),
                    "mcw"));
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                    functions.clear();
                    functions.putAll((Map<PlottableFunction, Path2D>) ois.readObject());

                    variables.clear();
                    variables.addAll((List<Variable>) ois.readObject());

                    /*
                    Updating functions pane
                     */
                    functionsPane.removeAll();
                    functionGridIndex.set(0);
                    for (PlottableFunction function : functions.keySet()) {
                        gbcFunctions.weightx = 0;
                        gbcFunctions.weighty = 0;
                        FunctionCard functionCard = new FunctionCard(functionGridIndex.get(), function);
                        functionsPane.add(functionCard, gbcFunctions, functionGridIndex.getAndIncrement());
                    }

                    functionsPane.add(createFunction, gbcFunctions, functionGridIndex.get());

                    gbcFunctions.weightx = 1;
                    gbcFunctions.weighty = 1;
                    functionsPane.add(new JPanel(), gbcFunctions, functionGridIndex.get() + 1);
                    functionsPane.revalidate();
                    
                    /*
                    Updating variables pane
                     */
                    variablesPane.removeAll();
                    variableGridIndex.set(0);
                    for (Variable variable : variables) {
                        gbcVariables.weightx = 0;
                        gbcVariables.weighty = 0;
                        VariableCard variableCard = new VariableCard(variable);
                        variablesPane.add(variableCard, gbcVariables, variableGridIndex.getAndIncrement());
                    }

                    variablesPane.add(createVariable, gbcVariables, variableGridIndex.get());

                    gbcVariables.weightx = 1;
                    gbcVariables.weighty = 1;
                    variablesPane.add(new JPanel(), gbcVariables, variableGridIndex.get() + 1);
                    variablesPane.revalidate();

                    if (WorkspaceSettings.isPlotOnOpen()) {
                        plottingPanel.plot();
                    }

                    setTitle(BUNDLE.getString("workspace.title") + " - " + fileChooser.getSelectedFile().getName());
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(openMenuItem);

        fileMenu.addSeparator();

        JMenuItem settingsMenuItem = new JMenuItem(BUNDLE.getString("generics.settings"));
        settingsMenuItem.addActionListener(e -> WorkspaceSettings.showWorkspaceSettingsDialog(this));
        fileMenu.add(settingsMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
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

        if (WorkspaceSettings.isOpenMaximized()) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        }
    }

    public static Workspace getInstance() {
        return INSTANCE;
    }

    void removeFunctionCard(FunctionCard functionCard) {
        functionCards.remove(functionCard);
        functions.remove(functionCard.getFunction());
        functionsPane.remove(functionCard);
        functionGridIndex.getAndDecrement();
        functionsPane.revalidate();
        PlottingPanel.getInstance().plot();
    }

    void removeVariableCard(VariableCard variableCard) {
        variableCards.remove(variableCard);
        variables.remove(variableCard.getVariable());
        variablesPane.remove(variableCard);
        variableGridIndex.getAndDecrement();
        variablesPane.revalidate();
        PlottingPanel.getInstance().plot();
    }
}
