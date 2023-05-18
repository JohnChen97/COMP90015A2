import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WhiteBoardPanel extends JPanel{

    private DataInputStream in;
    private DataOutputStream out;

    private Graphics2D graphics2D;
    private Image image;;
    private int currentX, currentY, oldX, oldY;

    private int fillSize  = 5;
    private String shape = "Oval";

    private Color currentColor = Color.BLACK;

    private ArrayList<String> tempMouseMotionList= new ArrayList<String>();

// https://www.youtube.com/watch?v=OOb1eil4PCo&t=435s
// Using code from the above whiteboard tutorial.

    public WhiteBoardPanel(DataInputStream in, DataOutputStream out) throws RemoteException {
        this.in = in;
        this.out = out;

        this.setSize(800, 600);
        setDoubleBuffered(false);
        this.listeningToMessageFromServer();



        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (graphics2D != null){

                    currentX = e.getX();
                    currentY = e.getY();
                    String shapeRecordJsonString = fillShape(shape, currentX - fillSize / 2, currentY - fillSize / 2, fillSize, fillSize);
                    tempMouseMotionList.add(shapeRecordJsonString);
                    repaint();
                    oldX = currentX;
                    oldY = currentY;

                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Clear the temporary list when mouse is pressed
                tempMouseMotionList.clear();
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


    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (this.image == null){
            this.image = createImage(800, 600);
            this.graphics2D = (Graphics2D) this.image.getGraphics();
            this.graphics2D.setColor(Color.WHITE);
            this.graphics2D.fillRect(0, 0, 800, 600);
            this.graphics2D.setColor(Color.BLACK);
            clearImage();
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void clearImage(){
        this.graphics2D.setPaint(Color.WHITE);
        this.graphics2D.fillRect(0, 0, 800, 600);
        this.graphics2D.setPaint(Color.BLACK);
        this.repaint();
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

    public void processReceivedMessage(String jsonString){
        try{

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
                    // ignore this message
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


}

