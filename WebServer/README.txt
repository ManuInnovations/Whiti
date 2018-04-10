FlaskServer.py - 
The main python script, runs using flask. Handles get and post requests based on the url.
Runs on your computers IP on Port 5000, using a self signed https certificate (this can be changed)
The server uses the nested content of the template and static folders to produce the html pages

simplerServerLogic.py -
Helper python script that helps process some of the event control logic.
Newer version of an older server logic piece of code and can be subsitituted to replace functionality.

server_email.py -
Handles the proccess of sending emails containting feedback to the chosen email adresses.
Requires a file called email_config.csv

email_config.csv -
All fields required, should be in this format seperated by commas
configured to work using gmail

TestSender,fakeEmail@gmail.com,fakePassword1
TestSenderSettings,smtp.gmail.com,587
TestReciever,secondFakeDestinationEmail@gmail.com,null
TestRecieverSettings,smtp.gmail.com,587

Second email doesn't use a password and can be left as null

RunnerScript.py -
Prints out the address to access the site at (from any device on the same network), and then runs the server.

run_server.bat -
Windows script that activates the virtual environment and runs the RunnerScript.