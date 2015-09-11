package com.antonioaltieri.telegram.mugiwara.bot;

import com.antonioaltieri.telegram.botapi.CommandHandler;
import com.antonioaltieri.telegram.botapi.DefaultHandler;
import com.antonioaltieri.telegram.botapi.MessageHandler;
import com.antonioaltieri.telegram.botapi.TelegramBot;
import com.antonioaltieri.telegram.botapi.requests.OptionalArgs;
import com.antonioaltieri.telegram.botapi.types.Message;
import com.antonioaltieri.telegram.mugiwara.bot.objects.Formazione;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
        }
    }
    private boolean containsExactIgnoreCase(String text,String word) {
        return Pattern.compile("(?i)\\b" + word + "\\b").matcher(text).find();
    }

   /* private String containsHashTag(String text) {
        return Pattern.compile("#(\\w+)").matcher(text).group().trim();
    }*/
}