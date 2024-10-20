package com.example.projetws;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projetws.entities.Etudiant;
import com.example.projetws.repository.StudentRepository;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailleEtudiantActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private ImageView imageViewEtudiant;
    private EditText editNom, editPrenom;
    private AutoCompleteTextView spinnerVille;
    private RadioButton radioHomme, radioFemme;
    private Button btnModifier, btnValider, btnAnnuler;
    private LinearLayout layoutButtons;
    private RadioGroup radioGroupSexe;
    private StudentRepository studentRepository;
    private Etudiant currentEtudiant;
    private static final int RESULT_UPDATED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaille_etudiant);

        // Initialize views
        initViews();

        // Initialize repository
        studentRepository = new StudentRepository(this);

        // Get student data from intent
        currentEtudiant = (Etudiant) getIntent().getSerializableExtra("etudiant");

        // Setup spinner
        setupSpinner();

        // Display student data
        displayStudentData();

        // Setup button listeners
        setupButtonListeners();
    }

    private void initViews() {
        imageViewEtudiant = findViewById(R.id.imageViewEtudiant);
        editNom = findViewById(R.id.editNom);
        editPrenom = findViewById(R.id.editPrenom);
        spinnerVille = findViewById(R.id.spinnerVille);
        radioHomme = findViewById(R.id.radioHomme);
        radioFemme = findViewById(R.id.radioFemme);
        btnModifier = findViewById(R.id.btnModifier);
        btnValider = findViewById(R.id.btnValider);
        btnAnnuler = findViewById(R.id.btnAnnuler);
        layoutButtons = findViewById(R.id.layoutButtons);
        radioGroupSexe = findViewById(R.id.radioGroupSexe);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.villes, android.R.layout.simple_dropdown_item_1line);
        spinnerVille.setAdapter(adapter);
    }

    private void displayStudentData() {
        if (currentEtudiant != null) {
            editNom.setText(currentEtudiant.getNom());
            editPrenom.setText(currentEtudiant.getPrenom());

            // Set ville in AutoCompleteTextView
            spinnerVille.setText(currentEtudiant.getVille(), false);

            // Set sexe
            if (currentEtudiant.getSexe().equals("homme")) {
                radioHomme.setChecked(true);
            } else {
                radioFemme.setChecked(true);
            }

            // Load image
            Glide.with(this)
                    .load(currentEtudiant.getFullImageUrl())
                    .into(imageViewEtudiant);
        }
    }

    private void setupButtonListeners() {
        btnModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing(true);
            }
        });

        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStudent();
            }
        });

        btnAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing(false);
                displayStudentData();
            }
        });
        imageViewEtudiant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNom.isEnabled()) {  // Only allow image selection when editing
                    openImagePicker();
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void enableEditing(boolean enable) {
        spinnerVille.setEnabled(enable);
        editNom.setEnabled(enable);
        editPrenom.setEnabled(enable);
        radioHomme.setEnabled(enable);
        radioFemme.setEnabled(enable);
        layoutButtons.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnModifier.setVisibility(enable ? View.GONE : View.VISIBLE);
        imageViewEtudiant.setClickable(enable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Preview the selected image
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(imageViewEtudiant);
        }
    }

    private void updateStudent() {
        if (!validateInputs()) {
            return;
        }

        // Update student object with new values
        currentEtudiant.setNom(editNom.getText().toString().trim());
        currentEtudiant.setPrenom(editPrenom.getText().toString().trim());
        currentEtudiant.setVille(spinnerVille.getText().toString());
        currentEtudiant.setSexe(radioHomme.isChecked() ? "homme" : "femme");

        studentRepository.updateEtudiant(
                currentEtudiant,
                selectedImageUri,
                this,
                new StudentRepository.UpdateCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        runOnUiThread(() -> {
                            try {
                                if (result.has("imageUrl")) {
                                    currentEtudiant.setImageUrl(result.getString("imageUrl"));
                                }

                                Toast.makeText(DetailleEtudiantActivity.this,
                                        "Étudiant modifié avec succès", Toast.LENGTH_SHORT).show();

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updatedStudent", currentEtudiant);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            } catch (JSONException e) {
                                onError(e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(DetailleEtudiantActivity.this,
                                    "Erreur lors de la modification: " + error,
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private boolean validateInputs() {
        if (editNom.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Le nom est requis", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editPrenom.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Le prénom est requis", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if a valid city is selected
        if (spinnerVille.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "La ville est requise", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}