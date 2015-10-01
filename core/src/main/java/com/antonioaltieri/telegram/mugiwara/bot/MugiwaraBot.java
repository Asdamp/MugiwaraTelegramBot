package com.antonioaltieri.telegram.mugiwara.bot;

import com.antonioaltieri.telegram.botapi.CommandHandler;
import com.antonioaltieri.telegram.botapi.DefaultHandler;
import com.antonioaltieri.telegram.botapi.MessageHandler;
import com.antonioaltieri.telegram.botapi.TelegramBot;
import com.antonioaltieri.telegram.botapi.requests.OptionalArgs;
import com.antonioaltieri.telegram.botapi.types.Message;


import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.googlecode.objectify.ObjectifyService;
import com.antonioaltieri.telegram.mugiwara.bot.objects.*;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MugiwaraBot extends TelegramBot {
	private final String help="Questo e' un bot di utilita' per il clan Mugiwara. Ulteriori funzioni verranno aggiunte in futuro Lista comandi\n\n /regolamento - Posta il regolamento del clan"; 
    private String regolamento=null;
    private Formazione formazione=null;
	private static final Logger log = Logger.getLogger(MugiwaraBot.class.getName());



    public MugiwaraBot() {
        super(Token.Token);
        ObjectifyService.register(Formazione.class);
        ObjectifyService.register(Membro.class);
        formazione= ofy().load().type(Formazione.class).id(1).now();
    }

    @CommandHandler("start")
    public void handleStart(Message message) {
        sendMessage(message.getChat().getId(), "Bot abilitato");
    }
    
    @CommandHandler("help")
    public void handleHelp(Message message) {
        sendMessage(message.getChat().getId(), help);
    }

    @CommandHandler("formazione")
    public void handleFormazione(Message message) {
        sendMessage(message.getChat().getId(), formazione.toString());
    }

    @CommandHandler("warban")
    public void handleWarban(Message message) {
        if(!permissionCheck(message)){
            replyTo(message,"Solo il ministro della guerra @asdamp può utilizzare questo comando");
            return;
        }
        String[] params=extractParam(message.getText());
        String nomeMembro;
        int giorniBan=Integer.parseInt(params[2]);
        if(params[1].charAt(0)=='@') nomeMembro=params[1].substring(1);
        else nomeMembro=params[1];
        Membro membro= ofy().load().type(Membro.class).id(nomeMembro).now();
        if(membro==null)
            membro=new Membro(nomeMembro);
        membro.addBan(giorniBan);
        ofy().save().entity(membro).now();
        membro= ofy().load().type(Membro.class).id(nomeMembro).now();
        sendMessage(message.getChat().getId(), "Bannato fino al "+membro.getBan().toString("dd/MM/yyyy"));
    }

    private boolean permissionCheck(Message msg) {
        return msg.getFrom().getUsername().equalsIgnoreCase("asdamp");
    }

    @CommandHandler("statoMembri")
    public void handleStatoMembri(Message message) {
        int chatId = message.getChat().getId();
        List<Membro> bannati=ofy().load().type(Membro.class).filter("bannedUntil != ",null).list();
        /*if(bannati.isEmpty()) {
            sendMessage(chatId, "Nessun membro del clan bannato!");
            return;
        }*/
        String bans="Lista dei membri bannati:";
        LocalDate today = LocalDate.now();
        for(Membro m:bannati){
            if(m.getBan().isBefore(today)){ // se il ban è scaduto, cioè se oggi è una data successiva al ban
                m.removeBan(); //elimina i dati sul ban
                ofy().save().entity(m); //e salva
            }
            else {
                bans = bans + "\n@" + m.getUsername() + "\t\t"+m.getBan().toString("dd/MM/yyyy");
            }
        }
        sendMessage(chatId, bans);

    }

    @CommandHandler("regolamento")
    public void handleRegolamento(Message message){
    	Scanner sc;
		if (regolamento == null) {
			try {
				regolamento = readAll(new FileInputStream("regolamento.txt"));
			}

			catch (FileNotFoundException e) {

				sendMessage(message.getChat().getId(),
						"Non riesco a trovare il magnifico regolamento del clan. @asdamp sistema subito!");
				return;
			}
		}
        sendMessage(message.getChat().getId(), regolamento, new OptionalArgs().disableWebPagePreview());

    }

    @DefaultHandler
    public void handleDefault(Message message) {

       // sendMessage(message.getChat().getId(), help);
    }
	private static String readAll(InputStream input) {
		Scanner scanner = new Scanner(input,"UTF-8");
		scanner.useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : null;
	}
	
    @MessageHandler(contentTypes = Message.Type.TEXT)
    public void handleTextMessage(Message message) {
        String text=message.getText();
        System.out.println("Handling text message: " + text);
        String[] txt=text.split(" ");
        if(txt[0].equalsIgnoreCase("#formazioneguerra")){
            formazione=new Formazione(text);
            ofy().save().entity(formazione).now();

        }
    }
    private boolean containsExactIgnoreCase(String text,String word) {
        return Pattern.compile("(?i)\\b" + word + "\\b").matcher(text).find();
    }

    private String[] extractParam(String text) {
        String[] param= text.split(" ");
        return param;
    }
}