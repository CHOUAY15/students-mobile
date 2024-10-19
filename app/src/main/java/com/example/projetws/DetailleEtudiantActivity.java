package com.example.projetws;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projetws.entities.Etudiant;
import com.example.projetws.repository.StudentRepository;

import org.json.JSONObject;

public class DetailleEtudiantActivity extends AppCompatActivity {
    private ImageView imageViewEtudiant;
    private EditText editNom, editPrenom;
    private Spinner spinnerVille;
    private RadioButton radioHomme, radioFemme;
    private Button btnModifier, btnValider, btnAnnuler;
    private LinearLayout layoutButtons;
    private RadioGroup radioGroupSexe;
    private StudentRepository studentRepository;
    private Etudiant currentEtudiant;

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
                R.array.villes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVille.setAdapter(adapter);
    }

    private void displayStudentData() {
        if (currentEtudiant != null) {
            editNom.setText(currentEtudiant.getNom());
            editPrenom.setText(currentEtudiant.getPrenom());

            // Set ville in spinner
            ArrayAdapter adapter = (ArrayAdapter) spinnerVille.getAdapter();
            int position = adapter.getPosition(currentEtudiant.getVille());
            spinnerVille.setSelection(position);

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
    }

    private void enableEditing(boolean enable) {
        editNom.setEnabled(enable);
        editPrenom.setEnabled(enable);
        spinnerVille.setEnabled(enable);
        radioHomme.setEnabled(enable);
        radioFemme.setEnabled(enable);
        layoutButtons.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnModifier.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void updateStudent() {
        // Update student object with new values
        currentEtudiant.setNom(editNom.getText().toString());
        currentEtudiant.setPrenom(editPrenom.getText().toString());
        currentEtudiant.setVille(spinnerVille.getSelectedItem().toString());
        currentEtudiant.setSexe(radioHomme.isChecked() ? "homme" : "femme");

        // Call repository to update student
        studentRepository.updateEtudiant(
                currentEtudiant.getId(),
                currentEtudiant.getNom(),
                currentEtudiant.getPrenom(),
                currentEtudiant.getVille(),
                currentEtudiant.getSexe(),
                new StudentRepository.UpdateCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(DetailleEtudiantActivity.this,
                                "Étudiant modifié avec succès", Toast.LENGTH_SHORT).show();
                        enableEditing(false);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(DetailleEtudiantActivity.this,
                                "Erreur lors de la modification: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}