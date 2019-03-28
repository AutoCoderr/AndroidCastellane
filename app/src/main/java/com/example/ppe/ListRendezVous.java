package com.example.ppe;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ppe.controleur.Controleur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListRendezVous extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean rdvSetted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Liste des rendez vous");

        Controleur controleur = new Controleur(this, this);

        controleur.getRdv(0);

        setContentView(R.layout.activity_list_rendez_vous);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    public void rdvGetted(JSONArray rdvs, final String index) {
        Button btDeleteRdv;
        Button btValidRdv;
        Button btDevalidRdv;
        Button btOtherRdv;
        this.rdvSetted = false;
        try {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
        } catch (NullPointerException e) {

        }
        try {
            btOtherRdv = (Button) findViewById(R.id.btOtherRdv);
            ((ViewGroup) btOtherRdv.getParent()).removeView(btOtherRdv);
        } catch (NullPointerException e) {

        }

        ArrayList<String> datesChoosed = new ArrayList<>();
        String rdvString;
        boolean choosed;
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.rdvsLayout);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (rdvs.length() == 0) {
            TextView message = new TextView(this);
            message.setText("Aucun rendez-vous");
            lparams.gravity = Gravity.CENTER;
            message.setLayoutParams(lparams);
            message.setTextSize(23);
            ll.addView(message);
            return;
        }
        try {

            for (int i = 0;i<rdvs.length();i++) {
                final JSONObject current = new JSONObject(rdvs.get(i).toString());
                final String currentDate = current.getString("date");
                choosed = false;
                for (int j=0;j<datesChoosed.size();j++) {
                    if (datesChoosed.get(j).equals(currentDate)) {
                        choosed = true;
                        break;
                    }
                }
                if (choosed == false) {
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(lparams);
                    if (rdvSetted) {
                        tv.setText("\n"+currentDate + " :");
                    } else {
                        tv.setText(currentDate + " :");
                        this.rdvSetted = true;
                    }
                    tv.setTextSize(23);
                    ll.addView(tv);
                    datesChoosed.add(currentDate);
                }

                TextView tv = new TextView(this);
                tv.setTextSize(15);
                tv.setLayoutParams(lparams);
                rdvString = current.getString("heureD")+" - "+current.getString("heureF")+" : ";
                if (current.getString("state").equals("occuped")) {
                    rdvString += " Occupé";
                } else {
                    if (current.getString("type").equals("Planning")) {
                        rdvString += "sur " + current.getString("lecon");
                    } else if (current.getString("type").equals("Exam")) {
                        rdvString += "Pour le " + current.getString("exam");
                    }
                    if (StockSession.perm.equals("user")) {
                        rdvString += " à "+current.getString("prix")+"€";
                        if (current.getString("state").equals("validated")) {
                            rdvString += "\n\t\tavec " + current.getString("moniteur");
                        } else if (current.getString("state").equals("unvalidated")) {
                            rdvString += "\n\t\tnon validé";
                        }
                    } else if (StockSession.perm.equals("admin")) {
                        rdvString += "\n\t\tdemandé par "+current.getString("client")+" ("+current.getString("prix")+"€)";
                        if (current.getString("state").equals("validated")) {
                            rdvString += "\n\t\t(validé par vous)";
                        } else if(current.getString("state").equals("unvalidated")) {
                            rdvString += "\n\t\t(non validé)";
                        }
                    } else if (StockSession.perm.equals("superadmin")) {
                        rdvString += "\n\t\tdemandé par "+current.getString("client")+" ("+current.getString("prix")+"€)";
                        if (current.getString("state").equals("validated")) {
                            rdvString += "\n\t\t(validé par "+current.getString("moniteur")+")";
                        } else if(current.getString("state").equals("unvalidated")) {
                            rdvString += "\n\t\t(non validé)";
                        }
                    }
                }
                tv.setText("\t\t"+rdvString);
                ll.addView(tv);

                if (StockSession.perm.equals("user")) {
                    btDeleteRdv = new Button(this);
                    LinearLayout.LayoutParams btDeleteParam = new LinearLayout.LayoutParams(500, 150);
                    btDeleteParam.gravity = Gravity.CENTER;
                    btDeleteRdv.setLayoutParams(btDeleteParam);
                    btDeleteRdv.setText("Supprimer");
                    btDeleteRdv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                            try {
                                controleur.deleteRdv(current.getString("id"), current.getString("type"));
                            } catch (org.json.JSONException e) {

                            }
                        }
                    });
                    ll.addView(btDeleteRdv);
                } else if (StockSession.perm.equals("admin")) {
                    if (current.getString("state").equals("unvalidated")) {
                        btValidRdv = new Button(this);
                        LinearLayout.LayoutParams ValidRdvParam = new LinearLayout.LayoutParams(250, 150);
                        ValidRdvParam.setMargins(0, 0, 175, 0);
                        ValidRdvParam.gravity = Gravity.CENTER;
                        btValidRdv.setLayoutParams(ValidRdvParam);
                        btValidRdv.setText("Valider");
                        btValidRdv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                                try {
                                    controleur.validRdv(current.getString("id"), current.getString("type"));
                                } catch(org.json.JSONException e) {

                                }
                            }
                        });
                        ll.addView(btValidRdv);
                    } else if (current.getString("state").equals("validated")) {
                        btDevalidRdv = new Button(this);
                        LinearLayout.LayoutParams DevalidRdvParam = new LinearLayout.LayoutParams(310, 150);
                        DevalidRdvParam.setMargins(0, 0, 175, 0);
                        DevalidRdvParam.gravity = Gravity.CENTER;
                        btDevalidRdv.setLayoutParams(DevalidRdvParam);
                        btDevalidRdv.setText("Dé-valider");
                        btDevalidRdv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                                try {
                                    controleur.devalidRdv(current.getString("id"), current.getString("type"));
                                } catch(org.json.JSONException e) {

                                }
                            }
                        });
                        ll.addView(btDevalidRdv);
                    }
                } else if (StockSession.perm.equals("superadmin")) {
                    LinearLayout.LayoutParams lBparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lBparams.gravity = Gravity.CENTER;
                    LinearLayout llB = new LinearLayout(this);
                    llB.setOrientation(LinearLayout.HORIZONTAL);
                    lBparams.setMargins(0,0,0,0);
                    llB.setLayoutParams(lBparams);
                    ll.addView(llB);


                    btDeleteRdv = new Button(this);
                    LinearLayout.LayoutParams btDeleteParam = new LinearLayout.LayoutParams(310, 150);
                    btDeleteParam.gravity = Gravity.CENTER;
                    btDeleteRdv.setLayoutParams(btDeleteParam);
                    btDeleteRdv.setText("Supprimer");
                    btDeleteRdv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                            try {
                                controleur.deleteRdv(current.getString("id"), current.getString("type"));
                            } catch (org.json.JSONException e) {

                            }
                        }
                    });
                    llB.addView(btDeleteRdv);
                    if (current.getString("state").equals("validated")) {
                        btDevalidRdv = new Button(this);
                        LinearLayout.LayoutParams DevalidRdvParam = new LinearLayout.LayoutParams(310, 150);
                        DevalidRdvParam.setMargins(0, 0, 0, 0);
                        DevalidRdvParam.gravity = Gravity.CENTER;
                        btDevalidRdv.setLayoutParams(DevalidRdvParam);
                        btDevalidRdv.setText("Dé-valider");
                        btDevalidRdv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                                try {
                                    controleur.devalidRdv(current.getString("id"), current.getString("type"));
                                } catch(org.json.JSONException e) {

                                }
                            }
                        });
                        llB.addView(btDevalidRdv);
                    }
                }
            }
            if (!index.equals("none")) {
                btOtherRdv = new Button(this);
                btOtherRdv.setId(R.id.btOtherRdv);
                btOtherRdv.setText("Afficher plus");
                ll.addView(btOtherRdv);
                btOtherRdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                        controleur.getRdv(Integer.valueOf(index));
                    }
                });
            }
        } catch (JSONException e) {

        }
    }

    public void rdvValidedOrUnvalided() {
        Intent i = new Intent(ListRendezVous.this, ListRendezVous.class);
        startActivity(i);
    }

    public void rdvDeleted() {
        Intent i = new Intent(ListRendezVous.this, ListRendezVous.class);
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
        FloatingActionButton btAddRdv;
        btAddRdv = (FloatingActionButton) findViewById(R.id.btAddRdv);
        if (StockSession.perm.equals("user")) {
            btAddRdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ListRendezVous.this, AddRendezVous.class);
                    startActivity(i);
                }
            });
        } else {
            ((ViewGroup) btAddRdv.getParent()).removeView(btAddRdv);
        }
        FloatingActionButton btRefreshRdv;
        btRefreshRdv = (FloatingActionButton) findViewById(R.id.btRefreshRdv);
        btRefreshRdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ll = (LinearLayout) ListRendezVous.this.findViewById(R.id.rdvsLayout);
                ll.removeAllViews();
                ProgressBar progressBar = new ProgressBar(ListRendezVous.this, null, android.R.attr.progressBarStyleSmall);
                progressBar.setId(R.id.progressBar);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.gravity = Gravity.CENTER;
                lparams.setMargins(0,300,0,0);
                lparams.width = 300;
                lparams.height = 300;
                progressBar.setLayoutParams(lparams);
                ll.addView(progressBar);

                Controleur controleur = new Controleur(ListRendezVous.this, ListRendezVous.this);
                controleur.getRdv(0);
            }
        });
        LinearLayout ll = (LinearLayout) ListRendezVous.this.findViewById(R.id.rdvsLayout);
        if (ll.getChildCount() == 0) {
            ProgressBar progressBar = new ProgressBar(ListRendezVous.this, null, android.R.attr.progressBarStyleSmall);
            progressBar.setId(R.id.progressBar);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.gravity = Gravity.CENTER;
            lparams.setMargins(0, 300, 0, 0);
            lparams.width = 300;
            lparams.height = 300;
            progressBar.setLayoutParams(lparams);
            ll.addView(progressBar);
        }
        getMenuInflater().inflate(R.menu.list_rendez_vous, menu);
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
                Intent i = new Intent(ListRendezVous.this, Connect.class);
                startActivity(i);
            } else {
                Intent i = new Intent(ListRendezVous.this, Disconnect.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(ListRendezVous.this, Register.class);
            startActivity(i);
        } else if (id == R.id.nav_presentation) {
            Intent i = new Intent(ListRendezVous.this, MainActivity.class);
            startActivity(i);
        } else if (item.getTitle() == "Diplomes") {
            Intent i = new Intent(ListRendezVous.this, Diplomes.class);
            startActivity(i);
        } else if (item.getTitle() == "Mes infos") {
            Intent i = new Intent(ListRendezVous.this, Fiche.class);
            startActivity(i);
        } else if (item.getTitle() == "Administration") {
            Intent i = new Intent(ListRendezVous.this, Admin.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
