import StringIO
import json
import logging
import random
import urllib
import urllib2
import sys
import codecs
import datetime
from google.appengine.ext import db
from google.appengine.api import users
# for sending images
#from PIL import Image
import multipart

# standard app engine imports
from google.appengine.api import urlfetch
from google.appengine.ext import ndb
import webapp2

TOKEN = '69301456:AAEPQ0mGZZasxJKTNlEZnEUfOFBhUqxc818'

BASE_URL = 'https://api.telegram.org/bot' + TOKEN + '/'


# ================================

class EnableStatus(ndb.Model):
    # key name: str(chat_id)
    enabled = ndb.BooleanProperty(indexed=False, default=False)


# ================================

def setEnabled(chat_id, yes):
    es = EnableStatus.get_or_insert(str(chat_id))
    es.enabled = yes
    es.put()

def getEnabled(chat_id):
    es = EnableStatus.get_by_id(str(chat_id))
    if es:
        return es.enabled
    return False

class Player(db.Model):
    name = db.StringProperty(required=True)
    ban_until = db.DateProperty()

# ================================

class MeHandler(webapp2.RequestHandler):
    def get(self):
        urlfetch.set_default_fetch_deadline(60)
        self.response.write(json.dumps(json.load(urllib2.urlopen(BASE_URL + 'getMe'))))


class GetUpdatesHandler(webapp2.RequestHandler):
    def get(self):
        urlfetch.set_default_fetch_deadline(60)
        self.response.write(json.dumps(json.load(urllib2.urlopen(BASE_URL + 'getUpdates'))))


class SetWebhookHandler(webapp2.RequestHandler):
    def get(self):
        urlfetch.set_default_fetch_deadline(60)
        url = self.request.get('url')
        if url:
            self.response.write(json.dumps(json.load(urllib2.urlopen(BASE_URL + 'setWebhook', urllib.urlencode({'url': url})))))


class WebhookHandler(webapp2.RequestHandler):
    def post(self):
    
        urlfetch.set_default_fetch_deadline(60)
        body = json.loads(self.request.body)
        logging.info('request body:')
        logging.info(body)
        self.response.write(json.dumps(body))

        update_id = body['update_id']
        message = body['message']
        message_id = message.get('message_id')
        date = message.get('date')
        text = message.get('text')
        fr = message.get('from')
        chat = message['chat']
        chat_id = chat['id']
        nickname = fr['username']
        if not text:
            logging.info('no text')
            return
        def ban(user_tag,weeks):
            pl=Player(user_tag)
            today=date.today
            d = datetime.timedelta(days=(weeks*7))
            ban_date=today+d
            pl.ban_until=ban_date
            pl.put()
        def get_banned():
            today=date.today
            banned = db.GqlQuery("SELECT * FROM Player WHERE ban_until> DATETIME( :1, :2, :3)", today.year,today.month,today.day)
            for p in banned:
                n=p.name
                b=p.ban_until.strftime("%y/%m/%d")
                msg=msg+"/n"+n+"   "+b
            return msg
        def send(msg=None, img=None):
            if msg:
                resp = urllib2.urlopen(BASE_URL + 'sendMessage', urllib.urlencode({
                    'chat_id': str(chat_id),
                    'text': msg.encode('utf-8'),
                    'disable_web_page_preview': 'true',
                })).read()
            elif img:
                resp = multipart.post_multipart(BASE_URL + 'sendPhoto', [
                    ('chat_id', str(chat_id)),
                    ('reply_to_message_id', str(message_id)),
                ], [
                    ('photo', 'image.jpg', img),
                ])
            else:
                logging.error('no msg or img specified')
                resp = None

            logging.info('send response:')
            logging.info(resp)
        if text.startswith('/'):
            if text == '/start':
                send('Bot abilitato')
                setEnabled(chat_id, True)
            elif text == '/stop':
                send('Bot disabilitato')
                setEnabled(chat_id, False)
            elif text == '/regolamento' or text=='/regolamento@MugiwaraBot':
                regFile=codecs.open("regolamento.txt", "r","utf-8")
                msg = regFile.read()
                send(msg)
            elif text == '/warban':
                ban("@asdamp", 2)
            elif text == '/dbg':
                send(get_banned())
            elif text == '/help' or text=='/help@MugiwaraBot':
                msg=u"Questo e' un bot di utilita' per il clan Mugiwara. Ulteriori funzioni verranno aggiunte in futuro Lista comandi\n\n /regolamento - Posta il regolamento del clan".encode("utf-8");
                send(msg)


app = webapp2.WSGIApplication([
    ('/me', MeHandler),
    ('/updates', GetUpdatesHandler),
    ('/set_webhook', SetWebhookHandler),
    ('/webhook', WebhookHandler),
], debug=True)
