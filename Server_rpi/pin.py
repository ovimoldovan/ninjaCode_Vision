import RPi.GPIO as GPIO
from time import sleep     # this lets us have a time delay (see line 12)
GPIO.setmode(GPIO.BCM)     # set up BCM GPIO numbering
GPIO.setup(21, GPIO.IN)    # set GPIO 25 as input

try:
    while True:            # this will carry on until you hit CTRL+C
        if GPIO.input(21): # if port 25 == 1
            print ("Port 25 is 1/GPIO.HIGH/True - button pressed")
        else:
            print ("Port 25 is 0/GPIO.LOW/False - button not pressed")
        sleep(0.5)         # wait 0.1 seconds

except KeyboardInterrupt:
    GPIO.cleanup()         # clean up after yourself
