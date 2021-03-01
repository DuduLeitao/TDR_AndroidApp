package com.example.tocadaraposa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class fetchData extends AsyncTask<Void,Void,Void> {

    private String data ="";
    private final String dataParsed = "";
    private final String singleParsed = "";
    JSONObject jsonObject = null;
    private Bitmap bitmap = null;

    fetchData(String jsonOutputString, String command) {
        this.jsonOutputString = jsonOutputString;
        this.command = command;
    }

    private final String jsonOutputString;
    private final String command;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {

        if(command.equals("ServerRequest")) {
            try {
                URL url = new URL(MainActivity.URL_server);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                byte[] input = jsonOutputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine = "";
                while (inputLine != null) {
                    inputLine = bufferedReader.readLine();
                    data = data + inputLine;
                }

                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (java.net.SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(command.equals("DownloadPicture")){
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(MainActivity.URL_server +"/campic.jpg").getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (command.equals("DownloadPicture")) {
            MainActivity.campic.setImageBitmap(bitmap);
            MainActivity.progressBarImage.setVisibility(View.INVISIBLE);
            MainActivity.lightbulb_progressBar.setVisibility(View.INVISIBLE);
            MainActivity.gateState.setEnabled(true);
            MainActivity.camera.setEnabled(true);
            MainActivity.lightbulb.setEnabled(true);
            MainActivity.debugTextView.setText("");
            return;
        }

        if (jsonObject==null) {
            MainActivity.gateState.setImageResource(R.drawable.alert);
            MainActivity.gateState.setTag("alert");
            MainActivity.lightbulb.setImageResource(R.drawable.alert);
            MainActivity.lightbulb.setTag("alert");
            MainActivity.progressBar.setVisibility(View.INVISIBLE);
            MainActivity.lightbulb_progressBar.setVisibility(View.INVISIBLE);
            MainActivity.gateState.setEnabled(true);
            MainActivity.camera.setEnabled(true);
            MainActivity.lightbulb.setEnabled(true);
            return;
        }

        try {
            Iterator<String> keys = jsonObject.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                switch (key){
                    case "result":
                        String result = (String) jsonObject.get("result");
                        if (result.contains("ERROR")){
                            MainActivity.debugTextView.setText("Response: ERROR");
                            return;
                        }
                        MainActivity.debugTextView.setText("");
                        break;
                    case "gateAction":
                        SystemClock.sleep(17000);
                        MainActivity.updateGateState();
                        break;
                    case "gardenLightAction":
                        MainActivity.updateGateState();
                        break;
                    case "gateState":
                        String gateState = (String) jsonObject.get("gateState");
                        if (gateState.equals("opend")){
                            MainActivity.gateState.setImageResource(R.drawable.opened_gate_green);
                            MainActivity.prefs_info.edit().putString("gate_state", "open").apply();
                            MainActivity.gateState.setTag("");
                        } else if (gateState.equals("closed")){
                            MainActivity.gateState.setImageResource(R.drawable.closed_gate_red);
                            MainActivity.prefs_info.edit().putString("gate_state", "close").apply();
                            MainActivity.gateState.setTag("");
                        } else {
                            MainActivity.gateState.setImageResource(R.drawable.alert);
                            MainActivity.gateState.setTag("alert");
                        }
                        MainActivity.progressBar.setVisibility(View.INVISIBLE);
                        MainActivity.lightbulb_progressBar.setVisibility(View.INVISIBLE);
                        MainActivity.gateState.setEnabled(true);
                        MainActivity.camera.setEnabled(true);
                        MainActivity.lightbulb.setEnabled(true);
                        break;
                    case "gardenLightState":
                        String gardenLightState = (String) jsonObject.get("gardenLightState");
                        if(gardenLightState.contains("on")){
                            MainActivity.lightbulb.setImageResource(R.drawable.lightbulb_on);
                            MainActivity.lightbulb.setTag("");
                        }else if(gardenLightState.contains("off")) {
                            MainActivity.lightbulb.setImageResource(R.drawable.lightbulb_off);
                            MainActivity.lightbulb.setTag("");
                        } else {
                            MainActivity.lightbulb.setImageResource(R.drawable.alert);
                            MainActivity.lightbulb.setTag("alert");
                        }
                        MainActivity.progressBar.setVisibility(View.INVISIBLE);
                        MainActivity.lightbulb_progressBar.setVisibility(View.INVISIBLE);
                        MainActivity.gateState.setEnabled(true);
                        MainActivity.camera.setEnabled(true);
                        MainActivity.lightbulb.setEnabled(true);
                        break;
                    default:
                        MainActivity.debugTextView.setText("JSON key: ERROR");
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
