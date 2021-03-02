@package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextInputEditText textInputEditText;
    String link;

    public void beginTask(View view)
    {
        link = "https://itunes.apple.com/search?term="+ textInputEditText.getText() + "+&enitity=song";
        link = link.replaceAll("\\s", "+");
        DownloadTask task = new DownloadTask();
        task.execute(link);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.ListView);
        textInputEditText = (TextInputEditText) findViewById(R.id.inputtext);

    }



    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                String SongInfo = jsonObject.getString("results");

                JSONArray arr = new JSONArray(SongInfo);

                int length = arr.length();

                List<String> listContents = new ArrayList<String>(length);

                String Details;

                for(int i = 0; i < length; i++)
                {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Details = "Artist: " + jsonPart.getString("artistName") + "\nTrack:" + jsonPart.getString("trackName") + "\nGenre:" + jsonPart.getString("primaryGenreName");

                    listContents.add(Details);

                }

                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.row, listContents));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}