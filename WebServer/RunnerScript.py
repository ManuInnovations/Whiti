'''
This script is just a simple wrapper for the server script, which does things
like tell the user the IP to access the site from other devices

It should be invoked by a batch/bash script
'''

import socket

try:
    #Try to find and grab the user's IP address
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(('8.8.8.8', 1)) #This requires an internet connection
    local_ip_address = s.getsockname()[0]
except:
    #If we can't get it, they'll just have to find it themselves
    print ("Cannot detect IP without internet connection, you need to find it yourself!")
    local_ip_address = "your_ip_address_here"
finally:
     s.close()

print ("Other devices can connect by going to:")
print ("https://" + local_ip_address + ":5000")

print ("Please wait...")

#Apparently execfile is bad practice, but this seemed like the best cross-platform method
execfile('FlaskServer.py')
