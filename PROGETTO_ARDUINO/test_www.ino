/**
 * Author: Alfio Costanzo
 * E-mail: alfioc87@hotmail.it
 * Progetto: Raccolta dati GSP e trasmissione a WebServer Remoto (php)
 **/

#include <WiFi.h>
#include <SPI.h>
#include <String.h>
#include <Adafruit_GPS.h>
#include <SoftwareSerial.h>

char ssid[] = "AndroidAP";
char key[] = "03dbd5019a71";
int keyIndex = 0;
int status = WL_IDLE_STATUS;

String arduinoKey ;

IPAddress server(31,27,101,76);
IPAddress gateway(192, 168, 43, 1);
//char server[] = "192.168.137.1";
WiFiClient client;

Adafruit_GPS GPS(&Serial1);
#define GPSECHO  false
boolean usingInterrupt = false;
void useInterrupt(boolean);
uint32_t timer = millis();


void setup() {
  
  Serial.begin(9600); 

  randomSeed(analogRead(0));
  arduinoKey = generateKey(4);

  Serial.println("Adafruit GPS library basic test!");
  GPS.begin(9600);
  GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_ALLDATA);
  GPS.sendCommand(PMTK_SET_NMEA_UPDATE_1HZ);
  GPS.sendCommand(PGCMD_ANTENNA);
  useInterrupt(true);

  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present"); 
    while(true);
  } 

  while ( status != WL_CONNECTED) { 
    Serial.print("Attempting to connect to WEP network, SSID: ");
    Serial.println(ssid);
    //status = WiFi.begin(ssid, keyIndex, key);  //Rete Domestica
    status = WiFi.begin(ssid,key);  //Rete Mobile
    delay(2000);
  }

  Serial.println("You're connected to the network");
  printCurrentNet();
  printWifiData();

}

SIGNAL(TIMER0_COMPA_vect) {
  char c = GPS.read();
#ifdef UDR0
  if (GPSECHO)
    if (c) UDR0 = c;  
#endif
}

void loop() {

  if (!usingInterrupt) {
    char c = GPS.read();
    if (GPSECHO)
      if (c) Serial.print(c);
  }

  if (GPS.newNMEAreceived()) {
    if (!GPS.parse(GPS.lastNMEA()))
      return;
  }

  if (timer > millis())  timer = millis();
  if (millis() - timer > 1000) {
    parsingGPS(GPS);

    if(GPS.fix) {
      String jsonGPS = GPSJson(GPS);

      Serial.println();
      Serial.println("===========================================");
      Serial.println("Dati in formato JSON da inviare al webserver: ");
      Serial.println(jsonGPS);
      Serial.println("===========================================");
      Serial.println();
      
      if (client.connect(server, 8080)) {
        Serial.println("===========================================");
        Serial.println("Connected to the server!");
char outBuf[64];

  sprintf(outBuf,"Content-Length: %u\r\n",jsonGPS.length());




          client.print("POST /traffic/webresources/webservice.position HTTP/1.1\r\n");
          client.print("Host: 31.27.101.76:8080\r\n");
          client.print("Content-Type: application/json\r\n");
          client.print(outBuf);
          client.print("Connection: close\r\n\r\n");
          client.print(jsonGPS+"\r\n");
        
        Serial.println("Codice JSON:\n"+jsonGPS);
        
        //client.print("\r\n\r\n");
        Serial.print("Dati Inviati!");
      }
      else {
        Serial.println("===========================================");
        Serial.println("Connection failed!");
      }
/*
      while(client.connected() && !client.available())
       delay(1); //waits for data
       while (client.connected() || client.available()) {
       char c = client.read();
       Serial.write(c);
       }*/

      client.stop();

      Serial.println();
      Serial.println("Disconnecting!");
      Serial.println("===========================================");
      Serial.println();
    }
    timer = millis(); // reset the timer
  }
}
