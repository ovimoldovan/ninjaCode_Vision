#include <Wire.h>

#include <Wire.h>
#include <SparkFun_APDS9960.h>
#include <SparkFun_ADXL345.h>
#include <ADXL345.h>
#define pino1 7
#define pino2 6
ADXL345 adxl = ADXL345();
// Global Variables

SparkFun_APDS9960 apds = SparkFun_APDS9960();
uint8_t proximity_data = 0;
int state;
void setup() {

  Serial.begin(9600);

  // Initialize ADXL345
  Serial.println("Initialize L3G4200D");

  adxl.powerOn();

  adxl.setFreeFallThreshold(4);       // (5 - 9) recommended - 62.5mg per increment
  adxl.setFreeFallDuration(15);       // (20 - 70) recommended - 5ms per increment

  // Initialize Serial port
  Serial.begin(9600);

  pinMode(pino1, OUTPUT);
  pinMode(pino2, OUTPUT);

  // Initialize APDS-9960 (configure I2C and initial values)
  if ( apds.init() ) {
    Serial.println(F("APDS-9960 initialization complete"));
  } else {
    Serial.println(F("Something went wrong during APDS-9960 init!"));
  }

  // Adjust the Proximity sensor gain
  if ( !apds.setProximityGain(PGAIN_2X) ) {
    Serial.println(F("Something went wrong trying to set PGAIN"));
  }

  // Start running the APDS-9960 proximity sensor (no interrupts)
  if ( apds.enableProximitySensor(false) ) {
    Serial.println(F("Proximity sensor is now running"));
  } else {
    Serial.println(F("Something went wrong during sensor init!"));
  }
  adxl.FreeFallINT(1);
}

void loop() {

  delay(50);

  int x, y, z;
  adxl.readAccel(&x, &y, &z);

  byte interrupts = adxl.getInterruptSource();

  // Free Fall Detection
  if (adxl.triggered(interrupts, ADXL345_FREE_FALL)) {
    Serial.println("*** FREE FALL ***");
    digitalWrite(pino1, HIGH);
  } else {
    digitalWrite(pino1, LOW);
  }

  // Read the proximity value
  if ( !apds.readProximity(proximity_data) ) {
    Serial.println("Error reading proximity value");
  } else {
    Serial.print("Proximity: ");
    Serial.println(proximity_data);
    if (proximity_data != 0) {
      digitalWrite(pino2, HIGH);
    } else {
      digitalWrite(pino2, LOW);
    }
  }

  // Wait 250 ms before next reading
  delay(250);
}
