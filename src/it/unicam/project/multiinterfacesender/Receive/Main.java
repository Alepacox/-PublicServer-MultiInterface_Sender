
package it.unicam.project.multiinterfacesender.Receive;



import java.net.URI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.json.simple.JSONObject;
import it.unicam.project.multiinterfacesender.Receive.*;


import jdk.incubator.http.HttpClient;
import jdk.incubator.http.WebSocket;
import org.json.simple.JSONValue;

public class Main {
   /* private static JTextPane chatText;
    private static JScrollPane chatTextPane;
    private static JFrame mainFrame;*/

    private static Connection_Manager managerSRV;

    public static void main(String[] args) {
        //initGUI();
        new Thread(managerSRV = new Connection_Manager()).start();

        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<WebSocket> ws = client.newWebSocketBuilder()
                            .buildAsync(URI.create("ws://0.0.0.0:3000"), new WebSocketListener());

    }
    public static class WebSocketListener implements WebSocket.Listener {

        public void onOpen(WebSocket webSocket)
        {
                System.out.println("connessione aperta al server node");
                JSONObject obj = new JSONObject();
                obj.put("java_server",true);
                webSocket.sendText(obj.toJSONString(),true);
        }
        @Override
        public CompletionStage<?> onText(WebSocket webSocket,
                                         CharSequence message, WebSocket.MessagePart part){

            Object obj= JSONValue.parse(message.toString());
            JSONObject a=(JSONObject)obj;
            if(a.get("disattiva")!=null)
            {
                String dtoken = (String)a.get("disattiva");
                Connection r= managerSRV.getConnectionByDToken(dtoken);
                r.dtoken_controparte = null;
                r = managerSRV.getControparteConnectionByDToken(dtoken);
                r.dtoken_controparte = null;
            }
            else
            {
                String ricevente = (String)a.get("ricevente");
                System.out.println("ricevente="+ricevente);

                String mittente = (String)a.get("mittente");
                System.out.println("mittente="+mittente);
                Connection r= managerSRV.getConnectionByDToken(ricevente);
                System.out.println("controparte="+r.dtoken);
                r.dtoken_controparte = mittente;
            }

            // Reuqesting next message
            webSocket.request(1);

            // Print the message when it's available
           // return CompletableFuture.completedFuture(message)
                  //  .thenAccept(System.out::println);
            return null;
        }
    }
    private static void initGUI() {

       /* chatText = new JTextPane();
        chatText.setMargin(new Insets(5, 5, 5, 5));
        chatText.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        chatText.setEditable(false);
        chatText.setBackground(Color.darkGray);

        chatTextPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel logPane = new JPanel(new BorderLayout());
        logPane.add(chatTextPane, BorderLayout.CENTER);
        logPane.setPreferredSize(new Dimension(640, 480));

        mainFrame = new JFrame("DEV-ServerMIFT 1.02a");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(mainFrame,
                        "Are you sure to close this window?", "QUIT?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    DisconnectEverything();
                    System.exit(0);
                }
            }
        });
        JMenuItem jMenuItem = new JMenuItem("Clear");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(e -> chatText.setText(""));
        JMenu jMenu = new JMenu("Log");
        jMenu.add(jMenuItem);
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(jMenu);
        mainFrame.setJMenuBar(jMenuBar);
        mainFrame.setContentPane(logPane);

        mainFrame.setSize(mainFrame.getPreferredSize());
        mainFrame.pack();
        mainFrame.setVisible(true);*/
    }

   // static void AppendLog(String message, Color color) {
      /*  StyledDocument doc = chatText.getStyledDocument();

        Style style = chatText.addStyle("I'm a Style", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), "\n[" + new Date().toString() + "]:\n\t" + message, style);
        } catch (BadLocationException e) {
            //System.out.println(e.getMessage());
        }
        //mainFrame.revalidate(); //Update the scrollbar size
        try {
            JScrollBar vertical = chatTextPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
   // }

    static void sendMessageToClients(Message d, String dtoken) {
        managerSRV.sendMessageToClients(d, dtoken);
    }

    static void removeClient(Connection c) {
        managerSRV.removeClient(c);
    }

    private static void DisconnectEverything() {
        managerSRV.Terminate();
    }
}
