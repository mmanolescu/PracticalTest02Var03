package ro.pub.cs.systems.eim.practicaltest02var03;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.HashMap;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by student on 19.05.2017.
 */

public class CommunicationThread extends Thread {

    public ServerThread serverThread;
    public Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word)!");
            String word = bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word)!");
                return;
            }

            HashMap<String, String> data = serverThread.getData();
            String wordDefinition;

            if (data.containsKey(word)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                wordDefinition = data.get(word);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + word);

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageCode = httpClient.execute(httpGet, responseHandler);

                if (pageCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(pageCode));
                    Document doc = dbBuilder.parse(is);

                    NodeList defs = doc.getElementsByTagName(Constants.WORD_DEFINITION);

                    Log.e(Constants.TAG, defs.item(0).toString());
                    serverThread.setData(word, defs.item(0).toString());

                } catch (ParserConfigurationException ex) {

                } catch (SAXException saxex) {

                }

            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }


    }
}
