package com.example.projetws.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projetws.R;
import com.example.projetws.entities.Etudiant;

import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.ViewHolder> {

    private List<Etudiant> etudiants;
    private Context context;

    public EtudiantAdapter(List<Etudiant> etudiants, Context context) {
        this.etudiants = etudiants;
        this.context = context;
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
                .load(etudiant.getImageUrl())
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                .into(holder.imageViewEtudiant);
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
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