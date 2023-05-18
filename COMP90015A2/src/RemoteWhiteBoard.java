import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteWhiteBoard{

    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    private JFrame frame;
    private WhiteBoardPanel whiteBoardPanel;

    public RemoteWhiteBoard(Socket socket, DataInputStream in, DataOutputStream out) throws RemoteException {

        this.socket = socket;
        this.in = in;
        this.out = out;

        this.frame = new JFrame("Whiteboard");
        whiteBoardPanel = new WhiteBoardPanel(this.in, this.out);
        this.frame.add(whiteBoardPanel);
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



        this.frame.add(buttonPanel, BorderLayout.SOUTH);
        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        clearButton.addActionListener(e -> {
            try {
                whiteBoardPanel.clearImage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        redButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setColor(Color.RED);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        blueButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setColor(Color.BLUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        greenButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setColor(Color.GREEN);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        eraseButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setColor(Color.WHITE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        confirmFillOvalSizeButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setFillSize(Integer.parseInt(fillOvalSizeField.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillOvalButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setShape("Oval");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillRectangleButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setShape("Rectangle");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillTriangleButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setShape("Triangle");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillSquareButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setShape("Square");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fillStarButton.addActionListener(e -> {
            try {
                whiteBoardPanel.setShape("Star");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    public void setColor(Color color) throws RemoteException {
        this.whiteBoardPanel .setColor(color);
    }


    public void draw(int x, int y) throws RemoteException {
        this.whiteBoardPanel .draw(x, y);
    }


    public void clearImage() throws RemoteException {
        this.whiteBoardPanel .clearImage();
    }


    public void erase(int x, int y) throws RemoteException {
        this.whiteBoardPanel .erase(x, y);
    }


    public void setFillSize(int fillOvalSize) throws RemoteException {
        this.whiteBoardPanel .setFillSize(fillOvalSize);
    }


    public void displayFrame() {
        this.frame.setVisible(true);
    }




}
