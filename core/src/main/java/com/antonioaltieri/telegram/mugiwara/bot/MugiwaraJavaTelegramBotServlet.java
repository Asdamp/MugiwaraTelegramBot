package com.antonioaltieri.telegram.mugiwara.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

import com.antonioaltieri.telegram.botapi.TelegramBot;
import com.antonioaltieri.telegram.botapi.types.Update;
import com.google.gson.Gson;



@SuppressWarnings("serial")
public class MugiwaraJavaTelegramBotServlet extends HttpServlet {
	TelegramBot bot;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Bot avviato");
		bot = new MugiwaraBot();
	    bot.start();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if(bot==null){
			bot = new MugiwaraBot();
		    bot.start();
		}
		ServletInputStream in=req.getInputStream();
		String json=readAll(in);
		Gson gson=new Gson();
		Update upd=gson.fromJson(json, Update.class);
		bot.notifyNewUpdate(upd);
	}
	private static String readAll(InputStream input) {
        Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : null;
    }
}
