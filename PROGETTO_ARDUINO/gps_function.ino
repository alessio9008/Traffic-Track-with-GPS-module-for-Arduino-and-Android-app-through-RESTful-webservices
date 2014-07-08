void useInterrupt(boolean v) {
  //Serial.println("useInterrupt");
  if (v) {
    OCR0A = 0xAF;
    TIMSK0 |= _BV(OCIE0A);
    usingInterrupt = true;
  } else {
    TIMSK0 &= ~_BV(OCIE0A);
    usingInterrupt = false;
  }
}

void parsingGPS(Adafruit_GPS GPS) {
  Serial.print("\nTime: ");
  Serial.print(GPS.hour, DEC); Serial.print(':');
  Serial.print(GPS.minute, DEC); Serial.print(':');
  Serial.print(GPS.seconds, DEC); Serial.print('.');
  Serial.println(GPS.milliseconds);
  
  Serial.print("Date: ");
  Serial.print(GPS.day, DEC); Serial.print('/');
  Serial.print(GPS.month, DEC); Serial.print("/20");
  Serial.println(GPS.year, DEC);
  
  Serial.print("Fix: "); Serial.print((int)GPS.fix);
  Serial.print(" quality: "); Serial.println((int)GPS.fixquality); 
  
  if (GPS.fix) {
    Serial.print("Location: ");
    Serial.print(GPS.latitude, 4); Serial.print(GPS.lat);
    Serial.print(", "); 
    Serial.print(GPS.longitude, 4); Serial.println(GPS.lon);
    
    Serial.print("Speed (knots): "); Serial.println(GPS.speed);
    Serial.print("Angle: "); Serial.println(GPS.angle);
    Serial.print("Altitude: "); Serial.println(GPS.altitude);
    Serial.print("Satellites: "); Serial.println((int)GPS.satellites);
  }
  else{
    Serial.print("Satellites: "); Serial.println((int)GPS.satellites);
  }
}

String GPSJson(Adafruit_GPS GPS) {
  static char lat[10];
  double degree=0;
  double latitude=0, longitude=0;
  
  latitude=((double)GPS.latitude)/100;
      degree=trunc(latitude);
      latitude=latitude-degree;
      latitude=latitude/0.6;
      latitude+=degree;
  
  dtostrf(latitude, 1, 6, lat);
  
  static char lon[10];
  longitude=((double)GPS.longitude)/100;
   degree=trunc(longitude);
      longitude=longitude-degree;
      longitude=longitude/0.6;
      longitude+=degree;
  
  dtostrf(longitude, 1, 6, lon);

     
  static char altitude[10];
  static char spd[10];
  static char angle[10];
  dtostrf(GPS.altitude, 1, 2, altitude);
  dtostrf(GPS.speed, 1, 2, spd);
  dtostrf(GPS.angle, 1, 2, angle);
  
  //String lat = doubleToString(GPS.latitude, 4);
  //String lon = doubleToString(GPS.longitude, 4);
  //String angle = doubleToString(GPS.angle, 2);
  //String altitude = doubleToString(GPS.altitude, 2);
  //String spd = doubleToString(GPS.speed, 2);
      
  String s = "{\"positionPK\":{\"id\":\"";
      s += arduinoKey;
      /*s +="\",\"hour\":\"";
      s += GPS.hour;
      s +="\",\"mim\":\"";
      s += GPS.minute;
      s +="\",\"sec\":\"";
      s += GPS.seconds;
      s +="\",\"day\":\"";
      s += GPS.day;
      s +="\",\"month\":\"";
      s += GPS.month;
      s +="\",\"year\":\"";
      s += GPS.year;*/
      
      s +="\",\"timestamp\":";
      s += millis();
      s +="}";
      //s +="\",\"fix\":\"";
      //s += GPS.fix;
      //s +="\",\"quality\":\"";
      //s += GPS.fixquality;
      s +=",\"lat\":";
      s += lat;
      s +=",\"lt\":\"";
      s += GPS.lat;
      s +="\",\"lon\":";
      s += lon;
      s +=",\"ln\":\"";
      s += GPS.lon;
      s +="\",\"speed\":";
      s += spd;
      s +=",\"angle\":";
      s += angle;
      s +=",\"altitude\":";
      s += altitude;
      //s +="\",\"satellites\":\"";
      //s += GPS.satellites;
      s +="}";
      
      return s;
}
