package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.FunctionCard;
import com.prinjsystems.mcplot.nmath.Constant;
import com.prinjsystems.mcplot.nmath.Function;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class FunctionPanel extends JPanel {
    private final List<Function> functions;

    private final AtomicInteger functionCount;

    public FunctionPanel(List<Function> functions, List<Constant> constants, PlottingPanel plottingPanel) {
        setLayout(new MigLayout());
        this.functions = functions;
        functionCount = new AtomicInteger(1);

        Function firstFunction = new Function();
        add(new FunctionCard(firstFunction, constants, plottingPanel, functionCount.getAndIncrement()),
                "pushx, span, growx");
        functions.add(firstFunction);

        JButton addFunctionCard = new JButton(BUNDLE.getString("workspace.actions.createFunction"));
        add(addFunctionCard, "growx");
        addFunctionCard.addActionListener(e -> {
            Function function = new Function();
            add(new FunctionCard(function, constants, plottingPanel, functionCount.getAndIncrement()),
                    "pushx, span, growx", getComponentCount() - 1);
            functions.add(function);
            updateUI();
        });
    }
}
