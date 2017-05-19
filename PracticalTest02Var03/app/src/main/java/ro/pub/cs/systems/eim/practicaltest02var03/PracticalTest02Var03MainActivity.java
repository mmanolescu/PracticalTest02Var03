package ro.pub.cs.systems.eim.practicaltest02var03;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02Var03MainActivity extends AppCompatActivity {

    private Button serverConnectButton;
    private Button clientConnectButton;

    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText wordEditText;

    private TextView resultTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;

    private ServerConnectButtonListener serverConnectButtonListener = new ServerConnectButtonListener();
    private class ServerConnectButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private ClientConnectButtonListener clientConnectButtonListener = new ClientConnectButtonListener();
    private class ClientConnectButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
                                    }
                                    if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String data = wordEditText.getText().toString();

            resultTextView.setText("");

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), wordEditText.getText().toString(), resultTextView);

            clientThread.start();



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_var03_main);

        serverConnectButton = (Button)findViewById(R.id.server_connect_button);
        serverConnectButton.setOnClickListener(serverConnectButtonListener);
        clientConnectButton = (Button)findViewById(R.id.client_connect_Button);
        clientConnectButton.setOnClickListener(clientConnectButtonListener);


        serverPortEditText = (EditText)findViewById(R.id.server_port_edittext);
        clientAddressEditText = (EditText)findViewById(R.id.client_address_editText);
        clientPortEditText = (EditText)findViewById(R.id.client_port_editText);
        wordEditText = (EditText)findViewById(R.id.word_editText);
        resultTextView = (TextView)findViewById(R.id.result_textView);
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }



}
