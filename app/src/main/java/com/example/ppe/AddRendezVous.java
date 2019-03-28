package com.example.ppe;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ppe.controleur.Controleur;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRendezVous extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HashMap<String, String> lecons = new HashMap<>();
    private HashMap<String, String> exams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rendez_vous);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Controleur controleur = new Controleur(AddRendezVous.this, AddRendezVous.this);
        controleur.getExams();

        setTitle("Ajouter un rendez vous");

        Button btArriere = (Button) findViewById(R.id.btArriere);
        btArriere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddRendezVous.this, ListRendezVous.class);
                startActivity(i);
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

    public void rdvSended() {
        Intent i = new Intent(AddRendezVous.this, ListRendezVous.class);
        startActivity(i);
    }

    public void examGetted(JSONArray reponse) {
        JSONObject current;
        try {
            for (int i = 0; i < reponse.length(); i++) {
                current = new JSONObject(reponse.get(i).toString());
                this.exams.put(current.getString("nom")+" ("+current.getString("prix")+"€)",current.getString("id"));
            }
        } catch(org.json.JSONException e) {

        }
        Controleur controleur = new Controleur(AddRendezVous.this, AddRendezVous.this);
        controleur.getLecons();
    }

    public void courGetted(JSONArray reponse) {
        JSONObject current;
        try {
            for (int i = 0; i < reponse.length(); i++) {
                current = new JSONObject(reponse.get(i).toString());
                this.lecons.put(current.getString("nom")+" ("+current.getString("tarif")+"€/h)",current.getString("id"));
            }
        } catch(org.json.JSONException e) {

        }

        String[] items = new String[]{"Cours","Exam"};

        final Spinner dropdownType = (Spinner) findViewById(R.id.type);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdownType.setAdapter(adapter);

        dropdownType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int i;
                try {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    ((ViewGroup) progressBar.getParent()).removeView(progressBar);
                } catch (NullPointerException e) {

                }
                LinearLayout ll = (LinearLayout) findViewById(R.id.formuLayout);
                //LinearLayout.LayoutParams btDeleteParam = new LinearLayout.LayoutParams(500, 100);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(500, 150);
                lparams.gravity = Gravity.CENTER;
                //lparams.setMargins(0, 0, 80, 0);

                try {
                    Button btSendRdv = (Button) findViewById(R.id.btSendRdv);
                    ((ViewGroup) btSendRdv.getParent()).removeView(btSendRdv);
                } catch(NullPointerException e) {

                }


                //Spinner spinner = (Spinner) findViewById(R.id.type);
                if (dropdownType.getSelectedItem().toString().equals("Cours")) {
                    try {
                        Spinner typeExam = (Spinner) findViewById(R.id.typeExam);
                        ((ViewGroup) typeExam.getParent()).removeView(typeExam);
                    } catch(NullPointerException e) {

                    }

                    EditText duree = new EditText(AddRendezVous.this);
                    duree.setHint("Durée");
                    duree.setId(R.id.dureeRdv);
                    duree.setLayoutParams(lparams);
                    ll.addView(duree);

                    Spinner leconRdv = new Spinner(AddRendezVous.this);

                    String[] items = new String[AddRendezVous.this.lecons.size()];
                    i = 0;
                    for (Map.Entry lecon : AddRendezVous.this.lecons.entrySet()) {
                        items[i] = lecon.getKey().toString();
                        i += 1;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddRendezVous.this, android.R.layout.simple_spinner_dropdown_item, items);

                    leconRdv.setAdapter(adapter);
                    leconRdv.setId(R.id.leconRdv);
                    leconRdv.setLayoutParams(lparams);
                    ll.addView(leconRdv);
                } else if (dropdownType.getSelectedItem().toString().equals("Exam")) {
                    try {
                        Spinner leconRdv = (Spinner) findViewById(R.id.leconRdv);
                        ((ViewGroup) leconRdv.getParent()).removeView(leconRdv);
                        EditText duree = (EditText) findViewById(R.id.dureeRdv);
                        ((ViewGroup) duree.getParent()).removeView(duree);
                    } catch(NullPointerException e) {

                    }

                    Spinner typeExam = new Spinner(AddRendezVous.this);

                    String[] items = new String[AddRendezVous.this.exams.size()];
                    i = 0;
                    for (Map.Entry exam : AddRendezVous.this.exams.entrySet()) {
                        if (i == 0)  {
                            items[1] = exam.getKey().toString();
                        } else if (i == 1) {
                            items[0] = exam.getKey().toString();
                        } else {
                            items[i] = exam.getKey().toString();
                        }
                        i += 1;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddRendezVous.this, android.R.layout.simple_spinner_dropdown_item, items);

                    typeExam.setAdapter(adapter);
                    typeExam.setId(R.id.typeExam);
                    typeExam.setLayoutParams(lparams);
                    ll.addView(typeExam);
                }
                Button btSendRdv = new Button(AddRendezVous.this);
                btSendRdv.setText("Envoyer");
                btSendRdv.setId(R.id.btSendRdv);
                btSendRdv.setLayoutParams(lparams);
                ll.addView(btSendRdv);
                btSendRdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = (String)((EditText) findViewById(R.id.date)).getText().toString();
                        String heureD = (String)((EditText) findViewById(R.id.heureD)).getText().toString();
                        String type = (String)((Spinner) findViewById(R.id.type)).getSelectedItem();
                        ArrayList<String> typeVars = new ArrayList<>();

                        if (type.equals("Cours")) {
                            typeVars.add((String)((EditText) findViewById(R.id.dureeRdv)).getText().toString());
                            typeVars.add(AddRendezVous.this.lecons.get((String)((Spinner) findViewById(R.id.leconRdv)).getSelectedItem()));
                        } else if (type.equals("Exam")) {
                            typeVars.add(AddRendezVous.this.exams.get((String)((Spinner) findViewById(R.id.typeExam)).getSelectedItem()));
                        }
                        Controleur controleur = new Controleur(AddRendezVous.this, AddRendezVous.this);
                        controleur.sendRdv(date, heureD, type, typeVars);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
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
        getMenuInflater().inflate(R.menu.add_rendez_vous, menu);
        LinearLayout ll = (LinearLayout) findViewById(R.id.formuLayout);
        if (ll.getChildCount() == 0) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            progressBar.setId(R.id.progressBar);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.gravity = Gravity.CENTER;
            lparams.setMargins(0,35,50,0);
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
                Intent i = new Intent(AddRendezVous.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(AddRendezVous.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(AddRendezVous.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(AddRendezVous.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(AddRendezVous.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(AddRendezVous.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(AddRendezVous.this, Fiche.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(AddRendezVous.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
