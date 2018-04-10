class SEvent:
    """
    An event contains a number of rooms that people are sorted into for listening.
    Each field is unique to the object and initialized each time for each object
    internalRooms = list of internal rooms for each language
    eventCode = 0123
    hostSocket = Host
    socketToRoom = map of socket to rooms
    """
    def __init__(self,dictMessage,host):
        self.eventCode = dictMessage["eventCode"]
        self.hostSocket = host
        self.description = "Event Description"

        self.internalRooms = []
        self.socketToRoom = {}
        self.socketToRoom[host] = "Lobby"

    def createRoom(self,dictMessage,host):
        newRoom = SRoom(dictMessage,host)
        self.internalRooms.append(newRoom)
        self.socketToRoom[host] = newRoom
        if dictMessage["userType"] == "interpreter": #tell them where the speaker is
            hostRoom = self.socketToRoom[self.hostSocket]
            if hostRoom == "Lobby":
                reply = { "messageType":"error", "errorCode":"004", "errorMessage":"host not initialized connection properly" }
                return reply
            else:
                reply = "successful creation"
                return reply
        else: #created by speaker
            reply = "successful creation"
            return reply
    def putIntoRoom(self,dictMessage,listener):
        #search rooms for one with a matching language
        #only listeners can be put into a room
        for room in self.internalRooms:
            if room.language == dictMessage["language"]:
                socketToRoom[listener]=room
                reply = room.retrieveStream()
                return reply
        reply = "error"#speaker has not been found
        return reply

    def requestLanguages(self):
        reply = {"messageType":"listenerLanguageRequest"}
        reply["languages"] = []
        for room in self.internalRooms:
            reply["languages"].append(room.language)
        return reply


class SRoom:
    """Rooms are the base level of the listening functionality"""
    def __init__(self,dictMessage,host):
        self.language = dictMessage["language"]
        self.hostSocket = host
    def retrieveStream(self):
        messageHost = self.hostSocket

def createEvent(dictMessage,socket,EventList,SocketToEvent):#done
    """
    Speaker:
    if event already exists send Error
    if event not exists createEvent,
    prompt asking for language
    """
    for eventIndex in EventList:
        if eventIndex.eventCode == dictMessage[eventCode]:
            reply = { "messageType":"error", "errorCode":"006", "errorMessage":"failed to create event, event allready exists" }
            return reply

    newEvent = SEvent(dictMessage,host=socket)
    SocketToEvent[socket] = newEvent #checked for collisions
    EventList.append(newEvent)
    reply = { "messageType":"languageRequest" } #behavior as in design docs, request lanuguage from socket
    return reply

def joinEvent(dictMessage,socket,EventList,SocketToEvent):#done
    """
    Listener/interpreter:
    if event is correct, add them to event
    """
    for eventIndex in EventList:#check if valid event
        if eventIndex.eventCode == dictMessage["eventCode"]:#check if have found event
            #found event, now add the person to that particular event by setting dict val,
            SocketToEvent[socket] = eventIndex
            if dictMessage["userType"] == "interpreter":
                reply = { "messageType":"languageRequest" } #behavior as in design docs, request lanuguage from socket
                return reply
            else: # is Listener
                reply = "Connected" #TODO make a message for successful listener connection maybe?
                return reply
    #case of event not found
    reply = { "messageType":"error", "errorCode":"001", "errorMessage":"event unavailable" }
    return reply

#input point for the script,
def createReply(dictMessage,socket,EventList,SocketToEvent,PendingRequests):
    #test for message type
    mType = dictMessage["messageType"]
    if mType =="connection":
        if dictMessage["userType"]=="speaker":
            #build event,autobuild room, send request speaking languageRequest
            reply = createEvent(dictMessage,socket,EventList,SocketToEvent)
            return reply
        else:#assumption of listener/interpreter
            reply = joinEvent(dictMessage,socket,EventList,SocketToEvent)
            return reply
    elif mType == "languageProvider":
        #check socket to room to make sure the event is made then setup the room
        eventR = SocketToEvent[socket]
        if eventR == None:
            reply = { "messageType":"error", "errorCode":"002", "errorMessage":"event unavailable to provide for" }
            return reply
        else:
            reply = eventR.createRoom(dictMessage,host=socket)
            return reply
    elif mType == "listenerRequestingLanguages":
        #find the users event then get it to provide languages
        reply = SocketToEvent[socket].requestLanguages()
        return reply
    elif mType == "listenerLanguageChoice":
        #find the event, and tell it to sort the socket into the room
        eventR = SocketToEvent[socket]
        if eventR == None:
            reply = { "messageType":"error", "errorCode":"003", "errorMessage":"event not found to select language from" }
            return reply
        else:
            reply = eventR.putIntoRoom(dictMessage,socket)
            return reply
    elif mType == "connectToClient":
        #store in socket to message dictionary
        PendingRequests[socket] = dictMessage
        reply = "successful input"#TODO success message?
        return reply
    else:
        reply = { "messageType":"error", "errorCode":"004", "errorMessage":"message not recognisedT" }
        return reply
