package com.example.projetws.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetws.R;
import com.example.projetws.beans.Etudiant;

import java.util.ArrayList;
import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> implements Filterable {

    public List<Etudiant> etudiantList;
    public List<Etudiant> filtredList;
    private final Context context;

    public EtudiantAdapter(Context context ,List<Etudiant> etudiantList) {
        this.context = context ;
        this.etudiantList = etudiantList;
        this.filtredList = new ArrayList<>(etudiantList);
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_etudiant, parent, false);
        final EtudiantViewHolder holder = new EtudiantViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popup = LayoutInflater.from(context).inflate(R.layout.item_etudiant, null, false);
                final ImageView img = popup.findViewById(R.id.avatar);
                final TextView fullname = popup.findViewById(R.id.Nom_Prenom);
                //final TextView ville = popup.findViewById(R.id.textViewVille);
                Bitmap bitmap = ((BitmapDrawable)((ImageView)view.findViewById(R.id.avatar)).getDrawable()).getBitmap();
                img.setImageBitmap(bitmap);
                fullname.setText(((TextView)view.findViewById(R.id.Nom_Prenom)).getText().toString());
//                prenom.setText(((TextView)view.findViewById(R.id.textViewPrenom)).getText().toString());
                //ville.setText(((TextView)view.findViewById(R.id.ville)).getText().toString());
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Faire le choi")
                        .setMessage("Voulez-vous modifier ou supprimer l'etudiant ?")
                        .setView(popup)
                        .setPositiveButton("modifier", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Supprimer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.show();
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Log.d(TAG, "onBindView call ! " + position);
        Etudiant etudiant = filtredList.get(position);
        Log.d("RecyclerView", "Affichage de l'étudiant : " + etudiant.getNom());

        holder.fullname.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        holder.textViewVille.setText(etudiant.getVille());
        //holder.textViewSexe.setText(etudiant.getSexe());
    }

    @Override
    public int getItemCount() {
        return filtredList.size();
    }

    @Override
    public Filter getFilter() {
        return new NewFilter();
    }

    static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView fullname;
        //        TextView textViewPrenom;
        TextView textViewVille;
        //TextView textViewSexe;
        LinearLayout parent ;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.Nom_Prenom);
            //textViewPrenom = itemView.findViewById(R.id.textViewPrenom);
            textViewVille = itemView.findViewById(R.id.ville);
            //textViewSexe = itemView.findViewById(R.id.textViewSexe);
            parent = itemView.findViewById(R.id.recyclerView);
        }
    }

    private class NewFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Etudiant> filteredList = new ArrayList<>();
            final FilterResults results = new FilterResults();

            if (charSequence.length() == 0) {
                filteredList.addAll(etudiantList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Etudiant etudiant : etudiantList) {
                    if (etudiant.getNom().toLowerCase().contains(filterPattern)) {
                        filteredList.add(etudiant);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filtredList.clear();
            filtredList.addAll((List<Etudiant>) filterResults.values);
            notifyDataSetChanged();
        }
    }
}
