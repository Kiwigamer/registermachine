import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

class Accumulator extends JPanel {

    private int value;
    private JLabel label;
    private JLabel valueLabel;

    public Accumulator() {
        this.value = 0;

        label = new JLabel("Accumulator:");
        valueLabel = new JLabel(String.valueOf(value));

        label.setPreferredSize(new Dimension(50, 10));
        valueLabel.setPreferredSize(new Dimension(50, 10));

        setLayout(new GridLayout(1, 2));
        add(label);
        add(valueLabel);
    }

    public void setValue(int value) {
        this.value = value;
        update();
    }

    public int getValue() {
        return value;
    }

    public void update() {
        valueLabel.setText(String.valueOf(value));
    }

    public void reset() {
        value = 0;
        update();
    }
}