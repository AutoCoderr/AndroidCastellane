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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class Register extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Inscription");
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // do { // Liste déroulante : TYPE
        Spinner dropdownType = (Spinner) findViewById(R.id.type);

        String[] items = new String[]{"étudiant(e)", "salarié(e)"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdownType.setAdapter(adapter);
        // }

        Button btRegisterSuite;
        btRegisterSuite = (Button) findViewById(R.id.btRegisterSuite);
        btRegisterSuite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean error = false;
                EditText TxtPrenom = (EditText) findViewById(R.id.prenom);
                String prenom = TxtPrenom.getText().toString();
                EditText TxtNom = (EditText) findViewById(R.id.nom);
                String nom = TxtNom.getText().toString();
                Spinner spinnerType = (Spinner) findViewById(R.id.type);
                String type = spinnerType.getSelectedItem().toString();

                String niveau = "";
                String reduction = "";
                String entreprise = "";
                if (type.equals("étudiant(e)")) {
                    EditText TxtNiveau = (EditText) findViewById(R.id.niveau);
                    niveau = TxtNiveau.getText().toString();
                    EditText TxtReduction = (EditText) findViewById(R.id.reduction);
                    reduction = TxtReduction.getText().toString();
                } else if (type.equals("salarié(e)")) {
                    EditText TxtEntreprise = (EditText) findViewById(R.id.entreprise);
                    entreprise = TxtEntreprise.getText().toString();
                }

                if (prenom.equals("") | nom.equals("")) {
                    error = true;
                }

                if (type.equals("étudiant(e)") & (niveau.equals("") | reduction.equals(""))) {
                    error = true;
                } else if (type.equals("salarié(e)") & (entreprise.equals(""))) {
                    error = true;
                }

                if (error) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                    alertDialog.setTitle("Champs non remplis");
                    alertDialog.setMessage("Certain champs ne sont pas remplis");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                Intent intent = new Intent(Register.this, RegisterSuite.class);

                intent.putExtra("prenom", prenom);
                intent.putExtra("nom", nom);
                intent.putExtra("type", type);
                if (type.equals("étudiant(e)")) {
                    intent.putExtra("niveau", niveau);
                    intent.putExtra("reduction", reduction);
                } else if (type.equals("salarié(e)")) {
                    intent.putExtra("entreprise", entreprise);
                }
                startActivity(intent);
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
        getMenuInflater().inflate(R.menu.register, menu);
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
                Intent i = new Intent(Register.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(Register.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {

        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(Register.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(Register.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(Register.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(Register.this, Fiche.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(Register.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
