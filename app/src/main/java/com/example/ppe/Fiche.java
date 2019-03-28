package com.example.ppe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ppe.controleur.Controleur;

import org.json.JSONObject;

import java.util.HashMap;

public class Fiche extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Informations personnelles");

        if (!StockSession.perm.equals("superadmin")) {
            Controleur controleur = new Controleur(this, this);
            controleur.getFiche();
        } else {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ficheLayout);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.gravity = Gravity.CENTER;
            TextView msg = new TextView(this);
            msg.setText("\n\nVous êtes directeur");
            msg.setLayoutParams(lparams);
            msg.setTextColor(Color.parseColor("#000000"));
            ll.addView(msg);
        }

        Button btChangePasswd = (Button) findViewById(R.id.btChangePasswd);
        btChangePasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtMdp1 = (EditText) findViewById(R.id.changePasswd1);
                String mdp1 = txtMdp1.getText().toString();
                EditText txtMdp2 = (EditText) findViewById(R.id.changePasswd2);
                String mdp2 = txtMdp2.getText().toString();
                Controleur controleur = new Controleur(Fiche.this, Fiche.this);

                controleur.changePasswd(mdp1,mdp2);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        if (StockSession.connected) {
            Menu menu = navView.getMenu();

            MenuItem connectButton = (MenuItem) menu.findItem(R.id.nav_connect);
            connectButton.setTitle("Déconnexion");

            Menu submenu;

            if (StockSession.perm.equals("user")) {
                submenu = menu.addSubMenu("Espace client");
                submenu.add("Rendez-vous");
                submenu.add("Diplomes");
                submenu.add("Mes infos");
            } else if (StockSession.perm.equals("admin")) {
                submenu = menu.addSubMenu("Espace Moniteur");
                submenu.add("Rendez-vous");
                submenu.add("Diplomes");
                submenu.add("Mes infos");
            } else if (StockSession.perm.equals("superadmin")) {
                submenu = menu.addSubMenu("Espace Directeur");
                submenu.add("Rendez-vous");
                submenu.add("Diplomes");
                submenu.add("Administration");
                submenu.add("Mes infos");
            }

            navView.invalidate();
        }
    }

    public void passwordChanged() {
        EditText txtMdp1 = (EditText) findViewById(R.id.changePasswd1);
        EditText txtMdp2 = (EditText) findViewById(R.id.changePasswd2);
        txtMdp1.setText("");
        txtMdp2.setText("");
    }

    public void ficheGetted(JSONObject fiche) {
        try {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
        } catch (NullPointerException e) {

        }

        HashMap<String,String> nameType = new HashMap<>();
        nameType.put("salarie","Salarié(e)");
        nameType.put("etudiant","Etudiant");
        nameType.put("moniteur","Moniteur");
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.ficheLayout);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;
        TextView msg = null;
        try {
            msg = new TextView(Fiche.this);
            msg.setLayoutParams(lparams);
            msg.setText("Vous êtes "+nameType.get(StockSession.type)+"\n");
            msg.setTextColor(Color.parseColor("#000000"));
            ll.addView(msg);

            msg = new TextView(Fiche.this);
            msg.setLayoutParams(lparams);
            msg.setText("Adresse mail : "+fiche.getString("mail"));
            ll.addView(msg);
            if (StockSession.type.equals("salarie") | StockSession.type.equals("etudiant")) {
                msg = new TextView(Fiche.this);
                msg.setLayoutParams(lparams);
                msg.setText("Adresse : "+fiche.getString("addr"));
                ll.addView(msg);

                msg = new TextView(Fiche.this);
                msg.setLayoutParams(lparams);
                msg.setText("Date de naissance : "+fiche.getString("dateN"));
                ll.addView(msg);

                msg = new TextView(Fiche.this);
                msg.setLayoutParams(lparams);
                msg.setText("Numéro de télephone : "+fiche.getString("numtel"));
                ll.addView(msg);

                msg = new TextView(Fiche.this);
                msg.setLayoutParams(lparams);
                msg.setText("Date d'inscription : "+fiche.getString("dateI"));
                ll.addView(msg);

                msg = new TextView(Fiche.this);
                msg.setLayoutParams(lparams);
                msg.setText("Mode de facturation : "+fiche.getString("facturation"));
                ll.addView(msg);
                if (StockSession.type.equals("etudiant")) {
                    msg = new TextView(Fiche.this);
                    msg.setLayoutParams(lparams);
                    msg.setText("Niveau d'étude : "+fiche.getString("etude"));
                    ll.addView(msg);

                    msg = new TextView(Fiche.this);
                    msg.setLayoutParams(lparams);
                    msg.setText("Réduction : "+fiche.getString("reduction"));
                    ll.addView(msg);
                } else if (StockSession.type.equals("salarie")) {
                    msg = new TextView(Fiche.this);
                    msg.setLayoutParams(lparams);
                    msg.setText("Votre entreprise : "+fiche.getString("entreprise"));
                    ll.addView(msg);
                }
            } else if (StockSession.type.equals("moniteur")) {
                msg = new TextView(Fiche.this);
                msg.setLayoutParams(lparams);
                msg.setText("Votre date d'embauche : "+fiche.getString("dateE"));
                ll.addView(msg);
            }
        } catch(org.json.JSONException e) {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fiche, menu);
        if (StockSession.connected) {
            String type = "";
            if (StockSession.perm.equals("user")) {
                type = "Client(e)";
            } else if (StockSession.perm.equals("admin")) {
                type = "Moniteur";
            } else if (StockSession.perm.equals("superadmin")) {
                type = "Directeur";
            }
            TextView ifConnectText = (TextView) findViewById(R.id.ifConnected);
            ifConnectText.setText(StockSession.prenom+" "+StockSession.nom+" ("+type+")");
        }
        LinearLayout ll = (LinearLayout) findViewById(R.id.ficheLayout);
        if (ll.getChildCount() == 0) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            progressBar.setId(R.id.progressBar);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.gravity = Gravity.CENTER;
            lparams.width = 300;
            lparams.height = 300;
            progressBar.setLayoutParams(lparams);
            ll.addView(progressBar);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_connect) {
            if (!StockSession.connected) {
                Intent i = new Intent(Fiche.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(Fiche.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(Fiche.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(Fiche.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(Fiche.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(Fiche.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(Fiche.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
