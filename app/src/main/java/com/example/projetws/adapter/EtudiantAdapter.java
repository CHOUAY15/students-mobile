package com.example.projetws.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projetws.DetailleEtudiantActivity;
import com.example.projetws.R;
import com.example.projetws.entities.Etudiant;
import com.example.projetws.repository.StudentRepository;

import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.ViewHolder> {

    private List<Etudiant> etudiants;
    private Context context;
    private StudentRepository studentRepository;

    public EtudiantAdapter(List<Etudiant> etudiants, Context context) {
        this.etudiants = etudiants;
        this.context = context;
        this.studentRepository = new StudentRepository(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etudiant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);
        holder.textViewNom.setText(etudiant.getNom());
        holder.textViewPrenom.setText(etudiant.getPrenom());
        holder.textViewVille.setText(etudiant.getVille());
        holder.textViewSexe.setText(etudiant.getSexe());

        Glide.with(context)
                .load(etudiant.getFullImageUrl())
                .into(holder.imageViewEtudiant);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailleEtudiantActivity.class);
                intent.putExtra("etudiant", etudiant);
                context.startActivity(intent);
            }
        });
    }

    public void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmer la suppression")
                .setMessage("Voulez-vous vraiment supprimer cet étudiant ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEtudiant(position);
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyItemChanged(position);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void deleteEtudiant(int position) {
        Etudiant etudiant = etudiants.get(position);
        studentRepository.deleteEtudiant(etudiant.getId(), new StudentRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                etudiants.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Erreur lors de la suppression: " + error, Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    public Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewEtudiant;
        TextView textViewNom, textViewPrenom, textViewVille, textViewSexe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewEtudiant = itemView.findViewById(R.id.imageViewEtudiant);
            textViewNom = itemView.findViewById(R.id.textViewNom);
            textViewPrenom = itemView.findViewById(R.id.textViewPrenom);
            textViewVille = itemView.findViewById(R.id.textViewVille);
            textViewSexe = itemView.findViewById(R.id.textViewSexe);
        }
    }

    public void updateEtudiants(List<Etudiant> newEtudiants) {
        this.etudiants = newEtudiants;
        notifyDataSetChanged();
    }
}