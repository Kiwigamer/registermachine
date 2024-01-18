import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class AccumulatorProgram {

    private DataCell[] cells;
    private Accumulator accumulator;
    private CodeEditor codeEditor;
    private CommandRegister commandRegister;
    private boolean done;

    private JButton runButton;
    private JButton stepButton;
    private JButton resetButton;
    private JButton loadButton;
    private JButton saveButton;

    public AccumulatorProgram() {
        JFrame frame = new JFrame("Accumulator Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupUI3(frame);

        // frame.setLayout(new GridLayout(3, 2));

        // cells = new DataCell[20];
        // for (int i = 0; i < cells.length; i++) {
        //     cells[i] = new DataCell(i);
        //     frame.add(cells[i]);
        // }

        // accumulator = new Accumulator();
        // frame.add(accumulator);

        // codeEditor = new CodeEditor();
        // frame.add(codeEditor);

        // commandRegister = new CommandRegister();
        // frame.add(commandRegister);

        // runButton = new JButton("Run Code");
        // runButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         runCode();
        //     }
        // });
        // frame.add(runButton);

        // stepButton = new JButton("Step");
        // stepButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         step();
        //     }
        // });
        // frame.add(stepButton);

        // resetButton = new JButton("Reset");
        // resetButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         resetAll();
        //     }
        // });
        // frame.add(resetButton);

        // frame.pack();
        // frame.setVisible(true);

        resetAll();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AccumulatorProgram();
            }
        });
    }

    private void runCode() {
        resetAll();

        while (!done) {
            step();
        }
    }

    private void step() {
        if (commandRegister.getValue() == codeEditor.getCodeLines().length + 1 && !done) {
            end();
            return;
        } else if (commandRegister.getValue() > codeEditor.getCodeLines().length + 1 || done) {
            resetAll();
        }

        int newInstructionPointer = executeLine(codeEditor.getCodeLines()[commandRegister.getValue() - 1],
                commandRegister.getValue());

        if (newInstructionPointer == -1) {
            // JOptionPane.showMessageDialog(null, "Invalid command in line " + commandRegister.getValue(), "Error",
                    // JOptionPane.ERROR_MESSAGE);
            done = true;
            return;
        }

        if (newInstructionPointer == -2) {
            JOptionPane.showMessageDialog(null, "Infinite loop detected at line " + commandRegister.getValue(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            done = true;
            return;
        }

        if (newInstructionPointer == commandRegister.getValue()) {
            end();
            return;
        }

        commandRegister.setValue(newInstructionPointer);
        commandRegister.update();
    }

    private void end() {
        done = true;
        commandRegister.increment(1);;
        commandRegister.update();
    }

    private int executeLine(String line, int instructionPointer) {
        String[] tokens = line.split(" ");

        if (tokens.length <= 0) {
            return instructionPointer + 1;
        } else if (tokens.length <= 1) {
            if (tokens[0].equals("END")) {
                return instructionPointer;
            }
            return instructionPointer + 1;
        }

        String command = tokens[0];
        int argument = Integer.parseInt(tokens[1]);

        switch (command) {
            case "DLOAD":
                dloadValue(argument);
                break;
            case "STORE":
                storeValue(argument);
                break;
            case "LOAD":
                loadValue(argument);
                break;
            case "ADD":
                addValue(argument);
                break;
            case "SUB":
                subtractValue(argument);
                break;
            case "MULT":
                multiplyValue(argument);
                break;
            case "DIV":
                divideValue(argument);
                break;
            case "JUMP":
                return jump(argument, instructionPointer);
            case "JGE":
                return conditionalJump(argument, accumulator.getValue() >= 0, instructionPointer);
            case "JGT":
                return conditionalJump(argument, accumulator.getValue() > 0, instructionPointer);
            case "JLE":
                return conditionalJump(argument, accumulator.getValue() <= 0, instructionPointer);
            case "JLT":
                return conditionalJump(argument, accumulator.getValue() < 0, instructionPointer);
            case "JEQ":
                return conditionalJump(argument, accumulator.getValue() == 0, instructionPointer);
            case "JNE":
                return conditionalJump(argument, accumulator.getValue() != 0, instructionPointer);
            default:
                JOptionPane.showMessageDialog(null, "Invalid command: " + command + ", in line " + instructionPointer,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return -1;
        }
        return instructionPointer + 1;
    }

    private int conditionalJump(int jumpTo, boolean condition, int instructionPointer) {
        if (condition) {
            return jump(jumpTo, instructionPointer);
        } else {
            return instructionPointer + 1;
        }
    }

    private int jump(int jumpTo, int instructionPointer) {
        if (jumpTo == instructionPointer)
            return -2;
        return jumpTo;
    }

    private void dloadValue(int value) {
        try {
            int intValue = value;
            accumulator.setValue(intValue);
            accumulator.update();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for DLOAD command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadValue(int value) {
        try {
            accumulator.setValue(cells[value].getValue());
            accumulator.update();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for LOAD command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void storeValue(int cellNumber) {
        try {
            if (cellNumber >= 0 && cellNumber < cells.length) {
                cells[cellNumber].setValue(accumulator.getValue());
                cells[cellNumber].update();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid cell number: " + cellNumber, "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for STORE command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addValue(int cellNumber) {
        try {
            accumulator.setValue(accumulator.getValue() + cells[cellNumber].getValue());
            accumulator.update();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for ADD command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void subtractValue(int cellNumber) {
        try {
            accumulator.setValue(accumulator.getValue() - cells[cellNumber].getValue());
            accumulator.update();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for SUBTRACT command", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void multiplyValue(int cellNumber) {
        try {
            accumulator.setValue(accumulator.getValue() * cells[cellNumber].getValue());
            accumulator.update();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for MULTIPLY command", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void divideValue(int cellNumber) {
        try {
            if (cells[cellNumber].getValue() != 0) {
                accumulator.setValue(accumulator.getValue() / cells[cellNumber].getValue());
                accumulator.update();
            } else {
                JOptionPane.showMessageDialog(null, "Division by zero", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Invalid value for DIVIDE command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetAll() {
        done = false;
        accumulator.reset();
        commandRegister.reset();
        for (DataCell cell : cells) {
            cell.reset();
        }
    }

    private void setupUI3(JFrame frame) {
        frame.setLayout(new BorderLayout());
    
        // Create a panel for the buttons and text editor on the left
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Buttons"),
                new EmptyBorder(10, 10, 10, 10))); // Add padding
    
        runButton = new JButton("Run Code");
        runButton.addActionListener(e -> runCode());
        leftPanel.add(runButton);
        leftPanel.add(Box.createVerticalStrut(10)); // Add vertical gap
    
        stepButton = new JButton("Step");
        stepButton.addActionListener(e -> step());
        leftPanel.add(stepButton);
        leftPanel.add(Box.createVerticalStrut(10)); // Add vertical gap
    
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetAll());
        leftPanel.add(resetButton);
        leftPanel.add(Box.createVerticalStrut(10)); // Add vertical gap
    
        // Add spacer
        leftPanel.add(Box.createVerticalStrut(10));

        // Load Button
        loadButton = new JButton("Load");
        loadButton.addActionListener(e -> load_file());
        leftPanel.add(loadButton);
        leftPanel.add(Box.createVerticalStrut(10)); // Add vertical gap

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> save_file());
        leftPanel.add(saveButton);

        // Create a panel for the info and cells on the center
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
    
        // Create a panel for the text editor in the center
        JPanel editorPanel = new JPanel();
        editorPanel.setLayout(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Text Editor"),
                new EmptyBorder(10, 10, 10, 10))); // Add padding
    
        codeEditor = new CodeEditor();
        editorPanel.add(codeEditor, BorderLayout.CENTER);
        centerPanel.add(editorPanel, BorderLayout.CENTER);
    
        // Create a panel for the info on the top right
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Add titled border and padding to the infoPanel
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Info"),
                new EmptyBorder(10, 10, 10, 10)));

        // Reduce the height of the accumulator
        accumulator = new Accumulator();
        accumulator.setPreferredSize(new Dimension(10, 50));
        accumulator.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.add(accumulator, gbc);

        gbc.gridy++;

        // Reduce the height of the command register
        commandRegister = new CommandRegister();
        commandRegister.setPreferredSize(new Dimension(10, 50));
        commandRegister.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.add(commandRegister, gbc);

        gbc.gridy++;

        // Create a panel for the cells in the lower right
        JPanel cellsPanel = new JPanel(new GridBagLayout());
        cellsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Cells"),
                new EmptyBorder(10, 10, 10, 10)));

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span two columns for the cellsPanel
        infoPanel.add(cellsPanel, gbc);

        // Initialize cells with border, padding, and reduced visibility
        cells = new DataCell[20];
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new DataCell(i);
            // Add border and padding to each cell
            cells[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
                    new EmptyBorder(10, 10, 10, 10)));

            GridBagConstraints cellConstraints = new GridBagConstraints();
            cellConstraints.gridx = gbc.gridx;
            cellConstraints.gridy = gbc.gridy;
            cellConstraints.gridwidth = 1;
            cellConstraints.gridheight = 1;
            cellConstraints.weightx = 1.0;
            cellConstraints.weighty = 1.0;
            cellConstraints.fill = GridBagConstraints.BOTH;

            cellsPanel.add(cells[i], cellConstraints);

            gbc.gridx++;
            if (gbc.gridx > 1) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        frame.add(infoPanel);

        // JPanel infoPanel = new JPanel();
        // infoPanel.setLayout(new GridLayout(3, 1));
        
        // infoPanel.setBorder(BorderFactory.createCompoundBorder(
        //         BorderFactory.createTitledBorder("Info"),
        //         new EmptyBorder(10, 10, 10, 10))); // Add padding
    
        // // Reduce the height of the accumulator
        // accumulator = new Accumulator();
        // accumulator.setPreferredSize(new Dimension(10, 10)); // Adjust the preferred size
        // infoPanel.add(accumulator);
    
        // // Reduce the height of the command register
        // commandRegister = new CommandRegister();
        // commandRegister.setPreferredSize(new Dimension(10, 10)); // Adjust the preferred size
        // infoPanel.add(commandRegister);
    
        // // Create a panel for the cells in the lower right
        // JPanel cellsPanel = new JPanel();
        // cellsPanel.setLayout(new GridLayout(0, 4, 10, 10)); // Rows of 4 cells with padding
        // cellsPanel.setBorder(BorderFactory.createCompoundBorder(
        //         BorderFactory.createTitledBorder("Cells"),
        //         new EmptyBorder(10, 10, 10, 10))); // Add padding
    
        // // Initialize cells with border, padding, and reduced visibility
        // cells = new DataCell[20];
        // for (int i = 0; i < cells.length; i++) {
        //     cells[i] = new DataCell(i);
        //     // Add border and padding to each cell
        //     cells[i].setBorder(BorderFactory.createCompoundBorder(
        //             BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1), // Reduced visibility
        //             new EmptyBorder(10, 10, 10, 10))); // Add padding
        //     cellsPanel.add(cells[i]);
        // }
    
        // infoPanel.add(cellsPanel);
    
        centerPanel.add(infoPanel, BorderLayout.EAST);
    
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);
    
        // Pack and set visible
        frame.pack();
        frame.setVisible(true);
    }

private void save_file() {
    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")); // Set initial directory
    int returnValue = fileChooser.showSaveDialog(null);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
            writer.write(codeEditor.getTextField().getText());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void load_file() {
    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")); // Set initial directory
    int returnValue = fileChooser.showOpenDialog(null);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
            String content = new String(Files.readAllBytes(selectedFile.toPath()));
            codeEditor.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// private void setupUI(JFrame frame) {
//     frame.setLayout(new BorderLayout());

//     // Create a panel for the buttons on the left
//     JPanel buttonPanel = new JPanel();
//     buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

//     // ... (existing button code)

//     frame.add(buttonPanel, BorderLayout.WEST);

//     // Create a panel for the text editor in the center
//     JPanel editorPanel = new JPanel();
//     editorPanel.setLayout(new BorderLayout());

//     codeEditor = new CodeEditor();
//     editorPanel.add(codeEditor, BorderLayout.CENTER);

//     frame.add(editorPanel, BorderLayout.CENTER);

//     // Create a panel for the info on the top right
//     JPanel infoPanel = new JPanel();
//     infoPanel.setLayout(new GridLayout(2, 1));

//     // ... (existing infoPanel code)

//     frame.add(infoPanel, BorderLayout.NORTH);

//     // Initialize cells
//     cells = new DataCell[20];
//     for (int i = 0; i < cells.length; i++) {
//         cells[i] = new DataCell(i);

//         // Add border and padding to each cell
//         cells[i].setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(Color.BLACK, 1),
//                 BorderFactory.createEmptyBorder(5, 5, 5, 5)
//         ));
//     }

//     // Create a panel for the cells in the lower right
//     JPanel cellsPanel = new JPanel();
//     cellsPanel.setLayout(new GridLayout(1, cells.length));

//     for (int i = 0; i < cells.length; i++) {
//         cellsPanel.add(cells[i]);
//     }

//     // Add border and padding to the cellsPanel
//     cellsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//     frame.add(cellsPanel, BorderLayout.SOUTH);

//     // Pack and set visible
//     frame.pack();
//     frame.setVisible(true);
// }


    // private int jge(int jumpToIf, int instructionPointer) {
    // if (accumulator.getValue() >= 0) {
    // return jumpToIf;
    // } else {
    // return instructionPointer + 1;
    // }
    // }

    // private int jgt(int jumpToIf, int instructionPointer) {
    // if (accumulator.getValue() > 0) {
    // return jumpToIf;
    // } else {
    // return instructionPointer + 1;
    // }
    // }

    // private int jle(int jumpToIf, int instructionPointer) {
    // if (accumulator.getValue() <= 0) {
    // return jumpToIf;
    // } else {
    // return instructionPointer + 1;
    // }
    // }

    // private int jlt(int jumpToIf, int instructionPointer) {
    // if (accumulator.getValue() < 0) {
    // return jumpToIf;
    // } else {
    // return instructionPointer + 1;
    // }
    // }

    // private int jeq(int jumpToIf, int instructionPointer) {
    // if (accumulator.getValue() == 0) {
    // return jumpToIf;
    // } else {
    // return instructionPointer + 1;
    // }
    // }

    // private int jne(int jumpToIf, int instructionPointer) {
    //     if (accumulator.getValue() != 0) {
    //         return jumpToIf;
    //     } else {
    //         return instructionPointer + 1;
    //     }
    // }
}