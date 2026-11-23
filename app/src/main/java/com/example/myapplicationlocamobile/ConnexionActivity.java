package com.example.myapplicationlocamobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationlocamobile.reseau.ClientApi;
import com.example.myapplicationlocamobile.reseau.ConnexionRequete;
import com.example.myapplicationlocamobile.reseau.Profil;
import com.example.myapplicationlocamobile.reseau.Session;
import com.example.myapplicationlocamobile.reseau.TokenReponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {
    private EditText courriel;
    private EditText motDePasse;
    private Button boutonConnexion;
    private TextView resultat;

    private void afficher(Profil profil, int code) {
        resultat.setText("Nom : " + profil.nom + "" +
                "\nRôle : " + profil.role + "" +
                "\nAdresse courriel : " + profil.courriel + "" +
                "\nCode HTTP : " + code);
    }
    private void afficherErreur(String message) {
        resultat.setText(message);
    }
    private void getProfil(TokenReponse token) {
        ClientApi.get().monProfil("Bearer " + token.token).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(Call<Profil> call, Response<Profil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    afficher(response.body(), response.code());
                }else {
                    afficherErreur("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Profil> call, Throwable throwable) {
                afficherErreur("Serveur injoignable : " + throwable.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connexion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boutonConnexion = findViewById(R.id.buttonConnection);
        resultat = findViewById(R.id.resultat);
        boutonConnexion.setOnClickListener(v -> {
            resultat.setText("");
            courriel = findViewById(R.id.textEmailAddress);
            motDePasse = findViewById(R.id.textPassword);
            String strCourriel = courriel.getText().toString().trim();
            String strMotDePasse = motDePasse.getText().toString().trim();
            if (strCourriel.isEmpty()) {
                courriel.setError("Le champ courriel est vide.");
            }else if (strMotDePasse.isEmpty()) {
                motDePasse.setError("Le champ mot de passe est vide.");
            }else {
                ConnexionRequete connexionRequete = new ConnexionRequete(strCourriel, strMotDePasse);
                ClientApi.get().connecter(connexionRequete).enqueue(new Callback<TokenReponse>() {
                    @Override
                    public void onResponse(Call<TokenReponse> call, Response<TokenReponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Session.enregistrer(v.getContext(), response.body().token, response.body().nom);
                            getProfil(response.body());
                        } else {
                            try {//J'ai utilisé l'agent intégré d'Android Studio pour m'aider avec ce bloc de code. Sans cette gestion d'erreur une erreur que je ne reconnaissais pas survenait. L'agent m'a donc suggéré d'ajouter ce try/catch et les if pour vérifier que le errorBody est non-null et qu'il contient bien un message.
                                if (response.errorBody() != null) {
                                    String brute = response.errorBody().string();
                                    JsonObject json = new Gson().fromJson(brute, JsonObject.class);
                                    if (json != null && json.has("message") && !json.get("message").isJsonNull()) {
                                        String messageErreur = json.get("message").getAsString();
                                        afficherErreur("Erreur HTTP " + response.code() + " : " + messageErreur);
                                    } else {
                                        afficherErreur("Erreur HTTP " + response.code());
                                    }
                                } else {
                                    afficherErreur("Erreur HTTP " + response.code());
                                }
                            } catch (IOException e) {
                                afficherErreur("Erreur HTTP " + response.code());
                            }//Fin du bloc assisté par l'agent.
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenReponse> call, Throwable throwable) {
                        afficherErreur("Serveur injoignable : " + throwable.getMessage());
                    }
                });
            }
        });
    }
}