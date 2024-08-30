package com.example.caei;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.caei.Class.Group;
import com.example.caei.Class.GroupAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ChatGroupe extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Group> groups;
    private GroupAdapter adapter;
    private RequestQueue requestQueue;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groupe);

        // Initialize ListView and SearchView
        listView = findViewById(R.id.group_list);
        searchView = findViewById(R.id.searchView);
        groups = new ArrayList<>();
        adapter = new GroupAdapter(this, groups);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Group selectedGroup = groups.get(position);
            Intent intent = new Intent(ChatGroupe.this, MessageGRP.class);
            intent.putExtra("senderId", -1); // Pass userId to MessageActivity
            intent.putExtra("groupId", selectedGroup.getId()); // Pass userId to MessageActivity
            startActivity(intent);
        });

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Load groups initially
        loadGroups("");

        // Set up the SearchView to filter groups based on the query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadGroups(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadGroups(newText);
                return true;
            }
        });
    }

    private void loadGroups(String query) {
        String url = "http://192.168.1.114:8080/Stage/test/load_groupe.php?query=" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    groups.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id_groupe");
                            String name = jsonObject.getString("nom_groupes");
                            String imageUrl = jsonObject.getString("img_groupe");
                            groups.add(new Group(id, imageUrl, name));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.d("errer", error.toString());
                    Toast.makeText(ChatGroupe.this, "Error loading groups: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}
