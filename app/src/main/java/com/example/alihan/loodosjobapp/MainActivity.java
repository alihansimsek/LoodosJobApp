package com.example.alihan.loodosjobapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button searchButton;
    private ProgressBar spinner;
    private ArrayList<MovieModel> movies;
    private ListView movieList;
    ArrayAdapter<MovieModel> movieAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.progressBar);
        movieList = findViewById(R.id.listView1);
        searchButton = findViewById(R.id.searchButton);
        searchBar = findViewById(R.id.searchBar);
        spinner.setVisibility(View.GONE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new omdbConnector().execute("http://www.omdbapi.com/?s=" + searchBar.getText() + "&apikey=bf2b2d50");

                Bundle bundle = new Bundle();                                       //firebase search log
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Search Button");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, searchBar.getText().toString());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);

                searchBar.setText("");
            }
        });
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = movieList.getItemAtPosition(position);
                MovieModel movie = (MovieModel) o;
                Intent intent = new Intent(getBaseContext(), MovieDetails.class);
                intent.putExtra("movieID", movie.getId());

                Bundle bundle = new Bundle();                                       //firebase movie click log
                bundle.putString(FirebaseAnalytics.Param.METHOD, "List View");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getName());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                startActivity(intent);
            }
        });

    }

    class omdbConnector extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            String answer;

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((answer = bufferedReader.readLine()) != null) {
                        stringBuilder.append(answer);
                    }
                    bufferedReader.close();
                    return new JSONObject(stringBuilder.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            super.onPostExecute(result);

            spinner.setVisibility(View.GONE);

            if (result != null) {
                try {
                    String status = result.getString("Response");

                    if (status.equals("True")) {
                        JSONArray jarr = result.getJSONArray("Search");

                        movies = new ArrayList<>();
                        for (int x = 0; x < jarr.length(); x++) {
                            movies.add(new MovieModel(jarr.getJSONObject(x).getString("Title"),
                                    jarr.getJSONObject(x).getString("imdbID")));
                        }
                        movieAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, movies);
                        movieList.setAdapter(movieAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "We couldn't find a movie with that name", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
            }
        }

    }

}


