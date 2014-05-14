import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by Dustin on 5/14/14.
 */
public class Main extends JFrame {
    private String ipAddress;
    private SwingWorker worker;
    private Socket connection;
    private boolean connected;
    private JLabel status;
    private ObjectOutputStream outputStream;
    public Main(){
        super("Control Pad");
        connected = false;
        worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                loop();
                return null;
            }
        };
    }
    public void createGUI(){
        setSize(new Dimension(75,100));
        status = new JLabel((connected)?"Connected":"Not Connected");
        add(status);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (connected){
                    try {
                        outputStream.writeObject(e.getKeyCode());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void loop() {
        try {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        ipAddress = JOptionPane.showInputDialog(getComponent(0), "IP address?");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            while(!connected){
                try{
                    connection = new Socket(ipAddress,5050);
                    connected = true;
                }
                catch (ConnectException e){
                    connected = false;
                }
            }
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(getComponent(0),"Connected!");
                    if (status != null){
                        status.setText("Connected");
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        final Main main = new Main();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                main.createGUI();
                main.setVisible(true);
            }
        });
        main.startWorker();
    }

    private void startWorker() {
        worker.execute();
    }

}
