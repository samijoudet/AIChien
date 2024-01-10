package com.example.aichien;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter; // Importer la classe Interpreter de TensorFlow Lite ne marche pas

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoImageView = findViewById(R.id.photoImageView);
        Button captureButton = findViewById(R.id.captureButton);



        // Charger le modèle à partir du fichier .tflite dans le dossier assets
        try {
            Interpreter.Options options = new Interpreter.Options();
            Interpreter interpreter = new Interpreter(loadModelFile("model.tflite", context), options);

            // Effectuer une prédiction avec une image
            float[][] input = preprocessImage(image);  // Prétraiter l'image selon les besoins du modèle
            float[][] output = new float[1][NUM_CLASSES];  // Assurez-vous que NUM_CLASSES correspond au nombre de classes de votre modèle

            interpreter.run(input, output);

            // Traiter les résultats de la prédiction
            // ...
        } catch (IOException e) {
            // Gérer les erreurs de chargement du modèle
            e.printStackTrace();
        }

        private MappedByteBuffer loadModelFile(String filename, Context context) throws IOException {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(filename);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }

    }

    public void capturePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // request

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoImageView.setImageBitmap(imageBitmap);
            photoImageView.setVisibility(View.VISIBLE); // Affiche l'ImageView

            // TODO: Envoyer l'image au serveur TensorFlow et traiter les résultats
            // Vous devrez implémenter la logique de communication avec le serveur ici
        }
    }
}
