package com.asdamp.mugiwara.bot;

import co.vandenham.telegram.botapi.CommandHandler;
import co.vandenham.telegram.botapi.DefaultHandler;
import co.vandenham.telegram.botapi.MessageHandler;
import co.vandenham.telegram.botapi.TelegramBot;
import co.vandenham.telegram.botapi.requests.ApiResponse;
import co.vandenham.telegram.botapi.requests.OptionalArgs;
import co.vandenham.telegram.botapi.types.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

public class MugiwaraBot extends TelegramBot {
	private final String help="Questo e' un bot di utilita' per il clan Mugiwara. Ulteriori funzioni verranno aggiunte in futuro Lista comandi\n\n /regolamento - Posta il regolamento del clan"; 
    private String regolamento=null;
	private static final Logger log = Logger.getLogger(MugiwaraBot.class.getName());

    public MugiwaraBot(boolean async) {
        super("69301456:AAEPQ0mGZZasxJKTNlEZnEUfOFBhUqxc818", async);
    }

    @CommandHandler("start")
    public void handleStart(Message message) {
        sendMessage(message.getChat().getId(), "Bot abilitato");
    }
    @CommandHandler("help")
    public void handleHelp(Message message) {
        sendMessage(message.getChat().getId(), help);
    }
    
    @CommandHandler("regolamento")
    public void handleRegolamento(Message message){
    	Scanner sc;
		if (regolamento == null) {
			try {
				regolamento = readAll(new FileInputStream("Regolamento.txt"));
			}

			catch (FileNotFoundException e) {

				sendMessage(message.getChat().getId(),
						"Non riesco a trovare il magnifico regolamento del clan. @asdamp sistema subito!");
				return;
			}
		}
        sendMessage(message.getChat().getId(), regolamento, new OptionalArgs().disableWebPagePreview());

    }
	private static String readAll(InputStream input) {
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : null;
	}
	
    @MessageHandler(contentTypes = Message.Type.TEXT)
    public void handleTextMessage(Message message) {
        log.info(String.format("%s: %s", message.getChat().getId(), message.getText()));
        replyTo(message, message.getText());
    }

    @DefaultHandler
    public void handleDefault(Message message) {
       log.info(String.format("%s: %s", message.getChat().getId(), message.getText()));
       //replyTo(message, "ciao");
    }
}