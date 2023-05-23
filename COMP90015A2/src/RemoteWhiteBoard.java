import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class RemoteWhiteBoard{

    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    private JFrame frame;
    private WhiteBoardPanel managerWhiteBoardPanel;
    private JTextPane chatHistoryPane;
    private String username;


    public RemoteWhiteBoard(Socket socket, DataInputStream in, DataOutputStream out, String username) throws RemoteException {

        this.socket = socket;
        this.in = in;
        this.out = out;
        this.username = username;


        this.frame = new JFrame("Whiteboard");
        managerWhiteBoardPanel = new WhiteBoardPanel(this.socket, this.in, this.out, this);
        this.frame.add(managerWhiteBoardPanel);
        Button clearButton = new Button("Clear");
        Button redButton = new Button("Red");
        Button blueButton = new Button("Blue");
        Button greenButton = new Button("Green");
        Button eraseButton = new Button("Erase");
        Panel buttonPanel = new Panel();
        buttonPanel.add(clearButton);
        buttonPanel.add(redButton);
        buttonPanel.add(blueButton);
        buttonPanel.add(greenButton);
        buttonPanel.add(eraseButton);

        JTextField fillOvalSizeField = new JTextField(5);
        Button confirmFillOvalSizeButton = new Button("Confirm");
        Panel fillOvalSizePanel = new Panel();
        fillOvalSizePanel.add(fillOvalSizeField);
        fillOvalSizePanel.add(confirmFillOvalSizeButton);
        this.frame.add(fillOvalSizePanel, BorderLayout.WEST);

        Button fillOvalButton = new Button("Oval");
        Button fillRectangleButton = new Button("Rectangle");
        Button fillTriangleButton = new Button("Triangle");
        Button fillSquareButton = new Button("Square");
        Button fillStarButton = new Button("Star");
        Panel fillShapePanel = new Panel();
        fillShapePanel.add(fillOvalButton);
        fillShapePanel.add(fillRectangleButton);
        fillShapePanel.add(fillTriangleButton);
        fillShapePanel.add(fillSquareButton);
        fillShapePanel.add(fillStarButton);
        this.frame.add(fillShapePanel, BorderLayout.NORTH);

        Button addTextButton = new Button("Add Text");
        Button addShapeButton = new Button("Add Shape");
        String[] fontList = {"Arial", "Times New Roman", "Courier New", "Comic Sans MS", "Impact"};
        JList fontJList = new JList(fontList);
        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane fontJScrollPane = new JScrollPane(fontJList);
        Panel TextPanel = new Panel();
        TextPanel.add(addTextButton);
        TextPanel.add(fontJScrollPane);
        TextPanel.add(addShapeButton);
        this.frame.add(TextPanel, BorderLayout.EAST);

        TextField chatField = new TextField(20);
        chatHistoryPane = new JTextPane();
        chatHistoryPane.setEditable(false);
        JScrollPane chatHistoryField = new JScrollPane(chatHistoryPane);
        chatHistoryField.setPreferredSize(new Dimension(200, 200));
        Button sendButton = new Button("Send");
        Panel chatPanel = new Panel();
        chatPanel.add(chatField);
        chatPanel.add(sendButton);
        chatPanel.add(chatHistoryField);
        this.frame.add(chatPanel, BorderLayout.NORTH);



        this.frame.add(buttonPanel, BorderLayout.SOUTH);
        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        clearButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.clearImage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        redButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setColor(Color.RED);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        blueButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setColor(Color.BLUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        greenButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setColor(Color.GREEN);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        eraseButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setColor(Color.WHITE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        confirmFillOvalSizeButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setFillSize(Integer.parseInt(fillOvalSizeField.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillOvalButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape("Oval");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillRectangleButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape("Rectangle");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillTriangleButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape("Triangle");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillSquareButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape("Square");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillStarButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape("Star");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addTextButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape("Text");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fontJList.addListSelectionListener(e -> {
            try {
                managerWhiteBoardPanel.setFontType(fontList[fontJList.getSelectedIndex()]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addShapeButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShapeOrText(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addTextButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setShapeOrText(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sendButton.addActionListener(e -> {
            try {
                String message = chatField.getText();
                message = this.username + ": " + message;
                Document doc = this.getChatHistoryPane().getDocument();
                doc.insertString(doc.getLength(), message + "\n", null);
                chatField.setText("");
                managerWhiteBoardPanel.sendChatMessage(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


    }


    public void setColor(Color color) throws RemoteException {
        this.managerWhiteBoardPanel.setColor(color);
    }


    public void draw(int x, int y) throws RemoteException {
        this.managerWhiteBoardPanel.draw(x, y);
    }


    public void clearImage() throws RemoteException {
        this.managerWhiteBoardPanel.clearImage();
    }


    public void erase(int x, int y) throws RemoteException {
        this.managerWhiteBoardPanel.erase(x, y);
    }


    public void setFillSize(int fillOvalSize) throws RemoteException {
        this.managerWhiteBoardPanel.setFillSize(fillOvalSize);
    }


    public void displayFrame() {
        this.frame.setVisible(true);
    }

    public JTextPane getChatHistoryPane() {
        return chatHistoryPane;
    }


    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public JFrame getFrame() {
        return frame;
    }
}
