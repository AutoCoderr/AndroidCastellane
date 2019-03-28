package com.example.ppe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.ppe.controleur.Controleur;

public class AddMoniteur extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_moniteur);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Ajouter moniteur");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Button btArriere = (Button) findViewById(R.id.btArriere);
        btArriere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddMoniteur.this, Admin.class);
                startActivity(i);
            }
        });

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

        Button btAddMoniteur = (Button) findViewById(R.id.btAddMoniteur);
        btAddMoniteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtPrenom = (EditText) findViewById(R.id.txtPrenom);
                String prenom = txtPrenom.getText().toString();

                EditText txtNom = (EditText) findViewById(R.id.txtNom);
                String nom = txtNom.getText().toString();

                EditText txtPassword1 = (EditText) findViewById(R.id.txtPassword1);
                String password1 = txtPassword1.getText().toString();

                EditText txtPassword2 = (EditText) findViewById(R.id.txtPassword2);
                String password2 = txtPassword2.getText().toString();

                EditText txtMail = (EditText) findViewById(R.id.txtMail);
                String mail = txtMail.getText().toString();

                if (!password1.equals(password2))  {
                    AlertDialog alertDialog = new AlertDialog.Builder(AddMoniteur.this).create();
                    alertDialog.setTitle("Erreur : ");
                    alertDialog.setMessage("Les mots de passe ne correspondent pas");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    Controleur controleur = new Controleur(AddMoniteur.this, AddMoniteur.this);
                    controleur.addMoniteur(prenom, nom, password1, mail);
                }
            }
        });
    }

    public void moniteurAdded() {
        Intent i = new Intent(AddMoniteur.this, Admin.class);
        startActivity(i);
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
        getMenuInflater().inflate(R.menu.add_moniteur, menu);
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
                Intent i = new Intent(AddMoniteur.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(AddMoniteur.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(AddMoniteur.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(AddMoniteur.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(AddMoniteur.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(AddMoniteur.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(AddMoniteur.this, Fiche.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(AddMoniteur.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
