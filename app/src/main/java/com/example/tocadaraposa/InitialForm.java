package com.example.tocadaraposa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by EduardoE on 23/06/2019.
 */

public class InitialForm extends AppCompatActivity {

    private EditText name_form;
    private RadioGroup gender_form;
    private RadioButton selected_gender_form;
    private Button button_form;
    SharedPreferences prefs_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_form);

        // Inicializamos datos personales
        prefs_info = getSharedPreferences("info", Context.MODE_PRIVATE);

        // Relaciona los elementos del layout con los de la clase java
        button_form = findViewById(R.id.button_form);
        name_form = findViewById(R.id.name_form);
        gender_form = findViewById(R.id.gender_form);

        // onClickListener en el botón de "Guardar"
        button_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    public void submitForm(){

        // Buscamos el RadioButton marcado y lo relacionamos
        selected_gender_form = findViewById(gender_form.getCheckedRadioButtonId());
        //selected_gender_form.getText() BORRAR

        // Comprueba si el nombre y el género están bien
        if (!checkName() || !checkGender()){
            return;
        }

        // Guardamos las variables
        initializeVariables();

        // Toast con información de que se creó el perfil
        Toast.makeText(getApplicationContext(), R.string.form_congratulations, Toast.LENGTH_SHORT).show();

        // Guardamos en "infos" que ya hemos hecho el wellcome y cerramos activity
        prefs_info = getSharedPreferences("info", Context.MODE_PRIVATE);
        prefs_info.edit().putBoolean("show_dataform", false).apply();

        // Save the token value in the db
        fetchData process = new fetchData("{\"user\": \""+prefs_info.getString("my_name", null)+"\", \"type\": \"token\", \"value\": \""+prefs_info.getString("token", null)+"\"}","ServerRequest");
        process.execute();

        // Cambiamos a la sigiente activity del Form
        finish();
        Intent intent = new Intent(InitialForm.this, MainActivity.class);
        startActivity(intent);


    }

    public void initializeVariables(){

        // Pasamos el nombre y el género en string
        String name = name_form.getText().toString().trim();
        String gender = selected_gender_form.getText().toString().trim();

        // Guardamos en "me" nuestros datos (name, gender)
        prefs_info.edit().putString("my_name", name).apply();
        prefs_info.edit().putString("my_gender", gender).apply();
    }


    // CHECKERS //

    // Coprobación de nombre
    public boolean checkName() {
        if (name_form.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Mete nome", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Comprobación de género
    public boolean checkGender(){
        if (selected_gender_form == null) {
            Toast.makeText(getApplicationContext(), "Mete género", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}