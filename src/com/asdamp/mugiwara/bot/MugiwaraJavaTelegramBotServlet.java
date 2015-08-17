package com.asdamp.mugiwara.bot;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import co.vandenham.telegram.botapi.TelegramBot;
import co.vandenham.telegram.botapi.requests.ApiResult;
import co.vandenham.telegram.botapi.types.Update;

@SuppressWarnings("serial")
public class MugiwaraJavaTelegramBotServlet extends HttpServlet {
	TelegramBot bot;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Bot avviato");
		bot = new MugiwaraBot(false);
		bot.equals(bot);
	    bot.start();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ServletInputStream in=req.getInputStream();
		String json=readAll(in);
		Gson gson=new Gson();
		Type t=new TypeToken<ApiResult<List<Update>>>() {}.getType();
		ApiResult<List<Update>> res=gson.fromJson(json, t);
		List<Update> upd=res.getResult();
		bot.notifyNewUpdates(upd);
	}
	private static String readAll(InputStream input) {
        Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : null;
    }
}
