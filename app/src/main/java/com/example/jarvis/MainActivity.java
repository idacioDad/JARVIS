package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.awt.font.TextAttribute;
import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {

    private AIConfiguration config;
    private AIService service;
    private Button button;
    TextView textview;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );


        switch (requestCode) {
            case 1: {
                // Se a solicitação de permissão foi cancelada o array vem vazio.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão cedida, recria a activity para carregar o mapa, só será executado uma vez

                    this.recreate();
                }
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );




       button= findViewById( R.id.button );
      textview= findViewById( R.id.textView );

        config = new AIConfiguration("0ea687c8a2e94c46b642f1506bd1dd19",
                AIConfiguration.SupportedLanguages.Portuguese,//Altere esta linha de acordo com o idioma que voce escolheu na consola
                AIConfiguration.RecognitionEngine.System);
        service = AIService.getService(this, config);
        service.setListener(this);

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.startListening();
            }
        } );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission( this, Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED ||

                    ContextCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED ) {

                String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

                requestPermissions( permissions, 1 );

            }

        }


    }





    @Override
    public void onResult(AIResponse response) {

        Result result = response.getResult();

        // Obter os parametros caso existam
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
        textview.setText("Query:" + result.getResolvedQuery() + //A frase que o utilizador usou
                "\nSpeech: " + result.getFulfillment().getSpeech() + //A resposta
                "\nParameters: " + parameterString); //Os parametros

    }

    @Override
    public void onError(AIError error) {
        textview.setText(error.toString());

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        textview.setText("Escutando...");

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
