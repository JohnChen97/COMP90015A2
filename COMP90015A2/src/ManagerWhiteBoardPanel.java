import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

public class ManagerWhiteBoardPanel extends JPanel{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ManagerRemoteWhiteBoard managerRemoteWhiteBoard;

    private Graphics2D graphics2D;
    private Image image;;
    private int imageWidth = 800;
    private int imageHeight = 600;
    private int currentX, currentY, oldX, oldY;

    private int fillSize  = 5;
    private String shape = "Oval";

    private Color currentColor = Color.BLACK;
    private int fontSize = 30;
    private String fontType = "Arial";

    private boolean ShapeOrText = true; // true = shape, false = text

    private ArrayList<String> tempMouseMotionList= new ArrayList<String>();
    TextRecord currentTextRecord = new TextRecord( 0, 0, 12, "Arial", "");

    private JPanel clientManagePanel;
    private String username;
//    private DefaultListModel<String> clientListModel;
//    private JList<String> clientJList;



// https://www.youtube.com/watch?v=OOb1eil4PCo&t=435s
// Using code from the above whiteboard tutorial.

    public ManagerWhiteBoardPanel(Socket socket, DataInputStream in, DataOutputStream out, ManagerRemoteWhiteBoard managerRemoteWhiteBoard) throws RemoteException {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.managerRemoteWhiteBoard = managerRemoteWhiteBoard;

        this.setSize(1000, 1000);
        setDoubleBuffered(false);
        this.listeningToMessageFromServer();



//        this.clientManagePanel = new JPanel();
//        clientListModel = new DefaultListModel<>();
//        clientJList = new JList<>(clientListModel);
//        clientJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        clientJList.setLayoutOrientation(JList.VERTICAL);
//        clientJList.setVisibleRowCount(-1);

//        clientListModel = new DefaultListModel<>();
//        clientJList = new JList<>(clientListModel);
//        JScrollPane clientScrollPane = new JScrollPane(clientJList);
//        clientScrollPane.setPreferredSize(new Dimension(250, 80));
//        add(clientScrollPane, BorderLayout.EAST);



        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (ShapeOrText) {
                    if (graphics2D != null) {

                        currentX = e.getX();
                        currentY = e.getY();
                        String shapeRecordJsonString = fillShape(shape, currentX - fillSize / 2, currentY - fillSize / 2, fillSize, fillSize);
                        tempMouseMotionList.add(shapeRecordJsonString);
                        repaint();
                        oldX = currentX;
                        oldY = currentY;

                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Clear the temporary list when mouse is pressed

                if (ShapeOrText) {
                    tempMouseMotionList.clear();
                    currentX = e.getX();
                    currentY = e.getY();
                    String shapeRecordJsonString = fillShape(shape, currentX - fillSize / 2, currentY - fillSize / 2, fillSize, fillSize);
                    tempMouseMotionList.add(shapeRecordJsonString);
                    repaint();
                } else {

                    JTextField textField = new JTextField(20);
                    textField.setBounds(e.getX(), e.getY(), 200, 20);
                    add(textField);
                    textField.requestFocus();

                    textField.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent ke) {

                            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {

                                currentTextRecord =  new TextRecord(e.getX() , e.getY(), fontSize, fontType, textField.getText());
                                JSONObject textJsonObject = currentTextRecord.toJsonObject();
                                textJsonObject.put("type", "text");
                                textJsonObject.put("action", "add");
                                String textJsonString = textJsonObject.toString();
                                try {
                                    out.writeUTF(textJsonString);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                remove(textField);

                                repaint();

                            }
                        }
                    });

                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Add all shapes in the temporary list to the main list when mouse is released
                try {
                    JSONObject tempMouseMotionListJsonObject = new JSONObject();
                    tempMouseMotionListJsonObject.put("data", tempMouseMotionList);
                    String tempMouseMotionListJsonString = tempMouseMotionListJsonObject.toString();
                    out.writeUTF(tempMouseMotionListJsonString);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });



//        clientJList.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                try{
//                    if (clientJList.getSelectedValue() != null) {
//                        if (e.getClickCount() == 2) {
//                            String selectedClientName = clientJList.getSelectedValue();
//                            JSONObject selectedClientJsonObject = new JSONObject();
//                            selectedClientJsonObject.put("type", "kickout");
//                            selectedClientJsonObject.put("action", "remove");
//                            selectedClientJsonObject.put("username", selectedClientName);
//                            out.writeUTF(selectedClientJsonObject.toString());
//                            removeClient(selectedClientName);
//                        }
//                    }
//                } catch (IOException event){
//                    event.printStackTrace();
//                }
//            }
//        });




    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (this.image == null){
            this.image = createImage(imageWidth, imageHeight);
            this.graphics2D = (Graphics2D) this.image.getGraphics();
            this.graphics2D.setColor(Color.WHITE);
            this.graphics2D.fillRect(0, 0, imageWidth, imageHeight);
            this.graphics2D.setColor(Color.BLACK);
            clearImage();
        }

        g.drawImage(this.image, 0, 0, this);
        currentTextRecord.draw(graphics2D);



    }

    public void clearImage(){
        this.graphics2D.setPaint(Color.WHITE);
        this.graphics2D.fillRect(0, 0, 800, 600);
        this.graphics2D.setPaint(Color.BLACK);
        this.repaint();
    }

    public void setShapeOrText(boolean shapeOrText){
        this.ShapeOrText = shapeOrText;
    }


    public void draw(int x, int y) {
        if (this.graphics2D != null) {
            this.fillShape(this.shape, x, y, this.fillSize, this.fillSize);
            this.repaint();
        }
    }

    public void setColor(Color color) throws RemoteException {
        this.graphics2D.setPaint(color);
    }


    public void erase(int x, int y) throws RemoteException {
        if (this.graphics2D != null) {
            this.graphics2D.setPaint(Color.WHITE);
            this.graphics2D.fillOval(x, y, this.fillSize, this.fillSize);
            //this.repaint(x, y, this.fillOvalSize, this.fillOvalSize);
            this.repaint();
            this.graphics2D.setPaint(Color.BLACK);
        }
    }

    public void setFillSize(int size){
        this.fillSize = size;
    }

    public String fillShape(String shape, int x, int y, int oldX, int oldY){
        if (shape.equals("Rectangle")){
            try {
                this.graphics2D.fillRect(x, y, oldX, oldY);
                ShapeRecord shapeRecord = new ShapeRecord(x, y, oldX, oldY, "Rectangle", this.graphics2D.getColor(), this.fillSize);
                String jsonString = this.getShapeRecordJsonString(shapeRecord);
                return jsonString;


            } catch (Exception e){
                System.out.println(e);
            }
        } else if (shape.equals("Oval")){
            try {
                this.graphics2D.fillOval(x, y, oldX, oldY);
                ShapeRecord shapeRecord = new ShapeRecord(x, y, oldX, oldY, "Oval", this.graphics2D.getColor(), this.fillSize);
                String jsonString = this.getShapeRecordJsonString(shapeRecord);
                return jsonString;

            } catch (Exception e){
                System.out.println(e);
            }
        } else if (shape.equals("Square")){
            this.graphics2D.fillRect(x, y, oldX, oldY);
            ShapeRecord shapeRecord = new ShapeRecord(x, y, oldX, oldY, "Square", this.graphics2D.getColor(), this.fillSize);
            String jsonString = this.getShapeRecordJsonString(shapeRecord);
            return jsonString;
        } else if (shape.equals("Triangle")){

            int[] xPoints = {x, x - oldX / 2, x + oldX / 2};
            int[] yPoints = {y, y + oldY, y + oldY};
            this.graphics2D.fillPolygon(xPoints, yPoints, 3);
            ShapeRecord shapeRecord = new ShapeRecord(x, y, oldX, oldY, "Triangle", this.graphics2D.getColor(), this.fillSize);
            String jsonString = this.getShapeRecordJsonString(shapeRecord);
            return jsonString;
        } else if (shape.equals("Star")){
            double radius = 50.0;
            double centerX = x;
            double centerY = y;


            double[] angles = new double[] {0.0, 4*Math.PI/5, 8*Math.PI/5, 2*Math.PI/5, 6*Math.PI/5, 0};

            int[] xPoints = new int[6];
            int[] yPoints = new int[6];

            for (int i = 0; i < 6; i++) {
                xPoints[i] = (int)(centerX + radius * Math.cos(angles[i]));
                yPoints[i] = (int)(centerY + radius * Math.sin(angles[i]));
            }

            Polygon star = new Polygon(xPoints, yPoints, 6);
            graphics2D.fillPolygon(star);
            ShapeRecord shapeRecord = new ShapeRecord(x, y, oldX, oldY, "Star", this.graphics2D.getColor(), this.fillSize);
            String jsonString = this.getShapeRecordJsonString(shapeRecord);
            return jsonString;
        }

        this.graphics2D.fillOval(x, y, oldX, oldY);
        ShapeRecord shapeRecord = new ShapeRecord(x, y, oldX, oldY, "Oval", this.graphics2D.getColor(), this.fillSize);
        String jsonString = this.getShapeRecordJsonString(shapeRecord);
        return jsonString;


    }
    public void setShape(String shape){
        this.shape = shape;
    }
    public void setFontSize(int size){
        this.fontSize = size;
    }
    public void setFontType(String fontType){
        this.fontType = fontType;
    }
    public String getShapeRecordJsonString(ShapeRecord shapeRecord) {
        try{
            JSONObject recordJsonObject = shapeRecord.toJsonObject();
            recordJsonObject.put("action", "add");
            recordJsonObject.put("type", "shape");

            return recordJsonObject.toString();
        } catch (Exception e){
            System.out.println("Something wrong in eventUpdateWord: " + e.toString());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", false);
            return jsonObject.toString();
        }

    }

    public void sendChatMessage(String chatMessage){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "add");
            jsonObject.put("type", "chat");
            jsonObject.put("chatMessage", chatMessage);
            this.out.writeUTF(jsonObject.toString());
        } catch (Exception e){
            System.out.println("Something wrong in sendChatMessage: " + e.toString());
        }
    }

    public void processReceivedMessage(String bigJsonString){
        try{

            JSONObject bigJsonObject = new JSONObject(bigJsonString);
            System.out.println("Received message: " + bigJsonString);
            if (bigJsonObject.has("data")) {

                JSONArray RecordStringList = bigJsonObject.getJSONArray("data");
                for (int i = 0; i < RecordStringList.length(); i++) {
                    String jsonString = RecordStringList.getString(i);
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.has("action")) {
                        String action = jsonObject.getString("action");
                        String type = jsonObject.getString("type");
                        if (type.equals("shape")) {
                            int x = jsonObject.getInt("x");
                            int y = jsonObject.getInt("y");
                            String shape = jsonObject.getString("shape");
                            int size = jsonObject.getInt("size");
                            this.fillShape(shape, x, y, size, size);
                            this.repaint();

                        } else if (type.equals("word")) {

                        } else if (type.equals("result")) {

                        }
                    } else {

                    }
                }
            } else {
                if (bigJsonObject.has("type")){
                    if (bigJsonObject.getString("type").equals("text")){
                        String text = bigJsonObject.getString("text");
                        int x = bigJsonObject.getInt("x");
                        int y = bigJsonObject.getInt("y");
                        int size = bigJsonObject.getInt("fontSize");
                        String fontType = bigJsonObject.getString("fontType");
                        TextRecord newTextRecord = new TextRecord(x, y, size, fontType, text);
                        newTextRecord.draw(this.graphics2D);
                        this.repaint();
                    } else if (bigJsonObject.getString("type").equals("chat")){
                        String chatMessage = bigJsonObject.getString("chatMessage");
                        Document doc = this.managerRemoteWhiteBoard.getChatHistoryPane().getDocument();
                        doc.insertString(doc.getLength(), chatMessage + "\n", null);

                    } else if (bigJsonObject.getString(("type")).equals("kickout")){


                    } else if (bigJsonObject.getString("type").equals("addClient")) {
                            System.out.println("New client: " + bigJsonObject.getString("username"));
                            String newClientName = bigJsonObject.getString("username");
                            JoinRequestNotification joinRequestNotification = new JoinRequestNotification(newClientName, this.out, this.in, this.managerRemoteWhiteBoard.getClientListModel());



                    } else if (bigJsonObject.getString("type").equals("load")){
                        try {
                            String image = bigJsonObject.getString("image");
                            byte[] imageData = Base64.getDecoder().decode(image);
                            InputStream inputStream = new ByteArrayInputStream(imageData);
                            BufferedImage bufferedImage = ImageIO.read(inputStream);
                            Image newImage = bufferedImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
                            this.setImage(newImage);
                            repaint();

                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e){
            System.out.println("Something wrong in processReceivedMessage: " + e.toString());


        }
    }

    public void listeningToMessageFromServer() {
        new Thread(() -> {
            while (true) {
                try {
                    if (this.in.available() > 0) {
                        String jsonString = this.in.readUTF();

                        System.out.println("Received message from server: " + jsonString);
                        this.processReceivedMessage(jsonString);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    System.out.println("Something wrong in listeningToMessageFromServer: " + e.toString());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public void addClient(String clientName) {
        this.managerRemoteWhiteBoard.getClientListModel().addElement(clientName);

    }
    public void removeClient(String clientName) {
        this.managerRemoteWhiteBoard.getClientListModel().removeElement(clientName);

    }

    public void setImage(Image image){
        this.image = image;
    }

    public Image getImage(){
        return this.image;
    }

    public void saveImage(){
        try{
            BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            this.paint(graphics2D);
            JFrame frame = new JFrame();
            JTextField textField = new JTextField();
            textField.setPreferredSize(new Dimension(200, 24));
            frame.setSize(300, 100);
            frame.setLayout(new FlowLayout());
            Button button = new Button("Save");
            frame.add(button);
            frame.add(textField);

            frame.setVisible(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String fileName = textField.getText();
                    try{
                        ImageIO.write(image, "png", new File(fileName + ".png"));
                        frame.setVisible(false);
                        frame.dispose();
                    } catch (Exception e1){
                        System.out.println("Something wrong in saveImage: " + e1.toString());
                        frame.setVisible(false);
                        frame.dispose();
                    }
                    frame.dispose();
                }
            });

        } catch (Exception e){
            System.out.println("Something wrong in saveImage: " + e.toString());
        }
    }


}

