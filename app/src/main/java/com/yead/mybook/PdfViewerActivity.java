package com.yead.mybook;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PdfViewerActivity extends AppCompatActivity {

    private WebView pdfWebView;
    private Button startAudioButton;
    private TextToSpeech textToSpeech;
    private String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfWebView = findViewById(R.id.pdfWebView);
        startAudioButton = findViewById(R.id.startAudioButton);

        // Get PDF URL from intent
        pdfUrl = getIntent().getStringExtra("PDF_URL");
        if (pdfUrl != null) {
            loadPdfInWebView(pdfUrl);
        } else {
            Toast.makeText(this, "No PDF URL found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            } else {
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Start audio reading
        startAudioButton.setOnClickListener(v -> startTextToSpeech());
    }

    private void loadPdfInWebView(String pdfUrl) {
        String googleDriveUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl;
        pdfWebView.setWebViewClient(new WebViewClient());
        pdfWebView.getSettings().setJavaScriptEnabled(true);
        pdfWebView.loadUrl(googleDriveUrl);
    }

    private void startTextToSpeech() {
        String sampleText = "This is a placeholder text for the audio reading.";
        textToSpeech.speak(sampleText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
