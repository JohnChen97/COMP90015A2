import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ManagerRemoteWhiteBoard extends RemoteWhiteBoard {
    private JPanel clientManagePanel;
    private String username;
    private ArrayList<String> clientList;
    private boolean manager;



    public ManagerRemoteWhiteBoard(Socket socket, DataInputStream in, DataOutputStream out, String username, boolean manager) throws RemoteException {
        super(socket, in, out, username, manager);


        this.clientManagePanel = new JPanel();
        this.clientList = new ArrayList<String>();
        JList<String> clientJList = new JList<String>(clientList.toArray(new String[clientList.size()]));
        clientJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientJList.setLayoutOrientation(JList.VERTICAL);
        clientJList.setVisibleRowCount(-1);
        JScrollPane clientJScrollPane = new JScrollPane(clientJList);
        clientJScrollPane.setPreferredSize(new Dimension(250, 80));
        this.clientManagePanel.add(clientJScrollPane);
        this.getFrame().add(this.clientManagePanel, BorderLayout.EAST);


        clientJList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    String selectedClient = clientJList.getSelectedValue();
                    if (selectedClient != null) {
                        System.out.println("Selected client: " + selectedClient);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("username", selectedClient);
                            jsonObject.put("type", "kickout");
                            jsonObject.put("action", "remove");
                            getOut().writeUTF("kickout");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }


                }
            }
        });
    }
        public void receivingNewClientName() {
            new Thread(() -> {
                while (true) {
                    try {
                        if (this.getIn().available() > 0) {
                            String jsonString = this.getIn().readUTF();

                            JSONObject jsonObject = new JSONObject(jsonString);
                            if (jsonObject.has("type")){
                                if (jsonObject.getString("type").equals("addClient")) {
                                    System.out.println("New client: " + jsonObject.getString("username"));
                                    String newClientName = jsonObject.getString("username");
                                    this.clientList.add(newClientName);
                                    System.out.println("New client: " + newClientName);
                                    this.clientManagePanel.repaint();
                                }
                            }
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


