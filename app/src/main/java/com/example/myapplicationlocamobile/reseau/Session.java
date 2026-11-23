package com.example.myapplicationlocamobile.reseau;

import android.content.Context;

public final class Session {
    public static void enregistrer(Context context, String jeton, String nom) {
        context.getSharedPreferences("session", Context.MODE_PRIVATE).edit().putString("jeton", jeton).putString("nom", nom).apply();
    }
    public static String jeton(Context context) {
        return context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("jeton", null);
    }
}
