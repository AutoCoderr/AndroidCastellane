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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ppe.controleur.Controleur;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class Infos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            setTitle("Informations "+StockSession.infos.getString("prenom")+" "+StockSession.infos.getString("nom"));
        } catch (org.json.JSONException e) {
            setTitle("Informations d'un utilisateur");
        }

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
            submenu = menu.addSubMenu("Espace Directeur");
            submenu.add("Rendez-vous");
            submenu.add("Diplomes");
            submenu.add("Administration");
            submenu.add("Mes infos");

            navView.invalidate();
        }
        Button btArriere = (Button) findViewById(R.id.btArriere);
        btArriere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Infos.this, Admin.class);
                startActivity(i);
            }
        });
        this.displayInfos();
    }

    private void displayInfos() {
        HashMap<String,String> nameType = new HashMap<>();
        nameType.put("salarie","Salarié(e)");
        nameType.put("etudiant","Etudiant");
        nameType.put("moniteur","Moniteur");

        LinearLayout ll = (LinearLayout) this.findViewById(R.id.infosLayout);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;
        try {
            JSONObject fiche = StockSession.infos;
            TextView msg = new TextView(this);
            msg.setLayoutParams(lparams);
            msg.setTextSize(23);
            msg.setTextColor(Color.parseColor("#000000"));
            msg.setText(fiche.getString("prenom")+" "+fiche.getString("nom")+" ("+nameType.get(fiche.getString("type"))+")\n");
            ll.addView(msg);
            msg = new TextView(this);
            msg.setLayoutParams(lparams);
            msg.setTextColor(Color.parseColor("#000000"));
            msg.setText("Adresse mail : " + fiche.getString("mail"));
            ll.addView(msg);
            if (fiche.getString("type").equals("salarie") | fiche.getString("type").equals("etudiant")) {
                msg = new TextView(this);
                msg.setLayoutParams(lparams);
                msg.setTextColor(Color.parseColor("#000000"));
                msg.setText("Adresse : " + fiche.getString("addr"));
                ll.addView(msg);

                msg = new TextView(this);
                msg.setLayoutParams(lparams);
                msg.setTextColor(Color.parseColor("#000000"));
                msg.setText("Date de naissance : " + fiche.getString("dateN"));
                ll.addView(msg);

                msg = new TextView(this);
                msg.setLayoutParams(lparams);
                msg.setTextColor(Color.parseColor("#000000"));
                msg.setText("Numéro de télephone : " + fiche.getString("numtel"));
                ll.addView(msg);

                msg = new TextView(this);
                msg.setLayoutParams(lparams);
                msg.setTextColor(Color.parseColor("#000000"));
                msg.setText("Date d'inscription : " + fiche.getString("dateI"));
                ll.addView(msg);

                msg = new TextView(this);
                msg.setLayoutParams(lparams);
                msg.setTextColor(Color.parseColor("#000000"));
                msg.setText("Mode de facturation : " + fiche.getString("facturation"));
                ll.addView(msg);
                if (fiche.getString("type").equals("etudiant")) {
                    msg = new TextView(this);
                    msg.setLayoutParams(lparams);
                    msg.setTextColor(Color.parseColor("#000000"));
                    msg.setText("Niveau d'étude : " + fiche.getString("etude"));
                    ll.addView(msg);

                    msg = new TextView(this);
                    msg.setLayoutParams(lparams);
                    msg.setTextColor(Color.parseColor("#000000"));
                    msg.setText("Réduction : " + fiche.getString("reduction"));
                    ll.addView(msg);
                } else if (fiche.getString("type").equals("salarie")) {
                    msg = new TextView(this);
                    msg.setLayoutParams(lparams);
                    msg.setTextColor(Color.parseColor("#000000"));
                    msg.setText("Entreprise : " + fiche.getString("entreprise"));
                    ll.addView(msg);
                }
            } else if (fiche.getString("type").equals("moniteur")) {
                msg = new TextView(this);
                msg.setLayoutParams(lparams);
                msg.setTextColor(Color.parseColor("#000000"));
                msg.setText("Date d'embauche : " + fiche.getString("dateE"));
                ll.addView(msg);
            }
        } catch(org.json.JSONException e) {

        }
        //StockSession.infos = null;
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
        getMenuInflater().inflate(R.menu.infos, menu);
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
                Intent i = new Intent(Infos.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(Infos.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(Infos.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(Infos.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(Infos.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(Infos.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(Infos.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
