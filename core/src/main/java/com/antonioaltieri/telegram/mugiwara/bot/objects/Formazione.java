package com.antonioaltieri.telegram.mugiwara.bot.objects;

import com.antonioaltieri.telegram.botapi.types.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 11/09/2015.
 */
@Entity
public class Formazione {
    @Id
    long id = 1;
    private List<Ref<Membro>> formazione = new ArrayList<Ref<Membro>>();

    public Formazione() {

    }

    public Formazione(String text) {
        estraiFormazione(text);
    }

    private void estraiFormazione(String text) {
        String txt[] = text.split("\\W+");
        for (int i = 3; i < txt.length; i = i + 2) {
            addMembro(new Membro(txt[i]));
        }
    }

    @Override
    public String toString() {
        String form = "#formazioneGuerra\n\n";
        for (int i = 0; i < formazione.size(); i++) {
            form = form + (i + 1) + ". @" + getMembro(i).getUsername() + "\n";
        }
        return form;
    }

    public Membro getMembro(int pos){
       return formazione.get(pos).get();
    }

    public void addMembro(Membro m){
        formazione.add(Ref.create(m));
    }
}
