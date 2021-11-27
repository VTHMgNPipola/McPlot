package com.prinjsystems.mcplot.ngui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.MessageFormat;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;
import static com.prinjsystems.mcplot.Main.VERSION;
import static com.prinjsystems.mcplot.PreferencesHelper.KEY_OPEN_MAXIMIZED;
import static com.prinjsystems.mcplot.PreferencesHelper.PREFERENCES;

public class Workspace extends JFrame {
    private WorkspaceController workspaceController;

    private MathPanel mathPanel;
    private PlottingPanel plottingPanel;
    private JSplitPane splitPane;

    public Workspace() {
        super(MessageFormat.format(BUNDLE.getString("workspace.title"), VERSION));
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void init() {
        initContentPane();
        initMenu();
        pack();
        setLocationRelativeTo(null);

        splitPane.setDividerLocation(0.3);
        plottingPanel.init();

        if (PREFERENCES.getBoolean(KEY_OPEN_MAXIMIZED, false)) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        }
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = new JMenu(BUNDLE.getString("workspace.menu.file"));
        menuBar.add(file);

        JMenuItem save = new JMenuItem(BUNDLE.getString("workspace.menu.file.save"));
        file.add(save);
        save.addActionListener(e -> mathPanel.save());

        JMenuItem open = new JMenuItem(BUNDLE.getString("workspace.menu.file.open"));
        file.add(open);
        open.addActionListener(e -> mathPanel.open(plottingPanel));

        file.addSeparator();

        // Export submenu
        JMenu export = new JMenu(BUNDLE.getString("workspace.menu.file.export"));
        file.add(export);

        JMenuItem exportSpreadsheet = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.spreadsheet"));
        export.add(exportSpreadsheet);

        JMenuItem exportText = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.text"));
        export.add(exportText);

        JMenuItem exportPgfplots = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.pgfplots"));
        export.add(exportPgfplots);

        JMenuItem exportPicture = new JMenuItem(BUNDLE.getString("workspace.menu.file.export.picture"));
        export.add(exportPicture);

        file.addSeparator();

        JMenuItem settings = new JMenuItem(BUNDLE.getString("generics.settings"));
        file.add(settings);
        settings.addActionListener(workspaceController::openSettings);

        file.addSeparator();

        JMenuItem about = new JMenuItem(BUNDLE.getString("workspace.menu.file.about"));
        file.add(about);
        about.addActionListener(workspaceController::openAbout);
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        contentPane.setPreferredSize(new Dimension(1024, 576));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        contentPane.add(splitPane, BorderLayout.CENTER);
        splitPane.setContinuousLayout(false);
        splitPane.setDividerLocation(0.3);

        plottingPanel = new PlottingPanel();
        splitPane.setRightComponent(plottingPanel);
        workspaceController = new WorkspaceController(plottingPanel);

        mathPanel = new MathPanel();
        splitPane.setLeftComponent(mathPanel);
        mathPanel.init(plottingPanel);
    }
}
