package com.example.caei;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.caei.Class.MessageGroup;
import com.example.caei.Class.MessageAdapter;
import com.example.caei.Class.MessageGroupAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MessageGRP extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageGroupAdapter messageGroupAdapter;
    private ArrayList<MessageGroup> messages;
    private int userId;
    private int selectedUserId; // ID of the selected user
    private EditText editTextMessage;
    private Button buttonSend;
    private String Nom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Get current user ID and selected user ID
        userId = getIntent().getIntExtra("senderId", -1);
        selectedUserId = getIntent().getIntExtra("groupId", -1);
        Nom = getIntent().getStringExtra("nom"); // Get the name of the user

        // Initialize RecyclerView
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        messageGroupAdapter = new MessageGroupAdapter(this, messages, userId); // Use MessageGroupAdapter
        recyclerViewMessages.setAdapter(messageGroupAdapter);

        // Initialize EditText and Button for sending messages
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Load messages
        loadMessages();

        // Set up button to send message
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(userId, selectedUserId, message);
                } else {
                    Toast.makeText(MessageGRP.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMessages() {
        String url = "http://192.168.1.114:8080/Stage/test/get_group_messages.php?a=" + selectedUserId + "&b=" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString()); // Log the JSON response for debugging

                        messages.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int senderId = jsonObject.getInt("id_sender");
                                String timestamp = jsonObject.getString("timestamp");
                                String jour = jsonObject.getString("jour");
                                String messageText = jsonObject.getString("message");

                                // Create a new MessageGroup object with the retrieved data
                                MessageGroup messageGroup = new MessageGroup( senderId, timestamp, jour, messageText);
                                messages.add(messageGroup);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MessageGRP.this, "Error parsing messages", Toast.LENGTH_SHORT).show();
                            }
                        }
                        messageGroupAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageGRP.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
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
                        Toast.makeText(MessageGRP.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        editTextMessage.setText(""); // Clear the message input
                        loadMessages(); // Reload messages to show the new message
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MessageGRP.this, "Failed to send message", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MessageGRP.this, "Error in sending message", Toast.LENGTH_SHORT).show());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }).start();
    }
}
