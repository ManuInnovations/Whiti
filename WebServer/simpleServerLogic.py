import random, string
"""
simpleServerLogic:
Designed to replace the functionality of the full serverLogic class
Uses the use case of just the interpreter in the same room as the person they are translating
Should be more robust and applys only one-to-many communication
The simple process is:
Room created
Interpreter connects to room,using eventCode, states language translating to
Listeners connect to the room, using eventCodes
Upon connecting to the room, the webRTC code will establish a one to many connection


"""

class SimpleEvent:
    """
    An event contains a number of rooms that people are sorted into for listening.
    Each field is unique to the object and initialized each time for each object
    eventCode = 0123
    hostID = Host
    """
    def __init__(self,dictMessage):
        self.eventCode = dictMessage["eventCode"]
        self.eventName = dictMessage["eventName"]
        self.description = dictMessage["eventDescription"]
        self.language = "noLang"

        self.userIDs = []
        self.hostID = "noHost"
    def joinEvent(self,dictMessage,userID):
        if dictMessage["userType"] == "interpreter":
            if self.hostID == "noHost":
                self.hostID=userID
                reply = { "messageType":"languageRequest" } #behavior as in design docs, request lanuguage from socket
                return reply
            else:
                reply = { "messageType":"error", "errorCode":"005", "errorMessage":"host allready in room" }
                return reply
        else: # is Listener
            self.userIDs.append(userID)
            reply = {"messageType":"Connected"} #TODO make a message for successful listener connection maybe?
            return reply

    def requestLanguages(self):
        if self.language == "noLang":
            return { "messageType":"error", "errorCode":"004", "errorMessage":"message not recognised" }
        reply = {"messageType":"listenerLanguageRequest"}
        reply["languages"] = [self.language]
        return reply
"""
Methods facilitating message types:
"""
def createEvent(dictMessage,EventList,UserToEvent):
    if dictMessage["eventCode"] == "":
        dictMessage["eventCode"] = generateEventCode(EventList)
    #check namespace available
    for eventR in EventList:
        if eventR.eventCode == dictMessage["eventCode"]:
        #error case
            reply = {"messageType":"error","errorCode":"006","errorMessage":"failed to create event allready exists"}
            return reply
    newEvent = SimpleEvent(dictMessage)
    EventList.append(newEvent)
    reply = {"messageType":"Successful_Creation","eventCode":dictMessage["eventCode"]}
    return reply


def joinEvent(dictMessage,userID,EventList,UserToEvent):
    for eventIndex in EventList:
        if dictMessage["eventCode"]==eventIndex.eventCode:
            reply= eventIndex.joinEvent(dictMessage,userID)#if this fails, needs to catch the error case on the way up
            if reply["messageType"] == "error":
                return reply
            else:
                UserToEvent[userID]=eventIndex
                return reply

    reply = { "messageType":"error", "errorCode":"001", "errorMessage":"event unavailable" }
    return reply

def createReply(dictMessage,userID,EventList,UserToEvent):
    mType = dictMessage["messageType"]
    if mType == "connection":
        if dictMessage["userType"]=="speaker":
            reply = createEvent(dictMessage,EventList,UserToEvent)
            return reply
        else:
            reply = joinEvent(dictMessage,userID,EventList,UserToEvent)
            return reply
    elif mType == "languageProvider":
        eventR = UserToEvent[userID]
        if eventR == None:
            reply = { "messageType":"error", "errorCode":"002", "errorMessage":"event unavailable to provide for" }
            return reply
        elif eventR.hostID == userID:
            eventR.language= dictMessage["language"]
            reply = {"messageType":"success","activity":"languageProvider"}
            return reply
        else:
            reply = { "messageType":"error", "errorCode":"002", "errorMessage":"you are not the host!" }
            return reply
    elif mType =="connectToClient":
        #TODO
        print ("TODO")
    else:
        reply =  { "messageType":"error", "errorCode":"004", "errorMessage":"message not recognised" }
        return reply


def generateEventCode(EventList):
    possible = "abcdefghijklmnopqrstuvwxyz123456789"
    #check namespace available
    while 1==1:
        valid = True
        code = ''.join(random.choice(possible) for i in range(5))
        for eventR in EventList:
            if eventR.eventCode == code:
                valid = False
                break
        if valid:
            return ''.join(random.choice(possible) for i in range(5))
    return null
