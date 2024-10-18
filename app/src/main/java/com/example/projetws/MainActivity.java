package com.example.projetws;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projetws.adapter.EtudiantAdapter;
import com.example.projetws.entities.Etudiant;
import com.example.projetws.repository.StudentRepository;
import com.example.projetws.util.SwipeToDeleteCallback;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabHost tabHost;
    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiantList;
    private StudentRepository studentRepository;

    // Form inputs for adding a student
    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private Button add, btnSelectImage;
    private ImageView imageView;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentRepository = new StudentRepository(this);

        setupTabHost();
        setupRecyclerView();
        setupFormInputs();
        setupImagePicker();

        // Fetch the list of students when the activity starts
        fetchEtudiants();
    }

    private void setupTabHost() {
        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1");
        spec1.setIndicator("Ajouter");
        spec1.setContent(R.id.tab1);
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2");
        spec2.setIndicator("Liste");
        spec2.setContent(R.id.tab2);
        tabHost.addTab(spec2);

        // Handle switching between tabs
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if ("Tab2".equals(tabId)) {
                    fetchEtudiants();
                }
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        etudiantList = new ArrayList<>();
        adapter = new EtudiantAdapter(etudiantList, this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void fetchEtudiants() {
        studentRepository.fetchEtudiants(new StudentRepository.FetchCallback() {
            @Override
            public void onSuccess(List<Etudiant> etudiants) {
                etudiantList.clear();
                etudiantList.addAll(etudiants);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                showToast("Error fetching students: " + error);
            }
        });
    }

    private void setupFormInputs() {
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        imageView = findViewById(R.id.imageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        add = findViewById(R.id.add);

        // Handle form submission
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    String sexe = m.isChecked() ? "homme" : "femme";
                    studentRepository.addEtudiant(
                            nom.getText().toString(),
                            prenom.getText().toString(),
                            ville.getSelectedItem().toString(),
                            sexe,
                            selectedImageUri,
                            MainActivity.this,
                            new StudentRepository.AddCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    showToast("Étudiant ajouté avec succès");
                                    clearInputs();
                                }

                                @Override
                                public void onError(String error) {
                                    showToast("Erreur lors de l'ajout : " + error);
                                }
                            });
                }
            }
        });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            imageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        if (nom.getText().toString().trim().isEmpty()) {
            showToast("Veuillez entrer un nom");
            return false;
        }
        if (prenom.getText().toString().trim().isEmpty()) {
            showToast("Veuillez entrer un prénom");
            return false;
        }
        if (ville.getSelectedItemPosition() == 0) {
            showToast("Veuillez sélectionner une ville");
            return false;
        }
        if (!m.isChecked() && !f.isChecked()) {
            showToast("Veuillez sélectionner un sexe");
            return false;
        }
        if (selectedImageUri == null) {
            showToast("Veuillez sélectionner une image");
            return false;
        }
        return true;
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        nom.setText("");
        prenom.setText("");
        ville.setSelection(0);
        m.setChecked(false);
        f.setChecked(false);
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        selectedImageUri = null;
    }
}
