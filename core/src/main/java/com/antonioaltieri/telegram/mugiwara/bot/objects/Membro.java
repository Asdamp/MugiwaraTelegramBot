package com.antonioaltieri.telegram.mugiwara.bot.objects;

import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotNull;

import java.util.Calendar;

/**
 * Created by Antonio on 11/09/2015.
 */
@Entity
public class Membro {
    @Id String username;
    @Index (IfNotNull.class) LocalDate bannedUntil;

    public LocalDate getBan() {
        return bannedUntil;
    }


    public String getUsername() {
        return username;
    }

    public Membro(String username) {

        this.username = username;
    }

    public Membro() {

    }

    public void setBan(LocalDate ban){
        bannedUntil=ban;
    }

    public void addBan(int days) {
        if(bannedUntil==null) {
            bannedUntil = LocalDate.now();
        }
        bannedUntil=(bannedUntil.plusDays(days));
    }

    public void removeBan(){
        bannedUntil=null;
    }
}
