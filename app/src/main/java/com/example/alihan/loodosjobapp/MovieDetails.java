package com.example.alihan.loodosjobapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetails extends AppCompatActivity {

    TextView title, director, writer, plot, year, actors, runtime, rating, genre;
    String title_str, director_str, writer_str, plot_str, year_str, actors_str, runtime_str, rating_str, genre_str;
    ImageView poster;
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        String data = getIntent().getExtras().getString("movieID");
        title = findViewById(R.id.m_title);
        director = findViewById(R.id.m_director);
        writer = findViewById(R.id.m_writer);
        plot = findViewById(R.id.m_plot);
        year = findViewById(R.id.m_year);
        actors = findViewById(R.id.m_actors);
        runtime = findViewById(R.id.m_runtime);
        rating = findViewById(R.id.m_rating);
        genre = findViewById(R.id.m_genre);
        poster = findViewById(R.id.m_poster);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        new omdbMovie().execute("http://www.omdbapi.com/?i=" + data + "&apikey=bf2b2d50");
        }

    private class omdbMovie extends AsyncTask<String, Void, JSONObject> {

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
                    JSONObject obj = new JSONObject(stringBuilder.toString());

                    try {
                        String imageURL = obj.getString("Poster");
                        InputStream is = new URL(imageURL).openStream();
                        Bitmap logo = BitmapFactory.decodeStream(is);
                        poster.setImageBitmap(logo);
                    }
                    catch (Exception ex) {
                    }

                    return obj;

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Toast.makeText(MovieDetails.this, "Everything is something happened...", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(JSONObject result) {

            super.onPostExecute(result);

            if (result != null) {
                try {
                    String status = result.getString("Response");

                    if (status.equals("True")) {
                        title_str=result.getString("Title");
                        year_str=result.getString("Year");
                        runtime_str=result.getString("Runtime");
                        genre_str=result.getString("Genre");
                        director_str=result.getString("Director");
                        writer_str=result.getString("Writer");
                        actors_str=result.getString("Actors");
                        plot_str=result.getString("Plot");
                        rating_str=result.getString("imdbRating");

                        title.setText(title_str);
                        year.setText("Year: " + year_str);
                        runtime.setText("Runtime: " + runtime_str);
                        genre.setText("Genre: " + genre_str);
                        director.setText("Director: " + director_str);
                        writer.setText("Writer: " + writer_str);
                        actors.setText("Actors: " + actors_str);
                        plot.setText("Plot: " + plot_str);
                        rating.setText("IMDB Rating: " + rating_str);

                        Bundle params = new Bundle();               //firebase movie log
                        params.putString("movie_title", title_str);
                        params.putString("year", year_str);
                        params.putString("runtime", runtime_str);
                        params.putString("genre", genre_str);
                        params.putString("director", director_str);
                        params.putString("writer", writer_str);
                        params.putString("actors", actors_str);
                        params.putString("plot", plot_str);
                        params.putString("imdb_rating", rating_str);
                        mFirebaseAnalytics.logEvent("Viewed_Movies", params);


                    }
                    else {
                        Toast.makeText(MovieDetails.this, "This was not supposed to happen", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MovieDetails.this, "There was an error", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
