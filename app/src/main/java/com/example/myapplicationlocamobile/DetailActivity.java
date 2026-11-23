package com.example.myapplicationlocamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationlocamobile.reseau.ClientApi;
import com.example.myapplicationlocamobile.reseau.LocaMobileApi;
import com.example.myapplicationlocamobile.reseau.Logement;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static final String  CLE_ID = "id";
    public static final String  CLE_TITRE = "titre";
    public static final String  CLE_PRIX = "prix";

    private TextView CHAMP_TITRE;
    private TextView CHAMP_VILLE;
    private TextView CHAMP_PRIX;
    private TextView champ_nbVoyageurs;
    private TextView champ_proprietaire;
    private TextView conteneurMessageErreur;
    private Button BOUTON_RESERVER;
    private void afficher(Logement logement) {
        conteneurMessageErreur.setVisibility(View.GONE);
        CHAMP_TITRE.setText(logement.titre);
        CHAMP_VILLE.setText(logement.ville);
        CHAMP_PRIX.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", logement.prixParNuit));
        champ_nbVoyageurs.setText(logement.nbVoyageurs + " voyageurs maximum");
        champ_proprietaire.setText("Propriété de " + logement.proprietaireNom);
        BOUTON_RESERVER.setOnClickListener(vue -> {
            Intent intention = new Intent(this, ReservationActivity.class);
            intention.putExtra(CLE_TITRE, logement.titre);
            intention.putExtra(CLE_PRIX, logement.prixParNuit);
            startActivity(intention);
        });
    }
    private void afficherErreur(String message) {
        conteneurMessageErreur.setText(message);
        conteneurMessageErreur.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        CHAMP_TITRE = findViewById(R.id.titre);
        CHAMP_VILLE = findViewById(R.id.ville);
        CHAMP_PRIX = findViewById(R.id.prix);
        champ_nbVoyageurs = findViewById(R.id.nbVoyageurs);
        champ_proprietaire = findViewById(R.id.proprietaire);
        conteneurMessageErreur =findViewById(R.id.etat);
        BOUTON_RESERVER = findViewById(R.id.reserver);
        int id = getIntent().getIntExtra(CLE_ID, 0);
        ClientApi.get().trouverLogement(id).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(Call<Logement> call, Response<Logement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    afficher(response.body());
                } else if (response.code() == 404) {
                    afficherErreur("Ce logement n'existe plus (404).");
                } else {
                    afficherErreur("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Logement> call, Throwable throwable) {

            }
        });
    }
}