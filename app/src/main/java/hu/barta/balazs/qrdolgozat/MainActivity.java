package hu.barta.balazs.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView textViewQRResult;
    private Button buttonScan, buttonKiir;
    private boolean writePermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("QR kód olvasása by Balázs");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.initiateScan();
            }
        });
        buttonKiir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String szoveg = textViewQRResult.getText().toString();
                if (szoveg != ""){
                    try {
                        Naplozas.kiir(szoveg);
                        Toast.makeText(MainActivity.this, "Sikeresen beleírtad a fájlba a szöveget!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.d("Kiírás hiba", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Nincs szöveg amit be lehetne írni", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if ( result != null ){
            if (result.getContents() == null){
                Toast.makeText(this, "Kiléptél a scannerből", Toast.LENGTH_SHORT).show();
            } else {
                textViewQRResult.setText(result.getContents());

                try {
                    Uri url = Uri.parse(result.getContents());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                } catch (Exception exception) {
                    Log.d("URL ERROR", exception.toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                fileIrasEllenorzes();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void fileIrasEllenorzes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            writePermission = false;
        } else {
            writePermission = true;
        }
    }

    public void init(){
        textViewQRResult = findViewById(R.id.textViewQRResult);
        buttonScan = findViewById(R.id.buttonScan);
        buttonKiir = findViewById(R.id.buttonKiir);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            String[] permission = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            writePermission = false;
            requestPermissions(permission, 0);
        } else {
            writePermission = true;
        }
    }
}