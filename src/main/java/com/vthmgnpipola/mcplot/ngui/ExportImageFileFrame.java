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

import com.formdev.flatlaf.icons.FlatFileViewDirectoryIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatApplyIcon;
import com.vthmgnpipola.mcplot.ngui.icons.FlatCopyIcon;
import com.vthmgnpipola.mcplot.nmath.Constant;
import com.vthmgnpipola.mcplot.nmath.Function;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

import static com.vthmgnpipola.mcplot.Main.BUNDLE;

public class ExportImageFileFrame extends ExportFunctionsFrame {
    private static final FileChooserExtension EXTENSION = new FileChooserExtension(
            BUNDLE.getString("export.image.extensionFilter"), "png",
            "png", "jpg", "tiff", "bmp");

    private JTextField filename;
    private JCheckBox enableAntialias;

    public ExportImageFileFrame(Map<String, Function> functionMap, Collection<Constant> constants,
                                Map<String, Double> constantValues, PlottingPanel plottingPanel) {
        super(BUNDLE.getString("export.image.title"), functionMap, constants, constantValues, plottingPanel);
    }

    @Override
    public void init() {
        initContentPane();
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(plottingPanel));
    }

    @Override
    public void export() {
        dispose();

        Path path = Path.of(filename.getText());
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            ImageIO.write(getExportedImage(), EXTENSION.getFileType(path.getFileName().toString()), outputStream);

            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.success"),
                    BUNDLE.getString("generics.successDialog"), JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.error"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    private void copyToClipboard() {
        dispose();

        try {
            TransferableImage transferableImage = new TransferableImage(getExportedImage());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferableImage, null);

            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.success"),
                    BUNDLE.getString("generics.successDialog"), JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(this, BUNDLE.getString("export.error"),
                    BUNDLE.getString("generics.errorDialog"), JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    private BufferedImage getExportedImage() {
        BufferedImage image = new BufferedImage(plottingPanel.getWidth(), plottingPanel.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        if (enableAntialias.isSelected()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        plottingPanel.paintComponent(g);
        return image;
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new MigLayout("insets 15", "[]15", "[]10"));
        setContentPane(contentPane);

        JLabel filenameLabel = new JLabel(BUNDLE.getString("export.image.filename"));
        contentPane.add(filenameLabel);
        JButton selectFile = new JButton(new FlatFileViewDirectoryIcon());
        contentPane.add(selectFile, "split 2");
        selectFile.setToolTipText(BUNDLE.getString("export.image.selectFile.tooltip"));
        selectFile.addActionListener(e -> {
            int result = openSaveDialog(EXTENSION.getFilter());
            if (result == JFileChooser.APPROVE_OPTION) {
                String selectedFile = EXTENSION.getPathWithExtension(FILE_CHOOSER.getSelectedFile().getAbsolutePath());

                filename.setText(selectedFile);
            }
        });
        filename = new JTextField();
        contentPane.add(filename, "growx, wrap");

        enableAntialias = new JCheckBox(BUNDLE.getString("export.image.enableAntialias"));
        contentPane.add(enableAntialias, "span");
        enableAntialias.setToolTipText(BUNDLE.getString("export.image.enableAntialias.tooltip"));
        enableAntialias.setSelected(true);

        JButton copyToClipboard = new JButton(BUNDLE.getString("export.image.copy"), new FlatCopyIcon());
        contentPane.add(copyToClipboard, "span, split 2, alignx right");
        copyToClipboard.setToolTipText(BUNDLE.getString("export.image.copy.tooltip"));
        copyToClipboard.addActionListener(e -> copyToClipboard());

        JButton export = new JButton(BUNDLE.getString("export.image.apply"), new FlatApplyIcon());
        contentPane.add(export, "alignx right");
        export.addActionListener(e -> export());
    }

    private record TransferableImage(BufferedImage image) implements Transferable {
        private static final DataFlavor[] DATA_FLAVORS = new DataFlavor[]{DataFlavor.imageFlavor};

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return DATA_FLAVORS;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }
}
