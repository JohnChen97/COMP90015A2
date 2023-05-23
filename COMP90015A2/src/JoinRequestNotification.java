import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class JoinRequestNotification {
    private JFrame frame;
    private JLabel notificationLabel;
    private JButton acceptButton;
    private JButton rejectButton;
    private String username;
    private DataOutputStream out;
    private DataInputStream in;

    private DefaultListModel<String> clientListModel;

    public JoinRequestNotification(String username, DataOutputStream out, DataInputStream in, DefaultListModel<String> clientListModel) {
        this.username = username;
        this.out = out;
        this.in = in;
        this.clientListModel = clientListModel;

        this.frame = new JFrame("Join Request");
        this.frame.setSize(300, 200);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new FlowLayout());
        this.notificationLabel = new JLabel(username + " wants to join your whiteboard");
        this.frame.add(this.notificationLabel);
        this.acceptButton = new JButton("Accept");
        this.rejectButton = new JButton("Reject");
        this.frame.add(this.acceptButton);
        this.frame.add(this.rejectButton);
        this.frame.setVisible(true);

        acceptButton.addActionListener(e -> {
            try {
                //this.clientListModel.addElement(username);
                JSONObject joinRequest = new JSONObject();
                joinRequest.put("type", "permit");
                joinRequest.put("status", true);
                joinRequest.put("action", "add");
                joinRequest.put("username", username);
                String [] usernameList = new String[clientListModel.size()];
                for (int i = 0; i < clientListModel.size(); i++) {usernameList[i] = clientListModel.get(i);}
                joinRequest.put("usernameList", usernameList);
                out.writeUTF(joinRequest.toString());
                this.frame.dispose();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        rejectButton.addActionListener(e -> {
            try {
                JSONObject joinRequest = new JSONObject();
                joinRequest.put("type", "permit");
                joinRequest.put("status", false);
                joinRequest.put("action", "add");
                joinRequest.put("username", username);
                out.writeUTF(joinRequest.toString());
                out.flush();
                this.frame.dispose();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });


    }
}

