package com.example.caei;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.caei.Class.User;
import com.example.caei.Class.UserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ChatPrive extends AppCompatActivity {

    private ListView listView;
    private ArrayList<User> users;
    private UserAdapter adapter;
    private RequestQueue requestQueue;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_prive);

        // Initialize ListView
        listView = findViewById(R.id.user_list);
        searchView = findViewById(R.id.searchView);
        users = new ArrayList<>();
        adapter = new UserAdapter(this, users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = users.get(position);
            Intent intent = new Intent(ChatPrive.this, Messages.class);
            intent.putExtra("userId", 1); // Replace with actual userId
            intent.putExtra("selectedUserId", selectedUser.getId());
            intent.putExtra("nom", selectedUser.getFirstName());
            startActivity(intent);
        });

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Load all users initially
        loadUsers("");

        // Set up the SearchView to filter users based on the query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadUsers(newText);
                return true;
            }
        });
    }

    private void loadUsers(String query) {
        String url = "http://192.168.1.114:8080/Stage/test/get_user.php?query=" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    users.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id_user");
                            String name = jsonObject.getString("nom_user");
                            String lastname = jsonObject.getString("prenom_user");
                            String imageUrl = jsonObject.getString("img_user");
                            users.add(new User(id, name, lastname, imageUrl));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.d("errer", error.toString());
                    Toast.makeText(ChatPrive.this, "Error loading users: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}
