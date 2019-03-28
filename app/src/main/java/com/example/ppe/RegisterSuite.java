package com.example.ppe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class RegisterSuite<toggle> extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Inscription - Suite");
        setContentView(R.layout.activity_register_suite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        Button btRegister = (Button) findViewById(R.id.btRegisterSend);

        btRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText TxtAdresse = (EditText) findViewById(R.id.adresse);
                String adresse = TxtAdresse.getText().toString();

                EditText TxtDateN = (EditText) findViewById(R.id.dateN);
                String dateN = TxtDateN.getText().toString();

                EditText TxtNumTel = (EditText) findViewById(R.id.numTel);
                String numTel = TxtNumTel.getText().toString();

                EditText TxtFacturation = (EditText) findViewById(R.id.facturation);
                String facturation = TxtFacturation.getText().toString();

                EditText TxtPassword1 = (EditText) findViewById(R.id.password);
                String password1 = TxtPassword1.getText().toString();

                EditText TxtPassword2 = (EditText) findViewById(R.id.password2);
                String password2 = TxtPassword2.getText().toString();

                EditText TxtEmail = (EditText) findViewById(R.id.email);
                String email = TxtEmail.getText().toString();

                if (adresse.equals("") | dateN.equals("") | numTel.equals("") | facturation.equals("") | password1.equals("") | email.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterSuite.this).create();
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

                if (!password1.equals(password2)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterSuite.this).create();
                    alertDialog.setTitle("Mots de passe différents");
                    alertDialog.setMessage("Les mots de passe ne correspondent pas");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                    return;
                }
                Intent intent = getIntent();

                String prenom = intent.getStringExtra("prenom");
                String nom = intent.getStringExtra("nom");
                String type = intent.getStringExtra("type");

                ArrayList<String> typeVars  = new ArrayList<>();
                String niveau;
                String reduction;
                String entreprise;
                if (type.equals("étudiant(e)")) {
                    typeVars.add(intent.getStringExtra("niveau"));
                    typeVars.add(intent.getStringExtra("reduction"));
                    intent.removeExtra("niveau");
                    intent.removeExtra("reduction");
                } else if (type.equals("salarié(e)")) {
                    typeVars.add(intent.getStringExtra("entreprise"));
                    intent.removeExtra("entreprise");
                }

                Controleur controleur = new Controleur(RegisterSuite.this, RegisterSuite.this);
                controleur.sendRegister(prenom, nom, type, typeVars, adresse, dateN, numTel, facturation, password1, email);
                //controleur.sendTEST();

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

    public void RegisterCallback() {
        Intent i = new Intent(RegisterSuite.this, MainActivity.class);
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
        getMenuInflater().inflate(R.menu.register_suite, menu);
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
                Intent i = new Intent(RegisterSuite.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(RegisterSuite.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(RegisterSuite.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(RegisterSuite.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(RegisterSuite.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(RegisterSuite.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(RegisterSuite.this, Fiche.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(RegisterSuite.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
