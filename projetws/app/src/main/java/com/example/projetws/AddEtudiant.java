package com.example.projetws;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FILE_REQUEST = 1;
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private Button photo;  // Ajoutez un bouton pour sélectionner le fichier
    private TextView photoFileNameTextView;  // Pour afficher le nom du fichier
    private Uri fileUri;
    RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/php02/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        // Initialisation des composants de l'interface utilisateur
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        photo = findViewById(R.id.photo);  // L'ID de votre bouton pour le fichier
        photoFileNameTextView = findViewById(R.id.file_name);
        add = findViewById(R.id.add);

        // Attacher le listener au bouton
        add.setOnClickListener(this);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Permet de choisir n'importe quel type de fichier
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String fileName = getFileName(fileUri);
            photoFileNameTextView.setText(fileName); // Met à jour le TextView avec le nom du fichier
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            // Initialiser la file de requêtes Volley
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            // Vérifier si les champs sont bien remplis
            if (nom.getText().toString().isEmpty() || prenom.getText().toString().isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer une requête Volley
            StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, response);
                            Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                            Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                            for(Etudiant e : etudiants){
                                Log.d(TAG, e.toString());
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VolleyError", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //String sexe = m.isChecked() ? "homme" : "femme";
                    String sexe = "";
                    if (m.isChecked())
                        sexe = "homme";
                    else
                        sexe = "femme";

                    Map<String, String> params = new HashMap<>();
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);
                    params.put("photo", fileUri != null ? fileUri.toString() : "");
                    return params;
                }
            };

            // Configurer le timeout de la requête pour éviter les ANR
            request.setRetryPolicy(new DefaultRetryPolicy(
                    5000,  // Timeout de 5 secondes
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // Nombre de tentatives
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT  // Multiplicateur de délai
            ));
            // Ajouter la requête à la file d'attente
            requestQueue.add(request);
        }
    }
}