package com.antonioaltieri.telegram.mugiwara.bot.objects;

import com.antonioaltieri.telegram.botapi.types.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 11/09/2015.
 */
public class Formazione {
    private List<Membro> formazione;
    public Formazione(String text) {
        estraiFormazione(text);

    }

    private void estraiFormazione(String text) {
        formazione=new ArrayList<>();
        String txt[]=text.split("\\W+");
        for(int i=3; i<txt.length; i=i+2){
            formazione.add(new Membro(txt[i]));
        }
    }

    @Override
    public String toString() {
        String form="#formazioneGuerra\n\n";
        for(int i=0;i<formazione.size();i++){
            form=form+(i+1)+". @"+ formazione.get(i).getUsername()+"\n";
        }
        return form;
    }
}
