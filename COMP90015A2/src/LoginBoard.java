import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class LoginBoard {
    private JFrame frame;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private Button loginButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private boolean validUser = false;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    private boolean manager;


    public LoginBoard(Socket socket, DataInputStream in, DataOutputStream out, boolean manager) {

        this.socket = socket;
        this.in = in;
        this.out = out;
        this.manager = manager;

        this.frame = new JFrame("Login");
        this.frame.setSize(300, 200);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new FlowLayout());

        this.usernameLabel = new JLabel("Username:");
        this.usernameField = new JTextField(10);
        this.passwordLabel = new JLabel("Password:");
        this.passwordField = new JPasswordField(10);
        this.loginButton = new Button("Login");
        this.usernameLabel.setLabelFor(this.usernameField);

        this.frame.add(this.usernameLabel);
        this.frame.add(this.usernameField);
        this.frame.add(this.passwordLabel);
        this.frame.add(this.passwordField);
        this.frame.add(this.loginButton);

        this.frame.setVisible(true);

        loginButton.addActionListener(e -> {
            try {



                        String username = usernameField.getText();
                        String password = passwordField.getText();
                        JSONObject UserPassword = new JSONObject();
                        UserPassword.put("username", username);
                        UserPassword.put("password", password);
                        UserPassword.put("type", "UsernamePassword");
                        UserPassword.put("action", "login");
                        out.writeUTF(UserPassword.toString());
                        out.flush();
                        String response = in.readUTF();
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.getBoolean("status")) {
                            validUser = true;
                            this.username = username;
                            this.frame.setVisible(false);
                            this.frame.dispose();
                            if (manager) {
                                System.out.println("Generate manager frame.");
                                generateManagerRemoteWhiteBoard();
                            } else {
                                System.out.println("Generate not manager frame.");
                                String permissionJsonString = in.readUTF();
                                JSONObject permissionJsonObject = new JSONObject(permissionJsonString);
                                System.out.println("Permission json string: " + permissionJsonString);
                                if (permissionJsonObject.has("type")) {
                                    if (permissionJsonObject.getString("type").equals("permit")) {
                                        Boolean status = permissionJsonObject.getBoolean("status");
                                        if (status) {
                                            generateRemoteWhiteBoard();
                                        } else {
                                            this.frame.setVisible(false);
                                            this.frame.dispose();
                                            JOptionPane.showMessageDialog(null, "You are not permitted to join the whiteboard.");

                                        }
                                    }
                                }
                            }

                        } else {
                            username = "";
                            password = "";
                            usernameField.setText("");
                            passwordField.setText("");
                            JOptionPane.showMessageDialog(null, "Please enter a valid username and password");
                        }


//


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public RemoteWhiteBoard generateRemoteWhiteBoard() {
        try {
            return new RemoteWhiteBoard(this.socket, this.in, this.out, this.username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ManagerRemoteWhiteBoard generateManagerRemoteWhiteBoard() {
        try {
            return new ManagerRemoteWhiteBoard(this.socket, this.in, this.out,this.username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return this.usernameField.getText();
    }

    public boolean getValidUser() {
        return this.validUser;
    }
}
