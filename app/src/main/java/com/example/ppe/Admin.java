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

import java.util.HashMap;

public class Admin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private JSONArray users = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Controleur controleur = new Controleur(this, this);

        controleur.getUsersList();

        setTitle("Administration");

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

        Button btSearchDiplome = (Button) findViewById(R.id.btSearchAdmin);
        btSearchDiplome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Admin.this);

                alert.setTitle("Recherche par mot clé");
                alert.setMessage("Quel mot clé?");

                final EditText input = new EditText(Admin.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        Admin.this.searchByKey(value);
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

        FloatingActionButton btAddMoniteur = (FloatingActionButton) findViewById(R.id.btAddMoniteur);
        btAddMoniteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Admin.this, AddMoniteur.class);
                startActivity(i);
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

    private void searchByKey(String word){
        word = this.toMin(word);
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.adminLayout);
        ll.removeAllViews();
        if (word.equals("")) {
            this.usersGetted(this.users);
        } else {
            try {
                JSONArray usersTmp = new JSONArray();
                for (int i = 0; i < this.users.length(); i++) {
                    JSONObject current = new JSONObject(this.users.get(i).toString());
                    System.out.println(this.toMin(current.getString("prenom")).replace(word, "")+" != "+this.toMin(current.getString("prenom")));
                    if (!this.toMin(current.getString("prenom")).replace(word, "").equals(this.toMin(current.getString("prenom"))) |
                        !this.toMin(current.getString("nom")).replace(word, "").equals(this.toMin(current.getString("nom")))) {
                        usersTmp.put(current);
                    }
                }
                this.usersGetted(usersTmp);
            } catch (org.json.JSONException e) {

            }
        }
    }

    public void usersGetted(JSONArray users) {
        try {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
        } catch (NullPointerException e) {

        }
        HashMap<String,String> types = new HashMap<>();
        types.put("salarie","Salarié(e)");
        types.put("etudiant","Etudiant(e)");
        types.put("moniteur","Moniteur");

        LinearLayout ll = (LinearLayout) findViewById(R.id.adminLayout);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;
        if (users.length() == 0) {
            TextView msg = new TextView(this);
            msg.setText("Aucun utilisateur");
            msg.setLayoutParams(lparams);
            msg.setTextSize(23);
            ll.addView(msg);
            return;
        }
        try {
            for (int i = 0; i < users.length(); i++) {
                final JSONObject current = new JSONObject(users.get(i).toString());
                TextView txtUser = new TextView(this);
                String msgUser = current.getString("prenom")+" "+current.getString("nom");
                msgUser += " ("+types.get(current.getString("type"))+")";
                txtUser.setText(msgUser);
                txtUser.setLayoutParams(lparams);
                ll.addView(txtUser);

                LinearLayout llB = new LinearLayout(this);
                llB.setLayoutParams(lparams);
                llB.setOrientation(LinearLayout.HORIZONTAL);
                ll.addView(llB);

                final Button btBanUnBan = new Button(this);
                LinearLayout.LayoutParams btParam = new LinearLayout.LayoutParams(300, 150);
                btParam.gravity = Gravity.CENTER;
                if (current.getString("banned").equals("1")) {
                    btBanUnBan.setText("Dé-bannir");
                    btBanUnBan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur controleur = new Controleur(Admin.this, Admin.this);
                            try {
                                controleur.banUnban(current.getString("id"),"0", btBanUnBan);
                            } catch (org.json.JSONException e) {

                            }
                        }
                    });
                } else {
                    btBanUnBan.setText("Bannir");
                    btBanUnBan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur controleur = new Controleur(Admin.this, Admin.this);
                            try {
                                controleur.banUnban(current.getString("id"),"1", btBanUnBan);
                            } catch (org.json.JSONException e) {

                            }
                        }
                    });
                }
                btBanUnBan.setLayoutParams(btParam);
                llB.addView(btBanUnBan);

                Button btInfo = new Button(this);
                btInfo.setLayoutParams(btParam);
                btInfo.setText("Voir infos");
                llB.addView(btInfo);
                btInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StockSession.infos = current;
                        Intent i = new Intent(Admin.this, Infos.class);
                        startActivity(i);
                    }
                });
            }
            if (this.users == null) {
                this.users = users;
            }
        } catch(org.json.JSONException e) {

        }
    }

    public void banUnbanned() {
        Intent i = new Intent(Admin.this, Admin.class);
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
        LinearLayout ll = (LinearLayout) findViewById(R.id.adminLayout);
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
        getMenuInflater().inflate(R.menu.admin, menu);
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
                Intent i = new Intent(Admin.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(Admin.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(Admin.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(Admin.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Rendez-vous") {
            Intent i = new Intent(Admin.this, ListRendezVous.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(Admin.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(Admin.this, Fiche.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
