import smtplib

from email.mime.text import MIMEText

def loadEmails():
    """
    we expect the file to be in this format
    TestSender,fakeEmail@gmail.com,fakePassword1
    TestSenderSettings,smtp.gmail.com,587
    TestReciever,secondFakeDestinationEmail@gmail.com,fakePassword2
    TestRecieverSettings,smtp.gmail.com,587
    """
    configFile = open("email_config.csv")
    all_lines = configFile.readlines()
    configFile.close()

    emailDict = {}

    for line in all_lines:
        entries = line.split(",")
        emailDict[entries[0]] = (entries[1].strip(),entries[2].strip())

    return emailDict

def testMessage(emailSettings):
    """
    Using the fake settings configured by the server this will send a message to the server
    """
    FakeMessage= "This is a test message with content \n Hopefully it works as intended"

    msg = {}

    msg['Content'] = FakeMessage
    msg['From'] = emailSettings['TestSender'][0]
    msg['To'] = emailSettings['TestReciever'][0]

    return msg

def initServer(emailSettings):
    settings = emailSettings['TestSenderSettings']

    smtpserver = smtplib.SMTP(settings[0],settings[1])
    smtpserver.ehlo()
    smtpserver.starttls()
    smtpserver.ehlo()

    loginDetails = emailSettings['TestSender']
    #print loginDetails[0],loginDetails[1]
    smtpserver.login(loginDetails[0],loginDetails[1])

    return smtpserver

def testmain():
    #Load the email config
    emailSettings = loadEmails()
    #create a test message to be sent
    message = testMessage(emailSettings)
    #start to initialise the emailServer
    emailServer = initServer(emailSettings)
    #lastly send the email
    emailServer.sendmail(message['From'],message['To'],message['Content'])
    #then quit
    emailServer.close()
    print ('email sent!')
"""
To access content from within another program,
use: server_email.initServer(server_email.loadEmails())
then use getAddress() method to return the sending values
lastly use the sendmail method as shown in the testmain method above
"""
def getAddress():
    emailSettings = loadEmails()
    return (emailSettings['TestSender'],emailSettings['TestReciever'])


if __name__ == '__main__':
    testmain()
