import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CodeEditor extends JPanel {

    private JTextArea textField;

    public CodeEditor() {
        setLayout(new BorderLayout());

        textField = new JTextArea();
        textField.setRows(40);
        textField.setColumns(40);

        JScrollPane scrollPane = new JScrollPane(textField);
        add(scrollPane);

        JScrollPane lineNumberScrollPane = new LineNumberScrollPane(textField);
        add(lineNumberScrollPane, BorderLayout.WEST);
    }

    public JTextArea getTextField() {
        return textField;
    }

    public String[] getCodeLines() {
        return textField.getText().split("\\n");
    }

    public void setText(String content) {
        textField.setText(content);
    }

    private static class LineNumberScrollPane extends JScrollPane {
        private JTextArea textArea;
        private JTextArea lineNumberArea;

        public LineNumberScrollPane(JTextArea textArea) {
            this.textArea = textArea;

            lineNumberArea = new JTextArea("1");
            lineNumberArea.setBackground(Color.LIGHT_GRAY);
            lineNumberArea.setEditable(false);
            lineNumberArea.setFocusable(false);
            lineNumberArea.setBorder(new EmptyBorder(0, 5, 0, 5));

            setRowHeaderView(lineNumberArea);
            setViewportView(textArea);

            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateLineNumbers();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateLineNumbers();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateLineNumbers();
                }
            });

            updateLineNumbers();
        }

        private void updateLineNumbers() {
            SwingUtilities.invokeLater(() -> {
                int totalLines = textArea.getLineCount();
                StringBuilder lineNumbers = new StringBuilder();

                for (int i = 1; i <= totalLines; i++) {
                    lineNumbers.append(i).append("\n");
                }

                lineNumberArea.setText(lineNumbers.toString());
            });
        }
    }
}



// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTextArea;

// class CodeEditor extends JPanel {

//     private JTextArea textField;

//     public CodeEditor() {
//         textField = new JTextArea();
//         textField.setRows(40); 
//         textField.setColumns(40);
//         JScrollPane scrollPane = new JScrollPane(textField);
//         add(scrollPane);
//     }

//     public JTextArea getTextField() {
//         return textField;
//     }

//     public String[] getCodeLines() {
//         return textField.getText().split("\\n");
//     }

//     public void setText(String content) {
//         textField.setText(content);
//     }
// }
