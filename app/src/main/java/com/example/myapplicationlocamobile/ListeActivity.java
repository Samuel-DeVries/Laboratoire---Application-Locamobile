package com.example.myapplicationlocamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationlocamobile.reseau.ClientApi;
import com.example.myapplicationlocamobile.reseau.Logement;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {
    private LinearLayout conteneur;
    private TextView conteneurMessageErreur;
    private EditText champRecherche;
    private Button boutonRecherche;
    private Button boutonConnexion;
    private void afficher(List<Logement> logements) {
        conteneurMessageErreur.setVisibility(View.GONE);
        conteneur.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneur, false);
            ((TextView) carte.findViewById(R.id.carteTitre)).setText(l.titre);
            ((TextView) carte.findViewById(R.id.carteVille)).setText(l.ville);
            ((TextView) carte.findViewById(R.id.cartePrix)).setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
            carte.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, l.id);
                startActivity(intent);
            });
            conteneur.addView(carte);
        }
    }
    private void afficherErreur(String message) {
        conteneur.removeAllViews();
        conteneurMessageErreur.setText(message);
        conteneurMessageErreur.setVisibility(View.VISIBLE);
    }

    private void recupererLogements() {
        Log.d("LocaMobile", " 1 ");
        ClientApi.get().listerLogements().enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(Call<List<Logement>> call, Response<List<Logement>> response) {
                Log.d("LocaMobile", " 3 ");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("LocaMobile", "Statut : " + response.code() + " | " + response.body().size() + " logements");
                    afficher(response.body());
                }else {
                    afficherErreur("Erreur HTTP " + response.code());
                    Log.d("LocaMobile", "Statut : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Logement>> call, Throwable throwable) {
                Log.e("LocaMobile", "Erreur reseau : " + throwable.getMessage());
                afficherErreur("Serveur injoignable : " + throwable.getMessage());
            }
        });
        Log.d("LocaMobile", " 2 ");
    }
    private void recupererLogements(String ville) {
        if (ville.isEmpty()) {
            recupererLogements();
        } else {
            ClientApi.get().chercherParVille(ville).enqueue(new Callback<List<Logement>>() {
                @Override
                public void onResponse(Call<List<Logement>> call, Response<List<Logement>> response) {
                    if (response.isSuccessful() && response.body().isEmpty()) {
                        afficherErreur("Aucun logement à' " + ville + ".");
                    } else if (response.isSuccessful() && response.body() != null) {
                        afficher(response.body());
                    } else {
                        afficherErreur("Erreur HTTP " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Logement>> call, Throwable throwable) {
                    afficherErreur("Serveur injoignable : " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_liste);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        conteneur = findViewById(R.id.conteneurLogements);
        conteneurMessageErreur = findViewById(R.id.messageErreur);
        boutonRecherche = findViewById(R.id.boutonChercher);
        boutonConnexion = findViewById(R.id.buttonConnection);
        recupererLogements();
        boutonRecherche.setOnClickListener(v -> {
            champRecherche = findViewById(R.id.champVille);
            String ville = champRecherche.getText().toString().trim();
            recupererLogements(ville);
        });
        boutonConnexion.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConnexionActivity.class);
            startActivity(intent);
        });
    }
}