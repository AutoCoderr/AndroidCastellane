package com.example.ppe.controleur;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ppe.AddMoniteur;
import com.example.ppe.AddRendezVous;
import com.example.ppe.Admin;
import com.example.ppe.Connect;
import com.example.ppe.Diplomes;
import com.example.ppe.Disconnect;
import com.example.ppe.Fiche;
import com.example.ppe.ListRendezVous;
import com.example.ppe.MainActivity;
import com.example.ppe.Register;
import com.example.ppe.RegisterSuite;
import com.example.ppe.StockSession;

import org.json.*;

import java.util.ArrayList;
import java.util.List;

import static android.util.Half.NaN;

public class Controleur {
    private String adresse = "http://54.38.184.22";
    private TextView unChamp;
    private Button unBouton;
    private int uneNote;
    private int nbPoint;
    private String unId;
    private String banUnbanVal;

    private Context context;
    private Connect srcConnect;
    private RegisterSuite srcRegister;
    private MainActivity srcMain;
    private Disconnect srcDisconnect;
    private ListRendezVous srcListRdv;
    private AddRendezVous srcSendRdv;
    private Diplomes srcDiplomes;
    private Fiche srcFiche;
    private Admin srcAdmin;
    private AddMoniteur srcAddMoniteur;

    public Controleur(Context context, Connect srcConnect) {
        this.context = context;
        this.srcConnect = srcConnect;
    }

    public Controleur(Context context, RegisterSuite srcRegister) {
        this.context = context;
        this.srcRegister = srcRegister;
    }

    public Controleur(Context context, MainActivity srcMain) {
        this.context = context;
        this.srcMain = srcMain;
    }

    public Controleur(Context context, ListRendezVous srcListRdv) {
        this.context = context;
        this.srcListRdv = srcListRdv;
    }

    public Controleur(Context context, Disconnect srcDisconnect) {
        this.context = context;
        this.srcDisconnect = srcDisconnect;
    }

    public Controleur(Context context, AddRendezVous srcSendRdv) {
        this.context = context;
        this.srcSendRdv = srcSendRdv;
    }

    public Controleur(Context context, Diplomes srcDiplomes) {
        this.context = context;
        this.srcDiplomes = srcDiplomes;
    }

    public Controleur(Context context, Fiche srcFiche) {
        this.context = context;
        this.srcFiche = srcFiche;
    }

    public Controleur(Context context, Admin srcAdmin) {
        this.context = context;
        this.srcAdmin = srcAdmin;
    }

    public Controleur(Context context, AddMoniteur srcAddMoniteur) {
        this.context = context;
        this.srcAddMoniteur = srcAddMoniteur;
    }

    public void sendRegister(String prenom, String nom, String type, ArrayList<String> typeVars, String adresse, String dateN, String numTel,
                             String facturation, String password, String email) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("prenom");
        values.add(prenom);

        keys.add("nom");
        values.add(nom);

        if (type.equals("étudiant(e)")) {
            keys.add("type");
            values.add("etudiant");

            keys.add("niveau");
            values.add(typeVars.get(0));

            keys.add("reduc");
            values.add(typeVars.get(1));
        } else if (type.equals("salarié(e)")) {
            keys.add("type");
            values.add("salarie");

            keys.add("entreprise");
            values.add(typeVars.get(0));
        }

        keys.add("addr");
        values.add(adresse);

        keys.add("dateN");
        values.add(dateN);

        keys.add("numtel");
        values.add(numTel);

        keys.add("facturation");
        values.add(facturation);

        keys.add("passwd");
        values.add(password);

        keys.add("mail");
        values.add(email);

        this.post("/POST/controleur_registe+rAndroid.php",keys,values,"sendRegisterCallback");
    }

    public void sendRegisterCallback(String reponse) {
        this.msg("Terminé", reponse);
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Inscription échouée","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Inscription réussie!", "Vous êtes inscrit");
                StockSession.connected = true;
                StockSession.prenom = obj.getString("prenom");
                StockSession.nom = obj.getString("nom");
                StockSession.perm = obj.getString("perm");
                StockSession.token = obj.getString("token");
                StockSession.type = obj.getString("type");
                this.srcRegister.RegisterCallback();
            }
        } catch(JSONException e) {
            //this.msg("Erreur JSON 1", e.toString());
        }
    }

    public void sendConnect(String prenom, String nom, String passwd) {

        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("prenom");
        values.add(prenom);

        keys.add("nom");
        values.add(nom);

        keys.add("passwd");
        values.add(passwd);

        this.post("/POST/controleur_connect.php",keys,values,"sendConnectCallback");
    }

    public void sendConnectCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Connexion échouée","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Connexion réussie!", "Vous êtes connecté!");
                StockSession.connected = true;
                StockSession.prenom = obj.getString("prenom");
                StockSession.nom = obj.getString("nom");
                StockSession.perm = obj.getString("perm");
                StockSession.token = obj.getString("token");
                StockSession.type = obj.getString("type");
                this.srcConnect.ConnectedCallback();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 2", e.toString());
        }
    }

    public void sendDisconnect() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        this.post("/POST/controleur_disconnectAndroid.php",keys,values,"sendDisconnectCallback");
    }

    public void sendDisconnectCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("De-connexion échouée","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("De-connexion réussie!", "Vous êtes dé-connecté!");
                StockSession.connected = false;
                StockSession.prenom = null;
                StockSession.nom = null;
                StockSession.perm = null;
                StockSession.token = null;
                this.srcDisconnect.DisconnectedCallback();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 3", e.toString());
        }
    }

    public void getRdv(int numRdv) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("numRdv");
        values.add(String.valueOf(numRdv));

        this.post("/POST/controleur_getRendezVous.php",keys,values,"getRdvCallback");
    }

    public void getRdvCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Récupération des cours échouée","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                srcListRdv.rdvGetted(obj.getJSONArray("rdvs"),obj.getString("index"));
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 4", e.toString());
        }
    }

    public void deleteRdv(String id, String type) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("id");
        values.add(id);

        keys.add("type");
        values.add(type);

        this.post("/POST/controleur_deleteRendezVous.php",keys,values,"deleteRdvCallback");
    }

    public void deleteRdvCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Suppression échouée","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Suppression réussie", "Suppression réussie");
                srcListRdv.rdvDeleted();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 5", e.toString());
        }
    }

    public void sendRdv(String date, String heureD, String type, ArrayList<String> typeVars) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("action");
        values.add("add");

        keys.add("date");
        values.add(date);

        keys.add("type");
        if (type.equals("Cours")) {
            values.add("planning");

            keys.add("heureD");
            values.add(heureD);
            int heureofD = 0;
            int minuteofD = 0;
            int heureDuree = 0;
            int minuteDuree = 0;

            try {
                heureofD = Integer.valueOf(heureD.split(":")[0]);
                minuteofD = Integer.valueOf(heureD.split(":")[1]);
                heureDuree = Integer.valueOf(typeVars.get(0).split(":")[0]);
                minuteDuree = Integer.valueOf(typeVars.get(0).split(":")[1]);
            } catch(NumberFormatException e) {
                this.msg("Erreur : ","Format de l'heure incorrect");
                return;
            }

            String heureofF = String.valueOf(heureofD+heureDuree);
            String minuteofF = String.valueOf(minuteofD+minuteDuree);

            if (Integer.valueOf(minuteofF) >= 60) {
                minuteofF = String.valueOf(Integer.valueOf(minuteofF)-60);
                heureofF = String.valueOf(Integer.valueOf(heureofF)-1);
            }

            if (minuteofF.length() < 2) {
                minuteofF = "0"+minuteofF;
            }

            if (heureofF.length() < 2) {
                heureofF = "0"+heureofF;
            }

            //System.out.println("heureD = "+heureD+" ; heureF = "+String.valueOf(heureofF)+":"+String.valueOf(minuteofF));

            keys.add("heureF");
            values.add(heureofF+":"+minuteofF);

            keys.add("lecon");
            values.add(typeVars.get(1));

        } else if (type.equals("Exam")) {
            values.add("exam");

            keys.add("heure");
            values.add(heureD);

            keys.add("typeexam");
            values.add(typeVars.get(0));
        }
        this.post("/POST/controleur_rendezvous.php",keys,values,"sendRdvCallback");
    }

    public void sendRdvCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Envoie échoué","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Création rendez vous :", "Rendez vous créé");
                srcSendRdv.rdvSended();
            }
        } catch(JSONException e) {
            //this.msg("Erreur JSON 6", e.toString());
        }
    }

    public void getLecons() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        this.post("/POST/controleur_getLecon.php",keys,values,"getLeconCallback");
    }

    public void getLeconCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de récupération des cours","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                srcSendRdv.courGetted(obj.getJSONArray("lecons"));
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 7", e.toString());
        }
    }

    public void getExams() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        this.post("/POST/controleur_getExam.php",keys,values,"getExamCallback");
    }

    public void getExamCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de récupération des cours","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                srcSendRdv.examGetted(obj.getJSONArray("exams"));
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 8", e.toString());
        }
    }

    public void getDiplomes() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        this.post("/POST/controleur_getDiplome.php",keys,values,"getDiplomeCallback");
    }

    public void getDiplomeCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de récupération des cours","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                srcDiplomes.diplomesGetted(obj.getJSONArray("diplomes"));
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 8", e.toString());
        }
    }

    public void validRdv(String id, String type) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("action");
        values.add("valid");

        keys.add("id");
        values.add(id);

        keys.add("type");
        values.add(type);

        this.post("/POST/controleur_rendezvous.php",keys,values,"validRdvCallback");
    }

    public void validRdvCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de validation du rendez vous","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Rendez vous validé", "Rendez vous validé");
                this.srcListRdv.rdvValidedOrUnvalided();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 8", e.toString());
        }
    }

    public void devalidRdv(String id, String type) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("action");
        values.add("devalid");

        keys.add("id");
        values.add(id);

        keys.add("type");
        values.add(type);

        this.post("/POST/controleur_rendezvous.php",keys,values,"devalidRdvCallback");
    }

    public void devalidRdvCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de dé-validation du rendez vous","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Rendez vous dé-validé", "Rendez vous dé-validé");
                this.srcListRdv.rdvValidedOrUnvalided();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 8", e.toString());
        }
    }

    public void changeNoteQuiz(final String id, final TextView diplome) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        alert.setTitle("Changer la note");
        alert.setMessage("Quelle note voulez vous mettre");

        final EditText input = new EditText(this.context);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                int note = 100000000;
                if (!value.equals("")) {
                    try {
                        note = Integer.valueOf(value);
                    } catch(NumberFormatException e) {
                        Controleur.this.msg("Erreur :", "Format de la note incorrect");
                        return;
                    }
                }
                List<String> keys = new ArrayList<>();

                List<String> values= new ArrayList<>();

                keys.add("token");
                values.add(StockSession.token);

                keys.add("id");
                values.add(id);

                keys.add("note");
                if (note != 100000000) {
                    values.add(String.valueOf(note));
                } else {
                    values.add("");
                }

                Controleur.this.unChamp = diplome;
                Controleur.this.uneNote = note;
                Controleur.this.post("/POST/controleur_setNoteQuiz.php",keys,values,"changeNoteQuizCallback");
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void changeNoteQuizCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de changement de note","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                String champsFirstStep = this.unChamp.getText().toString().split(":")[0];
                if (this.uneNote != 100000000) {
                    this.unChamp.setText(champsFirstStep + ":    " + this.uneNote+"/"+obj.getString("nbPoint"));
                } else {
                    this.unChamp.setText(champsFirstStep + ":    non noté");
                }
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 8", e.toString());
        }
    }

    public void changeNote(String idd, final TextView diplome) {
        final String id = idd;
        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        alert.setTitle("Changer la note");
        alert.setMessage("Quelle note voulez vous mettre");

        final EditText input = new EditText(this.context);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                int note = 100000000;
                if (!value.equals("")) {
                    try {
                        note = Integer.valueOf(value);
                        if (note < 0 | note > 20) {
                            Controleur.this.msg("Erreur :", "Format de la note incorrect");
                            return;
                        }
                    } catch(NumberFormatException e) {
                        Controleur.this.msg("Erreur :", "Format de la note incorrect");
                        return;
                    }
                }
                List<String> keys = new ArrayList<>();

                List<String> values= new ArrayList<>();

                keys.add("token");
                values.add(StockSession.token);

                keys.add("id");
                values.add(id);

                keys.add("note");
                if (note != 100000000) {
                    values.add(String.valueOf(note));
                } else {
                    values.add("");
                }

                Controleur.this.unChamp = diplome;
                Controleur.this.uneNote = note;
                Controleur.this.post("/POST/controleur_setNote.php",keys,values,"changeNoteCallback");
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void changeNoteCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de changement de note","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                String champsFirstStep = this.unChamp.getText().toString().split(":")[0];
                if (this.uneNote != 100000000) {
                    this.unChamp.setText(champsFirstStep + ":    " + this.uneNote + "/20");
                } else {
                    this.unChamp.setText(champsFirstStep + ":    non noté");
                }
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 8", e.toString());
        }
    }

    public void getFiche() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        this.post("/POST/controleur_getOwnFicheAndroid.php",keys,values,"getFicheCallback");
    }

    public void getFicheCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de récupération de fiche","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.srcFiche.ficheGetted(obj.getJSONObject("fiche"));
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 9", e.toString());
        }
    }

    public void changePasswd(String mdp1, String mdp2) {
        if (mdp1.equals("")) {
            this.msg("Erreur", "Veuillez spécifier un mot de passe");
        } else if (!mdp1.equals(mdp2)) {
            this.msg("Erreur", "Vous n'avez pas rentré deux fois le même mot de passe");
        } else {
            List<String> keys = new ArrayList<>();

            List<String> values= new ArrayList<>();

            keys.add("token");
            values.add(StockSession.token);

            keys.add("password");
            values.add(mdp1);

            this.post("/POST/controleur_changePasswordAndroid.php",keys,values,"changePasswdCallback");
        }
    }

    public void changePasswdCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de changement de mot de passe","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Réussi","Mot de passe changé avec succès!");
                this.srcFiche.passwordChanged();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 10", e.toString());
        }
    }

    public void alterCode(String code, String idClient, TextView diplome, Button unBouton) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("idClient");
        values.add(idClient);

        keys.add("code");
        values.add(code);

        this.uneNote = Integer.valueOf(code);
        this.unChamp = diplome;
        this.unBouton = unBouton;
        this.unId = idClient;

        this.post("/POST/controleur_alterCodeAndroid.php",keys,values,"alterCodeCallback");
    }

    public void alterCodeCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de changement du code","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                if (this.uneNote == 0) {
                    this.unChamp.setText("N'a pas le code");
                    this.unBouton.setText("Donner");
                    this.unBouton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Controleur.this.alterCode("1", Controleur.this.unId, Controleur.this.unChamp,Controleur.this.unBouton);
                        }
                    });
                } else {
                    this.unChamp.setText("A le code");
                    this.unBouton.setText("Enlever");
                    this.unBouton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur.this.alterCode("0", Controleur.this.unId, Controleur.this.unChamp,Controleur.this.unBouton);
                        }
                    });
                }
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 11", e.toString());
        }
    }

    public void getUsersList() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        this.post("/POST/controleur_getUsersAndroid.php",keys,values,"getUsersListCallback");
    }

    public void getUsersListCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de récupération des utilisateurs","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.srcAdmin.usersGetted(obj.getJSONArray("users"));
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 12", e.toString());
        }
    }

    public void banUnban(String id, String val, Button unBouton) {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("token");
        values.add(StockSession.token);

        keys.add("id");
        values.add(id);

        keys.add("val");
        values.add(val);

        this.banUnbanVal = val;
        this.unBouton = unBouton;
        this.unId = id;

        this.post("/POST/controleur_banUnBanAndroid.php",keys,values,"banUnbanCallback");
    }

    public void banUnbanCallback(String reponse) {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de bannissement/dé-bannissement","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                if (this.banUnbanVal.equals("1")) {
                    this.unBouton.setText("Dé-bannir");
                    this.unBouton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur.this.banUnban(Controleur.this.unId,"0", Controleur.this.unBouton);
                        }
                    });
                } else {
                    this.unBouton.setText("Bannir");
                    this.unBouton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Controleur.this.banUnban(Controleur.this.unId,"1", Controleur.this.unBouton);
                        }
                    });
                }
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 13", e.toString());
        }
    }

    public void addMoniteur(String prenom, String nom, String password, String mail){
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        keys.add("action");
        values.add("addmoniteur");

        keys.add("prenom");
        values.add(prenom);

        keys.add("nom");
        values.add(nom);

        keys.add("mail");
        values.add(mail);

        keys.add("passwd");
        values.add(password);

        keys.add("token");
        values.add(StockSession.token);

        this.post("/POST/controleur_admin.php",keys,values,"addMoniteurCallback");
    }

    public void addMoniteurCallback(String reponse)  {
        try {
            JSONObject obj = new JSONObject(reponse);
            if (obj.getString("rep").equals("failed")) {
                String errors = "";
                JSONArray errorsList = obj.getJSONArray("errors");

                for (int i = 0;i<errorsList.length();i++) {
                    errors += "\t\t"+errorsList.get(i)+"\n";
                }
                this.msg("Erreur de d'ajout de moniteur","Erreurs :\n"+errors);
            } else if (obj.getString("rep").equals("success")) {
                this.msg("Réussi : ","Ajout d'un moniteur réussi!");
                this.srcAddMoniteur.moniteurAdded();
            }
        } catch(JSONException e) {
            this.msg("Erreur JSON 14", e.toString());
        }
    }

    public void testSession() {
        List<String> keys = new ArrayList<>();

        List<String> values= new ArrayList<>();

        this.post("/POST/sessionTest.php",keys,values,"sessionTestCallback");
    }

    public void sessionTestCallback(String reponse) {
        this.msg("Réponses", "reponse : "+reponse+"\ncookie : "+StockSession.PHPSESSID);
    }



    private void post(String file, List<String> keys, List<String> values, String callback) {
        new Post(this.adresse+file,this, callback).execute(keys,values);
    }

    public void msg(String titre, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
        alertDialog.setTitle(titre);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}