package com.example.myapplicationlocamobile;

import android.nfc.FormatException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText champVoyageurs = findViewById(R.id.champVoyageurs);
        EditText champNuits = findViewById(R.id.champNuits);
        Button bCalculer = findViewById(R.id.boutonCalculer);
        TextView champResultat = findViewById(R.id.resultat);
        String titre = getIntent().getStringExtra(DetailActivity.CLE_TITRE);
        double prix = getIntent().getDoubleExtra(DetailActivity.CLE_PRIX, 0);
        bCalculer.setOnClickListener(event -> {
            String strVoyageurs = champVoyageurs.getText().toString();
            String strNuits = champNuits.getText().toString();
            int nVoyageurs;
            int nNuits;
            try {
                nVoyageurs = Integer.parseInt(strVoyageurs);
                if (nVoyageurs < 1) {
                    throw new NumberFormatException();
                }
            }catch (NumberFormatException e) {
                champVoyageurs.setError("Le nombre de voyageurs doit être entier et supérieur à 0.");
                return;
            }
            try {
                nNuits = Integer.parseInt(strNuits);
                if (nNuits < 1) {
                    throw new NumberFormatException();
                }
            }catch (NumberFormatException e) {
                champNuits.setError("Le nombre de nuits doit être entier et supérieur à 0.");
                return;
            }
            double total = nVoyageurs * nNuits * prix;
            champResultat.setText(nVoyageurs + " voyageurs * " + nNuits + " nuits * " + prix + "$ = " + total + "$");
        });
    }
}