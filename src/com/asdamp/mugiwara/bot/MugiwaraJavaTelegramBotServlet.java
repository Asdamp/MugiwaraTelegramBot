package com.asdamp.mugiwara.bot;

import java.io.IOException;
import javax.servlet.http.*;

import co.vandenham.telegram.botapi.TelegramBot;

@SuppressWarnings("serial")
public class MugiwaraJavaTelegramBotServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Bot avviato");
		TelegramBot bot = new MugiwaraBot(false);
	    bot.start();
	}
	
}
