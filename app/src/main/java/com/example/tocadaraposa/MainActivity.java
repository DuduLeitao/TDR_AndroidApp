package com.example.tocadaraposa;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.longdo.mjpegviewer.MjpegView;


public class MainActivity extends AppCompatActivity{

    public static String name;
    public static ProgressBar progressBar;
    public static ImageView gateState;
    public static ImageView camera;
    public static ProgressBar progressBarImage;
    public static ImageView lightbulb;
    public static ProgressBar lightbulb_progressBar;
    public static SharedPreferences prefs_info;
    private static Dialog imageWindow;
    static PhotoView campic;
//    public static MjpegView campic;
    public static TextView debugTextView;
//    public static String URL_server = "http://192.168.1.148";
    public static String URL_server = "http://tdr.hopto.org:8080";

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Personal data initialization.
        prefs_info = getSharedPreferences("info", Context.MODE_PRIVATE);

        // Change to form layout if needed.
        dataForm();

        // Hide the ActionBar
        getSupportActionBar().hide();

        name = prefs_info.getString("my_name", getString(R.string.name_title));

        // Set superior "welcome" text.
        setUpText();

        // Relate layout elements with the variables in the java class.
        progressBar = findViewById(R.id.opening_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        lightbulb_progressBar = findViewById(R.id.lightbulb_progressBar);
        lightbulb_progressBar.setVisibility(View.INVISIBLE);
        gateState = findViewById(R.id.gate_state);
        gateState.setTag("");
        camera = findViewById(R.id.camera);
        lightbulb = findViewById(R.id.lightbulb);
        lightbulb.setTag("");
        debugTextView = findViewById(R.id.debugTextView);


        // Prepare variables to show video stream in a dialog.
        /*
        imageWindow = new Dialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.image_window, null);
        imageWindow.setContentView(view);
        progressBarImage = imageWindow.findViewById(R.id.image_progressBar);
        progressBarImage.setVisibility(View.INVISIBLE);
        campic = imageWindow.findViewById(R.id.campic);
        campic.setMode(MjpegView.MODE_FIT_WIDTH);
        campic.setAdjustHeight(true);
        campic.setUrl(URL_server+"/videoServer.php");
         */

        // Prepare variables to take picture, download and present it in a dialog.
        imageWindow = new Dialog(MainActivity.this);
        imageWindow.setContentView(R.layout.image_window);
        imageWindow.setTitle("Gate image");
        campic = imageWindow.findViewById(R.id.campic);
        progressBarImage = imageWindow.findViewById(R.id.image_progressBar);
        progressBarImage.setVisibility(View.INVISIBLE);


        // Update gate state.
        updateGateState();

        // Click handler for the open gate button.
        gateState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAlert(gateState)){
                    updateGateState();
                }else{
                    openGate();
                }
            }
        });

        imageWindow.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //campic.stopStream();
                gateState.setEnabled(true);
                camera.setEnabled(true);
                lightbulb.setEnabled(true);
            }
        });

        // Click handler for the camera button.
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                imageViewDialog();
            }
        });


        // Click handler for the lightbulb button.
        lightbulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAlert(lightbulb)){
                    updateGateState();
                }else{
                    switchLight();
                }
            }
        });
    }

    public void dataForm(){
        // Actividad sobre el botón de la pantalla de presentación
        if (prefs_info.getBoolean("show_dataform", true)){
            finish();
            Intent intent = new Intent(MainActivity.this, InitialForm.class);
            startActivity(intent);
        }
    }

    public void setUpText(){
        TextView welcome_text_TV = findViewById(R.id.welcome_text);
        String genmod;
        String welcome_text;
        if (prefs_info.getString("my_gender", getString(R.string.gender_title)).equals(getString(R.string.female))){
            genmod = "a ";
        }else{
            genmod = "o ";
        }
        welcome_text = getString(R.string.welcome) + genmod + getString(R.string.to_app) + ", " + prefs_info.getString("my_name", getString(R.string.name_title))+"!";
        welcome_text_TV.setText(welcome_text);
    }

    public static void updateGateState(){
        progressBar.setVisibility(View.VISIBLE);
        lightbulb_progressBar.setVisibility(View.VISIBLE);
        gateState.setEnabled(false);
        camera.setEnabled(false);
        lightbulb.setEnabled(false);
        fetchData process = new fetchData("{\"user\": \""+MainActivity.name+"\", \"type\": \"command\", \"value\": \"updateGateState\"}", "ServerRequest");
        process.execute();
    }

    public static void openGate(){
        progressBar.setVisibility(View.VISIBLE);
        gateState.setEnabled(false);
        camera.setEnabled(false);
        lightbulb.setEnabled(false);
        // Create an instance of the class in charge of the communication process and executes it
        String gateAction = "open";
        if (prefs_info.getString("gate_state", "open").equals("open")){ //Check if the gate is opened, if so, the order is to close it, otherwise (if the gate is closed), the order is to open it
            gateAction = "close";
        }
        fetchData process = new fetchData("{\"user\": \""+MainActivity.name+"\", \"type\": \"command\", \"value\": \""+gateAction+"\"}","ServerRequest");
        process.execute();
    }

    public static void switchLight(){
        lightbulb_progressBar.setVisibility(View.VISIBLE);
        gateState.setEnabled(false);
        camera.setEnabled(false);
        lightbulb.setEnabled(false);
        // Create an instance of the class in charge of the communication process and executes it
        String gateAction = "gardenLightSwitch";
        fetchData process = new fetchData("{\"user\": \""+MainActivity.name+"\", \"type\": \"command\", \"value\": \""+gateAction+"\"}","ServerRequest");
        process.execute();
    }

    public void takePicture(){
        progressBarImage.setVisibility(View.VISIBLE);
        gateState.setEnabled(false);
        camera.setEnabled(false);
        lightbulb.setEnabled(false);
        campic.setImageResource(R.drawable.info);
//        campic.startStream();
        // Create an instance of the class in charge of the communication process and executes it
        fetchData process = new fetchData("{\"user\": \""+MainActivity.name+"\", \"type\": \"command\", \"value\": \"picture\"}","ServerRequest");
        process.execute();

    }

    public void imageViewDialog(){
        // Create an instance of the class in charge of the communication process and executes it
        fetchData process = new fetchData("Null","DownloadPicture");
        process.execute();
        imageWindow.show();
    }

    public boolean checkAlert(ImageView imageView) {

        String tag = (String) imageView.getTag();
        return tag.equals("alert");
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();

        if (imageWindow.isShowing()){
            campic.stopStream();
            gateState.setEnabled(true);
            camera.setEnabled(true);
            lightbulb.setEnabled(true);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (imageWindow.isShowing()){
            campic.startStream();
            gateState.setEnabled(false);
            camera.setEnabled(false);
            lightbulb.setEnabled(false);
        }

    }
     */
}