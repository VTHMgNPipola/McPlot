package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.ConstantCard;
import com.prinjsystems.mcplot.nmath.Constant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ConstantsPanel extends JPanel {
    private final List<Constant> constants;

    private final AtomicInteger constantCount;

    public ConstantsPanel(List<Constant> constants) {
        super(new MigLayout());
        this.constants = constants;
        constantCount = new AtomicInteger(1);

        Constant firstConstant = new Constant();
        add(new ConstantCard(firstConstant, constantCount.getAndIncrement()), "pushx, span, growx");
        constants.add(firstConstant);

        JButton addConstantCard = new JButton(BUNDLE.getString("workspace.actions.createConstant"));
        add(addConstantCard, "growx");
        addConstantCard.addActionListener(e -> {
            Constant constant = new Constant();
            add(new ConstantCard(constant, constantCount.getAndIncrement()), "pushx, span, growx",
                    getComponentCount() - 1);
            constants.add(constant);
            updateUI();
        });
    }
}
