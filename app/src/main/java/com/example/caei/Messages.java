package com.example.caei;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.caei.Class.Message;
import com.example.caei.Class.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Messages extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private int userId;
    private int selectedUserId; // ID of the selected user
    private EditText editTextMessage;
    private Button buttonSend;
    private String Nom;
    private CardView c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Récupérer l'ID de l'utilisateur courant et de l'utilisateur sélectionné
        userId = getIntent().getIntExtra("userId", -1);
        selectedUserId = getIntent().getIntExtra("selectedUserId", -1);
        Nom = getIntent().getStringExtra("nom"); // Get the name of the user

        // Initialiser RecyclerView
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages, userId); // Pass userId as currentUserId
        recyclerViewMessages.setAdapter(messageAdapter);

        // Initialiser EditText et Button pour l'envoi de message
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Charger les messages
        loadMessages();

        // Configurer le bouton pour envoyer le message
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(userId, selectedUserId, message);
                } else {
                    Toast.makeText(Messages.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMessages() {
        String url = "http://192.168.1.114:8080/Stage/test/get_mess.php?sender=" + 1 + "&recipient=" + selectedUserId;
        RequestQueue requestQueue = Volley.newRequestQueue(this); // Initialize requestQueue

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString());
                        messages.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String messageText = jsonObject.getString("message");
                                String senderName = jsonObject.getString("sender");
                                String recipientName = jsonObject.getString("recipient");;
                                String timestamp = jsonObject.getString("timestamp");
                                String jour = jsonObject.getString("jour");

                                // Create a new Message object
                                Message message = new Message(userId, senderName,messageText, timestamp, jour);
                                messages.add(message);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Messages.this, "Error parsing messages", Toast.LENGTH_SHORT).show();
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Messages.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void sendMessage(int senderId, int recipientId, String message) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://192.168.1.114:8080/Stage/test/send_msg.php"); // Change to your URL
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                // Create JSON object
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", message);
                jsonObject.put("sender", senderId);
                jsonObject.put("recipient", recipientId);

                // Send JSON data
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Check response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(Messages.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        editTextMessage.setText(""); // Clear the message input
                        loadMessages(); // Reload messages to show the new message
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Messages.this, "Error sending message", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Messages.this, "Error sending message", Toast.LENGTH_SHORT).show());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }).start();
    }
}
