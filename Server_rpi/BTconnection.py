import os
import glob
import time
import RPi.GPIO as GPIO
from bluetooth import *

GPIO.setmode(GPIO.BCM)     # set up BCM GPIO numbering
GPIO.setup(21, GPIO.IN)
#GPIO.setup(20, GPIO.IN)

b1=0
x=1
wait = 1



connection = False
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",3))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "VoltMeterPiServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ] 
                    #protocols = [ OBEX_UUID ]
                   )
while True:
    if(connection == False):
        print("Waiting for connection on RFCOMM channel %d" % port)
        client_sock, client_info = server_sock.accept()
        connection = True
        print("Accepted connection from ", client_info)
        try:
            while True: 
                #data = client_sock.recv(1024)
                #if (data == "disconnect"):
                #    print("Client wanted to disconnect")
                #    client_sock.close()
                #    connection = False
                #if (GPIO.input(21) == True):
                
                if (GPIO.input(21) == True):
                    b1 = 1
                    client_sock.send("OBJECT AHEAD")
                    #client_sock.close()
                    #connection = False
                    print ("SENT1") 
                if(GPIO.input(21) == False):
                    b1 = 0
                    client_sock.send("A")
                    print ("SENT0")
                    #client_sock.close()
                    #connection = False
                #if(GPIO.input(20) == False):
                #    b1 = 0
                #    client_sock.send("FALL DETECTED")
                #    print ("SENT0")
                    #client_sock.close()
                    #connection = False
            #client_sock.send("CEVA1")
                time.sleep(wait)
            
        except IOError:
            print("Connection disconnected! Exceptie")
            client_sock.close()
            connection = False
            pass
        except BluetoothError:
            print("Something wrong with bluetooth")
        except KeyboardInterrupt:
            print("\nDisconnected")
            client_sock.close()
            server_sock.close()
            break
