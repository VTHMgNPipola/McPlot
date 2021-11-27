package com.prinjsystems.mcplot.ngui;

import com.prinjsystems.mcplot.ngui.components.ConstantCard;
import com.prinjsystems.mcplot.nmath.Constant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import static com.prinjsystems.mcplot.Main.BUNDLE;

public class ConstantsPanel extends JPanel {
    private final List<ConstantCard> constantCards;
    private final List<Constant> constants;

    private AtomicInteger index;

    public ConstantsPanel(List<Constant> constants) {
        super(new MigLayout());
        this.constantCards = new ArrayList<>();
        this.constants = constants;
        index = new AtomicInteger(1);

        if (constants.size() != 0) {
            constants.forEach(c -> {
                ConstantCard constantCard = new ConstantCard(c, constants, this, index.getAndIncrement());
                add(constantCard, "pushx, span, growx");
                constantCards.add(constantCard);
            });
        } else {
            Constant firstConstant = new Constant();
            ConstantCard firstConstantCard = new ConstantCard(firstConstant, constants, this,
                    index.getAndIncrement());
            add(firstConstantCard, "pushx, span, growx");
            constants.add(firstConstant);
            constantCards.add(firstConstantCard);
        }

        JButton addConstantCard = new JButton(BUNDLE.getString("workspace.actions.createConstant"));
        add(addConstantCard, "pushx, span, growx");
        addConstantCard.addActionListener(e -> {
            Constant constant = new Constant();
            ConstantCard constantCard = new ConstantCard(constant, constants, this, index.getAndIncrement());
            add(constantCard, "pushx, span, growx", getComponentCount() - 1);
            constants.add(constant);
            constantCards.add(constantCard);
            updateUI();
        });
    }

    public void removeConstantCard(ConstantCard constantCard) {
        remove(constantCard);
        constantCards.remove(constantCard);

        index = new AtomicInteger(1);
        constantCards.forEach(fc -> fc.setIndex(index.getAndIncrement()));

        updateUI();
    }
}
