package com.downloader.video;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private EditText urlInput;
    private Button downloadBtn;
    private TextView statusText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlInput = findViewById(R.id.urlInput);
        downloadBtn = findViewById(R.id.downloadBtn);
        statusText = findViewById(R.id.statusText);
        downloadBtn.setOnClickListener(v -> downloadVideo());
    }
    private void downloadVideo() {
        String videoUrl = urlInput.getText().toString().trim();
        if (videoUrl.isEmpty()) {
            Toast.makeText(this, "Введите ссылку", Toast.LENGTH_SHORT).show();
            return;
        }
        statusText.setText("Загрузка...");
        downloadBtn.setEnabled(false);
        new Thread(() -> {
            try {
                String fileName = "video_" + System.currentTimeMillis() + ".mp4";
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(dir, fileName);
                URL url = new URL(videoUrl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
                input.close();
                runOnUiThread(() -> {
                    statusText.setText("Скачано: " + fileName);
                    Toast.makeText(MainActivity.this, "Видео сохранено в Загрузки", Toast.LENGTH_LONG).show();
                    downloadBtn.setEnabled(true);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    statusText.setText("Ошибка: " + e.getMessage());
                    downloadBtn.setEnabled(true);
                });
            }
        }).start();
    }
}
