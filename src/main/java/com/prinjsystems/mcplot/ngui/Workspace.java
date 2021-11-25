package com.prinjsystems.mcplot.ngui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class Workspace extends JFrame {
    private final WorkspaceController workspaceController;

    private PlottingPanel plottingPanel;
    private JSplitPane splitPane;

    public Workspace() {
        super(BUNDLE.getString("workspace.title"));
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        workspaceController = new WorkspaceController();
    }

    public void init() {
        initMenu();
        initContentPane();
        pack();
        setLocationRelativeTo(null);

        splitPane.setDividerLocation(0.3);
        plottingPanel.init();
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = new JMenu(BUNDLE.getString("workspace.menu.file"));
        menuBar.add(file);

        JMenuItem save = new JMenuItem(BUNDLE.getString("workspace.menu.file.save"));
        file.add(save);
        save.addActionListener(workspaceController::save);

        JMenuItem open = new JMenuItem(BUNDLE.getString("workspace.menu.file.open"));
        file.add(open);
        open.addActionListener(workspaceController::open);

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
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(0.3);

        MathPanel mathPanel = new MathPanel();
        splitPane.setLeftComponent(mathPanel);
        mathPanel.init();

        plottingPanel = new PlottingPanel();
        splitPane.setRightComponent(plottingPanel);
    }
}
