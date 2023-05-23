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

    private DefaultListModel<String> clientListModel;
    private JList<String> clientJList;

    private DefaultListModel<String> colorListModel;
    private JList<String> colorJList;
    private DefaultListModel<String> shapeListModel;
    private JList<String> shapeJList;


    public RemoteWhiteBoard(Socket socket, DataInputStream in, DataOutputStream out, String username) throws RemoteException {

        this.socket = socket;
        this.in = in;
        this.out = out;
        this.username = username;


//        this.frame = new JFrame("Whiteboard");
//        managerWhiteBoardPanel = new WhiteBoardPanel(this.socket, this.in, this.out, this);
//        this.frame.add(managerWhiteBoardPanel);
//        Button clearButton = new Button("Clear");
//        Button redButton = new Button("Red");
//        Button blueButton = new Button("Blue");
//        Button greenButton = new Button("Green");
//        Button eraseButton = new Button("Erase");
//        Panel buttonPanel = new Panel();
//        buttonPanel.add(clearButton);
//        buttonPanel.add(redButton);
//        buttonPanel.add(blueButton);
//        buttonPanel.add(greenButton);
//        buttonPanel.add(eraseButton);
//
//        JTextField fillOvalSizeField = new JTextField(5);
//        Button confirmFillOvalSizeButton = new Button("Confirm");
//        Panel fillOvalSizePanel = new Panel();
//        fillOvalSizePanel.add(fillOvalSizeField);
//        fillOvalSizePanel.add(confirmFillOvalSizeButton);
//        this.frame.add(fillOvalSizePanel, BorderLayout.WEST);
//
//        Button fillOvalButton = new Button("Oval");
//        Button fillRectangleButton = new Button("Rectangle");
//        Button fillTriangleButton = new Button("Triangle");
//        Button fillSquareButton = new Button("Square");
//        Button fillStarButton = new Button("Star");
//        Panel fillShapePanel = new Panel();
//        fillShapePanel.add(fillOvalButton);
//        fillShapePanel.add(fillRectangleButton);
//        fillShapePanel.add(fillTriangleButton);
//        fillShapePanel.add(fillSquareButton);
//        fillShapePanel.add(fillStarButton);
//        this.frame.add(fillShapePanel, BorderLayout.NORTH);
//
//        Button addTextButton = new Button("Add Text");
//        Button addShapeButton = new Button("Add Shape");
//        String[] fontList = {"Arial", "Times New Roman", "Courier New", "Comic Sans MS", "Impact"};
//        JList fontJList = new JList(fontList);
//        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane fontJScrollPane = new JScrollPane(fontJList);
//        Panel TextPanel = new Panel();
//        TextPanel.add(addTextButton);
//        TextPanel.add(fontJScrollPane);
//        TextPanel.add(addShapeButton);
//        this.frame.add(TextPanel, BorderLayout.EAST);
//
//        TextField chatField = new TextField(20);
//        chatHistoryPane = new JTextPane();
//        chatHistoryPane.setEditable(false);
//        JScrollPane chatHistoryField = new JScrollPane(chatHistoryPane);
//        chatHistoryField.setPreferredSize(new Dimension(200, 200));
//        Button sendButton = new Button("Send");
//        Panel chatPanel = new Panel();
//        chatPanel.add(chatField);
//        chatPanel.add(sendButton);
//        chatPanel.add(chatHistoryField);
//        this.frame.add(chatPanel, BorderLayout.NORTH);
//
//
//
//        this.frame.add(buttonPanel, BorderLayout.SOUTH);
//        this.frame.pack();
//        this.frame.setVisible(true);
//        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.frame.setLocationRelativeTo(null);
//        this.frame.setVisible(true);

        frame = new JFrame("Whiteboard");
        frame.setSize(1500, 1500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        managerWhiteBoardPanel = new WhiteBoardPanel(this.socket, this.in, this.out, this);
        frame.add(managerWhiteBoardPanel, BorderLayout.CENTER);

        JTextField fillOvalSizeField = new JTextField(5);
        Button confirmFillOvalSizeButton = new Button("Confirm");


        // Fill Shape Panel

        shapeListModel = new DefaultListModel<>();
        String[] shapes = {"Oval", "Rectangle", "Triangle", "Square", "Star", "Circle", "Line"};
        for (String shape : shapes) {
            shapeListModel.addElement(shape);
        }
        shapeJList = new JList<>(shapeListModel);
        JScrollPane shapeScrollPane = new JScrollPane(shapeJList);
        shapeScrollPane.setPreferredSize(new Dimension(250, 80));
        JPanel shapePanel = createButtonPanel();
        shapePanel.add(shapeScrollPane, BorderLayout.CENTER);
        shapePanel.add(fillOvalSizeField, BorderLayout.NORTH);
        shapePanel.add(confirmFillOvalSizeButton, BorderLayout.NORTH);
        frame.add(shapePanel, BorderLayout.WEST);

        colorListModel = new DefaultListModel<>();
        String[] colors = {"red", "blue", "green", "black", "white", "yellow", "pink", "orange", "gray", "cyan", "magenta", "lightGray", "darkGray", "darkRed", "darkGreen", "darkBlue"};
        for (String color : colors) {
            colorListModel.addElement(color);
        }
        colorJList = new JList<>(colorListModel);
        JScrollPane colorScrollPane = new JScrollPane(colorJList);
        colorScrollPane.setPreferredSize(new Dimension(250, 80));
        shapePanel.add(colorScrollPane, BorderLayout.SOUTH);


        // Text Panel
        Button addTextButton = new Button("Add Text");
        Button addShapeButton = new Button("Add Shape");
        String[] fontList = {"Arial", "Times New Roman", "Courier New", "Comic Sans MS", "Impact"};
        JList<String> fontJList = new JList<>(fontList);
        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane fontJScrollPane = new JScrollPane(fontJList);
        JPanel textPanel = createButtonPanel();
        textPanel.add(addTextButton);
        textPanel.add(fontJScrollPane);
        textPanel.add(addShapeButton);
        frame.add(textPanel, BorderLayout.EAST);

        // Chat Panel
        JTextField chatField = new JTextField(20);
        chatHistoryPane = new JTextPane();
        chatHistoryPane.setEditable(false);
        JScrollPane chatHistoryField = new JScrollPane(chatHistoryPane);
        chatHistoryField.setPreferredSize(new Dimension(200, 200));
        Button sendButton = new Button("Send");
        JPanel chatPanel = createButtonPanel();
        chatPanel.add(chatField);
        chatPanel.add(sendButton);
        chatPanel.add(chatHistoryField);
        frame.add(chatPanel, BorderLayout.NORTH);

        // Save Image Panel
        Button saveImageButton = new Button("Save Image on MongoDB");
        Button loadImageButton = new Button("Load Image from MongoDB");
        Button saveImageToLocal = new Button("Save Image to Local");
        JPanel imagePanel = createButtonPanel();
        imagePanel.add(saveImageButton);
        imagePanel.add(loadImageButton);
        imagePanel.add(saveImageToLocal);
        frame.add(imagePanel, BorderLayout.PAGE_END);

        clientListModel = new DefaultListModel<>();
        clientJList = new JList<>(clientListModel);
        JScrollPane clientScrollPane = new JScrollPane(clientJList);
        clientScrollPane.setPreferredSize(new Dimension(120, 80));
        frame.add(clientScrollPane, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);

        colorJList.addListSelectionListener(e -> {
            try {
                java.lang.reflect.Field field = Class.forName("java.awt.Color").getField(colorJList.getSelectedValue());
                Color color = (Color) field.get(null);
                managerWhiteBoardPanel.setColor(color);
            } catch (Exception ex) {
                if (colorJList.getSelectedValue() == "darkRed"){
                    try {
                        managerWhiteBoardPanel.setColor(new Color(204, 0, 0));
                    } catch (RemoteException exc) {
                        throw new RuntimeException(exc);
                    }
                } else if (colorJList.getSelectedValue() == "darkBlue") {
                    try {
                        managerWhiteBoardPanel.setColor(new Color(0, 0, 204));
                    } catch (RemoteException exc) {
                        throw new RuntimeException(exc);
                    }
                } else if (colorJList.getSelectedValue() == "darkGreen"){
                    try {
                        managerWhiteBoardPanel.setColor(new Color(0, 204, 0));
                    } catch (RemoteException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }
        });

        confirmFillOvalSizeButton.addActionListener(e -> {
            try {
                managerWhiteBoardPanel.setFillSize(Integer.parseInt(fillOvalSizeField.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        shapeJList.addListSelectionListener(e -> {
            try {
                managerWhiteBoardPanel.setShape(shapeJList.getSelectedValue());
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

    public JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        return panel;
    }

    public DefaultListModel<String> getClientListModel() {
        return clientListModel;
    }

    public String getUsername() {
        return username;
    }
}
