package ro.pub.cs.systems.eim.practicaltest02var03;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by student on 19.05.2017.
 */

public class ClientThread extends  Thread {

    private String address;
    private int port;
    private String word;
    private TextView defView;


    private Socket socket;
    public ClientThread(String address, int port, String word, TextView defView) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.defView = defView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println(word);
            printWriter.flush();

            String definition;
            while ((definition = bufferedReader.readLine()) != null) {
                final String finalDefinition = definition;
                defView.post(new Runnable() {
                    @Override
                    public void run() {
                        defView.setText(finalDefinition);
                    }
                });
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
