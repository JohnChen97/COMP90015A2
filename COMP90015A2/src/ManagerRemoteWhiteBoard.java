import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class ManagerRemoteWhiteBoard {
//    private JPanel clientManagePanel;
    private String username;
//    private ArrayList<String> clientList;


    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    private JFrame frame;
    private ManagerWhiteBoardPanel managerWhiteBoardPanel;
    private JTextPane chatHistoryPane;

    private DefaultListModel<String> clientListModel;
    private JList<String> clientJList;



    public ManagerRemoteWhiteBoard(Socket socket, DataInputStream in, DataOutputStream out, String username) throws RemoteException {





        this.socket = socket;
        this.in = in;
        this.out = out;
        this.username = username;


        this.frame = new JFrame("Whiteboard");
        this.frame.setSize(1500, 1500);
        managerWhiteBoardPanel = new ManagerWhiteBoardPanel(this.socket, this.in, this.out, this);
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

        Button  saveImageButton = new Button("Save Image on MongoDB");
        this.frame.add(saveImageButton, BorderLayout.PAGE_START);

        Button loadImageButton = new Button("Load Image from MongoDB");
        this.frame.add(loadImageButton, BorderLayout.PAGE_END);

        Button saveImageToLocal = new Button("Save Image to Local");
        this.frame.add(saveImageToLocal, BorderLayout.PAGE_END);

        this.frame.add(buttonPanel, BorderLayout.SOUTH);
        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        clientListModel = new DefaultListModel<>();
        clientJList = new JList<>(clientListModel);
        JScrollPane clientScrollPane = new JScrollPane(clientJList);
        clientScrollPane.setPreferredSize(new Dimension(250, 80));
        this.frame.add(clientScrollPane, BorderLayout.EAST);

//        this.clientManagePanel = new JPanel();
//        this.clientList = new ArrayList<String>();
//        JList<String> clientJList = new JList<String>(clientList.toArray(new String[clientList.size()]));
//        clientJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        clientJList.setLayoutOrientation(JList.VERTICAL);
//        clientJList.setVisibleRowCount(-1);
//        JScrollPane clientJScrollPane = new JScrollPane(clientJList);
//        clientJScrollPane.setPreferredSize(new Dimension(250, 80));
//        this.clientManagePanel.add(clientJScrollPane);
//        this.getFrame().add(this.clientManagePanel, BorderLayout.WEST);


//        clientJList.addListSelectionListener(new ListSelectionListener() {
//            public void valueChanged(ListSelectionEvent e) {
//                if (e.getValueIsAdjusting() == false) {
//                    String selectedClient = clientJList.getSelectedValue();
//                    if (selectedClient != null) {
//                        System.out.println("Selected client: " + selectedClient);
//                        try {
//                            JSONObject jsonObject = new JSONObject();
//                            jsonObject.put("username", selectedClient);
//                            jsonObject.put("type", "kickout");
//                            jsonObject.put("action", "remove");
//                            getOut().writeUTF("kickout");
//                        } catch (IOException ex) {
//                            throw new RuntimeException(ex);
//                        }
//
//                    }
//
//
//                }
//            }
//        });

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

        saveImageButton.addActionListener(e -> {
            try {
                JFrame saveImageToMongoDBFrame = new JFrame();
                saveImageToMongoDBFrame.setTitle("Save Image to MongoDB");
                JTextField imageTitleField = new JTextField();
                imageTitleField.setPreferredSize(new Dimension(200, 24));
                JButton saveButton = new JButton("Save");
                saveImageToMongoDBFrame.add(imageTitleField, BorderLayout.NORTH);
                saveImageToMongoDBFrame.add(saveButton, BorderLayout.SOUTH);
                saveImageToMongoDBFrame.pack();
                saveImageToMongoDBFrame.setVisible(true);
                saveButton.addActionListener(e1 -> {
                    try {
                        String imageTitle = imageTitleField.getText();
                        this.saveImageToMongoDB(imageTitle);
                        saveImageToMongoDBFrame.setVisible(false);
                        saveImageToMongoDBFrame.dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadImageButton.addActionListener(e -> {
            try {
                JFrame loadImageButtonFrame = new JFrame();
                loadImageButtonFrame.setTitle("Load Image from MongoDB");
                JTextField imageTitleField = new JTextField();
                imageTitleField.setPreferredSize(new Dimension(200, 24));
                JButton loadButton = new JButton("Load");
                loadImageButtonFrame.add(imageTitleField, BorderLayout.NORTH);
                loadImageButtonFrame.add(loadButton, BorderLayout.SOUTH);
                loadImageButtonFrame.pack();
                loadImageButtonFrame.setVisible(true);
                loadButton.addActionListener(e1 -> {
                    try {
                        String imageName = imageTitleField.getText();
                        this.loadImageFromMongoDB(imageName);
                        loadImageButtonFrame.setVisible(false);
                        loadImageButtonFrame.dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        saveImageToLocal.addActionListener(e ->{
            try {
                this.saveImageToLocal();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        clientJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    if (clientJList.getSelectedValue() != null) {
                        if (e.getClickCount() == 2) {
                            String selectedClientName = clientJList.getSelectedValue();
                            JSONObject selectedClientJsonObject = new JSONObject();
                            selectedClientJsonObject.put("type", "kickout");
                            selectedClientJsonObject.put("action", "remove");
                            selectedClientJsonObject.put("username", selectedClientName);
                            out.writeUTF(selectedClientJsonObject.toString());
                            managerWhiteBoardPanel.removeClient(selectedClientName);
                        }
                    }
                } catch (IOException event){
                    event.printStackTrace();
                }
            }
        });
    }

    public void setColor(Color color) throws RemoteException {
        this.managerWhiteBoardPanel .setColor(color);
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

    public void saveImageToMongoDB(String imageName) throws IOException {
        Image image = managerWhiteBoardPanel.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "save");
        jsonObject.put("action", "add");
        jsonObject.put("image", base64Image);
        jsonObject.put("username", this.username);
        jsonObject.put("time", new Date().toString());
        jsonObject.put("imageName", imageName);
        getOut().writeUTF(jsonObject.toString());
    }

    public void loadImageFromMongoDB(String imageName) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "load");
        jsonObject.put("action", "add");
        jsonObject.put("username", this.username);
        jsonObject.put("imageName", imageName);
        getOut().writeUTF(jsonObject.toString());
    }

    public void saveImageToLocal(){
        this.managerWhiteBoardPanel.saveImage();
    }

    public DefaultListModel<String> getClientListModel(){
        return this.clientListModel;
    }


}


