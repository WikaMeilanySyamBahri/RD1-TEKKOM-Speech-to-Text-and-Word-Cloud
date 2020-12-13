package com.example.speechtotext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

// Memanggil kelas speech recognizer untuk merecord audio
public class MainActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;

    private SpeechRecognizer speechRecognizer;

    // Memanggil atribut
    EditText editText;
    ImageView imageView;
    AlertDialog.Builder alertSpeechDialog;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.EditText);
        imageView = findViewById(R.id.imageView);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        // Membangun speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Memasukkan extra bahasa
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Set recognizer listener saat mulai menggunakan aplikasi
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            // Beginning speech dengan alert dialog
            @Override
            public void onBeginningOfSpeech() {
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.alertcustom, viewGroup, false);

                alertSpeechDialog = new AlertDialog.Builder(MainActivity.this);
                alertSpeechDialog.setMessage("Listening...");
                alertSpeechDialog.setView(dialogView);
                alertDialog = alertSpeechDialog.create();
                alertDialog.show();

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }
            // Result speech diubah ke dalam teks
            @Override
            public void onResults(Bundle bundle) {
                imageView.setImageResource(R.drawable.ic_baseline_keyboard_voice_24);
                ArrayList<String> arrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(arrayList.get(0));
                alertDialog.dismiss();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        // Sentuh mic untuk memulai
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    imageView.setImageResource(R.drawable.ic_baseline_keyboard_voice_24);
                    speechRecognizer.startListening(speechIntent);
                }

                return false;
            }
        });
    }

    // Request untuk allow record audio
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO
            }, RecordAudioRequestCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


