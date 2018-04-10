import socket
import select
import json
import simpleServerLogic as Slogic
import uuid
import os
import re
import Server_Email

from flask import Flask, render_template, abort, request, make_response,redirect,url_for
# Quickstart located here
# http://flask.pocoo.org/docs/0.12/quickstart/#routing
app = Flask(__name__)

regexSafe = re.compile('[^a-zA-Z0-9_\s,.]')
secretKey = uuid.uuid4()
app.config['SECRET_KEY'] = secretKey.bytes


"""-----------------Process 'Get' requests using Flask---------------------"""

@app.route('/')
@app.route('/index.html', methods=['GET','POST'])
def indexRequest():
    if request.method =='GET':
        #needs a cookie
        print("new User connected")
        newUUID =str( new_user())
        response = make_response(render_template('index.html'))
        response.set_cookie('userID',newUUID)
        return response
    else:#post
        newConnectionRecord = {
            "userType":request.cookies.get('userType'),
            "dateInformation":request.cookies.get('dateInformation'),
            "id":request.cookies.get('userID')
            }
        print newConnectionRecord
        connectionRecords.append(newConnectionRecord)
        return redirect('/join_event.html')
@app.route('/join_event.html',methods=['GET','POST'])
def joinRequest():
    if request.method =='GET':
        return render_template('join_event.html')
    else:
        dictMessage={}#encapsulate post request as message format
        room = request.cookies.get('eventCode')
        dictMessage["eventCode"]= room
        dictMessage["userType"] = request.cookies.get('userType')

        reply = Slogic.joinEvent(
        dictMessage=dictMessage,
        userID=request.cookies.get('userID'),
        EventList=EventList,
        UserToEvent=UserToEvent)
        if reply["messageType"]=="error":
            return render_template('join_event.html'),404
        else:
            return redirect('/event.html/'+str(room))

def matchCode(ecode):
    if ecode == 'about.html': return True
    elif ecode == 'feedback.html': return True
    elif ecode == 'news.html' :return True
    elif ecode == 'help.html':return True
    elif ecode == 'index.html':return True
    elif ecode == '':return True
    elif ecode == 'create_event.html':return True
    else: return False

@app.route('/event.html/<eventCode>',methods=['GET'])
def eventRequest(eventCode):
    if matchCode(eventCode):
        return redirect('/'+eventCode)
    else:
        user =  request.cookies.get('userID')
        event = UserToEvent[user]
        return render_template(
        'event.html',
        EName =event.eventName,
        description= event.description)

@app.route('/create_event.html',methods=['GET','POST'])
def createRequest():
    if request.method=='GET':
        return render_template('create_event.html')
    else:
        dictMessage = fromJson(request.cookies.get('eventDetails'))
        #print dictMessage
        reply = Slogic.createEvent(
        dictMessage=dictMessage,
        EventList=EventList,
        UserToEvent=UserToEvent)
        #print reply
        return toJson(reply),200


#MenuPages
@app.route('/about.html',methods=['GET'])
def aboutRequest():
    return render_template('about.html')

@app.route('/feedback.html',methods=['GET','POST'])
def feedbackRequest():
    if request.method=='GET':
        return render_template('feedback.html')
    else:#Process the feedback from the page
        if request.is_json:
            jsonForm = request.get_json()
            messageContent = jsonForm['content']
            #should also check for sql injections if using sql and any other forms of code interference
            if regexSafe.search(messageContent)==None:#Message is safe!
                print "Recieved feedback:"
                print messageContent
                #EMAIL SENT OUT USING SERVER. NEEDS SAFETY FEATURES
                emailSettingsTemp = Server_Email.getAddress()
                #using the recieved email settings, lookup the email adresses
                #usage  sendmail(FROM,TO,MESSAGECONTENT)
                #uncomment line bellow to actually send message
                #emailServer.sendmail(emailSettingsTemp[0][0],emailSettingsTemp[1][0],messageContent)
                #this also needs to disallow future usage of multiple feedback submissions
                return render_template('feedback.html'),200
            else:
                print "WARNING: Someone has tried to send non safe Feedback(possible code injection)"
                return render_template('feedback.html'),400
        else:
            return render_template('feedback.html'),400


@app.route('/news.html',methods=['GET'])
def newsRequest():
    return render_template('news.html')
@app.route('/help.html',methods=['GET'])
def helpRequest():
    return render_template('help.html')
#handle error messages
@app.errorhandler(401)
def pageNotFound(error):
    return render_template('/pageNotFound.html'),401
@app.route('/usageData',methods=['GET'])
def returnUsageData():
    return str(connectionRecords)

"""----------------------Setup serverLogic Variables----------------------"""

# USAGE:createReply(dictMessage,socket,EventList,SocketToEvent,PendingRequests)
EventList = []#EventList
UserToEvent = {}#SocketToEvent changed to usertoevent
#PendingRequests = {}
UserSocketList = set()
connectionRecords=[]
# emailServer = Server_Email.initServer(Server_Email.loadEmails())
def new_user():
    userID = uuid.uuid4()
    while userID in UserSocketList:
        userID = uuid.uuid4()
    UserSocketList.add(userID)
    return userID

#Test room removed, uncomment to use the test room
#Slogic.createEvent({"eventCode":"1234","eventDescription":"Test event for Whiti.com","eventName":"Test Event"},EventList,UserToEvent)



"""------------Helper methods-----------"""

def fromJson(someString):
    dictionary = json.loads(str(someString))
    return dictionary
def toJson(someDictionary):
    someString = json.dumps(someDictionary)
    return someString#does this need to be none unicode?

"""---------------INIT---------------"""

if __name__ == '__main__':
    app.run(host='0.0.0.0',ssl_context='adhoc')
