package com.example.ppe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Diplomes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private JSONArray diplomes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diplomes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Diplômes");

        Controleur controleur = new Controleur(Diplomes.this, Diplomes.this);
        controleur.getDiplomes();

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
        Button btSearchDiplome = (Button) findViewById(R.id.btSearchDiplome);
        btSearchDiplome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Diplomes.this);

                alert.setTitle("Recherche par mot clé");
                alert.setMessage("Quel mot clé?");

                final EditText input = new EditText(Diplomes.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        Diplomes.this.searchByKey(value);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }

    private String toMin(String word) {
        String[] alphabetMin = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        String[] alphabetMaj = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        for(int i=0;i<alphabetMaj.length;i++) {
            word = word.replace(alphabetMaj[i],alphabetMin[i]);
        }
        return word;
    }

    public void diplomesGetted(JSONArray diplomes) {
        try {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
        } catch (NullPointerException e) {

        }
        LinearLayout ll =(LinearLayout) this.findViewById(R.id.diplomeLayout);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;

        if (diplomes.length() == 0) {
            TextView msg = new TextView(this);
            msg.setText("Aucun diplome");
            msg.setLayoutParams(lparams);
            msg.setTextSize(23);
            ll.addView(msg);
            return;
        }
            try {
                if (StockSession.perm.equals("user")) {
                    for (int i = 0; i < diplomes.length(); i++) {
                        JSONObject current = new JSONObject(diplomes.get(i).toString());
                        TextView diplome = new TextView(this);
                        if (i == 0) {
                            if (current.getString("resultat").equals("1")) {
                                diplome.setText("Vous avez le code");
                            } else {
                                diplome.setText("Vous n'avez pas le code");
                            }
                        } else {
                            if (!current.getString("resultat").equals("null")) {
                                diplome.setText(current.getString("exam") + " :    " + current.getString("resultat"));
                                if (!current.getString("exam").equals("Quiz")) {
                                    diplome.setText(diplome.getText()+"/20");
                                }
                            } else {
                                diplome.setText(current.getString("exam") + " :    non noté");
                            }
                        }
                        diplome.setLayoutParams(lparams);
                        diplome.setTextSize(17);
                        ll.addView(diplome);
                    }
                } else if (StockSession.perm.equals("admin") | StockSession.perm.equals("superadmin")) {
                    boolean firstDisplayed = true;
                    boolean displayed;
                    ArrayList<String> ClientsDisplayed = new ArrayList<>();
                    for (int i = 0; i < diplomes.length(); i++) {
                        final JSONObject current = new JSONObject(diplomes.get(i).toString());
                        displayed = false;
                        for(int j=0;j<ClientsDisplayed.size();j++) {
                            if (ClientsDisplayed.get(j).equals(current.getString("client"))) {
                                displayed = true;
                                break;
                            }
                        }
                        if (displayed == false) {
                            ClientsDisplayed.add(current.getString("client"));
                            TextView client = new TextView(this);
                            if (firstDisplayed) {
                                client.setText(current.getString("client") + " : ");
                                firstDisplayed = false;
                            } else {
                                client.setText("\n-------------------------------------------");
                                client.setLayoutParams(lparams);
                                ll.addView(client);
                                client = new TextView(this);
                                client.setText(current.getString("client") + " : ");
                            }
                            client.setTextSize(23);
                            client.setLayoutParams(lparams);
                            ll.addView(client);
                        }
                        final TextView diplome = new TextView(this);
                        if (!current.getString("typeId").equals("1")) {
                            if (!current.getString("resultat").equals("null")) {
                                diplome.setText(current.getString("exam") + " :    " + current.getString("resultat"));
                                if (!current.getString("typeId").equals("0")) {
                                    diplome.setText(diplome.getText()+"/20");
                                }
                            } else {
                                diplome.setText(current.getString("exam") + " :    non noté");
                            }

                            diplome.setLayoutParams(lparams);
                            diplome.setTextSize(17);
                            ll.addView(diplome);
                            Button changeNote = new Button(this);

                            changeNote.setText("Changer la note");
                            changeNote.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Controleur controleur = new Controleur(Diplomes.this, Diplomes.this);
                                    try {
                                        if (!current.getString("typeId").equals("0")) {
                                            controleur.changeNote(current.getString("id"),diplome);
                                        } else {
                                            controleur.changeNoteQuiz(current.getString("id"),diplome);
                                        }
                                    } catch (org.json.JSONException e) {

                                    }
                                }
                            });
                            ll.addView(changeNote);
                        } else {
                            diplome.setLayoutParams(lparams);
                            diplome.setTextSize(17);
                            final Button btAlterCode = new Button(this);
                            if (current.getString("resultat").equals("1")) {
                                diplome.setText("A le code");
                                btAlterCode.setText("Enlever");
                                btAlterCode.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Controleur controleur = new Controleur(Diplomes.this, Diplomes.this);
                                        try {
                                            controleur.alterCode("0", current.getString("idClient"),diplome,btAlterCode);
                                        } catch(org.json.JSONException e) {

                                        }
                                    }
                                });
                            } else {
                                diplome.setText("N'a pas le code");
                                btAlterCode.setText("Donner");
                                btAlterCode.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Controleur controleur = new Controleur(Diplomes.this, Diplomes.this);
                                        try {
                                            controleur.alterCode("1", current.getString("idClient"),diplome,btAlterCode);
                                        } catch(org.json.JSONException e) {

                                        }
                                    }
                                });
                            }
                            ll.addView(diplome);
                            ll.addView(btAlterCode);
                        }
                    }
                }
            } catch (org.json.JSONException e) {

            }
            if (this.diplomes == null) {
                this.diplomes = diplomes;
            }
    }

    private void searchByKey(String word){
        word = this.toMin(word);
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.diplomeLayout);
        ll.removeAllViews();
        if (word.equals("")) {
            this.diplomesGetted(this.diplomes);
        } else {
            try {
                JSONArray diplomesTmp = new JSONArray();
                for (int i = 0; i < this.diplomes.length(); i++) {
                    JSONObject current = new JSONObject(this.diplomes.get(i).toString());
                    if (!this.toMin(current.getString("client")).replace(word, "").equals(this.toMin(current.getString("client")))) {
                        diplomesTmp.put(current);
                    }
                }
                this.diplomesGetted(diplomesTmp);
            } catch (org.json.JSONException e) {

            }
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
        getMenuInflater().inflate(R.menu.diplomes, menu);
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
            if (StockSession.perm.equals("user")) {
                try {
                    Button btSearchDiplome = (Button) findViewById(R.id.btSearchDiplome);
                    ((ViewGroup) btSearchDiplome.getParent()).removeView(btSearchDiplome);
                } catch (NullPointerException e) {

                }
            }
        }
        LinearLayout ll = (LinearLayout) findViewById(R.id.diplomeLayout);
        if (ll.getChildCount() == 0) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            progressBar.setId(R.id.progressBar);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.gravity = Gravity.CENTER;
            lparams.setMargins(0, 125, 0, 0);
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
                Intent i = new Intent(Diplomes.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(Diplomes.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(Diplomes.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(Diplomes.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(Diplomes.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(Diplomes.this, Fiche.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(Diplomes.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
