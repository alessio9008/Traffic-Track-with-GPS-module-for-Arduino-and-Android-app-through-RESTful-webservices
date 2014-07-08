String generateKey(const int len) {
  String s = "";
  static const char alphanum[] =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
  for (int i = 0; i < len; ++i) {
    s += alphanum[random() % (sizeof(alphanum) - 1)];
  }
  
  //Serial.println(sizeof(alphanum));

  return s;
}

String doubleToString(double input,int decimalPlaces) {
  if(decimalPlaces!=0) {
    String string = String((int)(input*pow(10,decimalPlaces)));
    if(abs(input)<1) {
      if(input>0)
        string = "0"+string;
      else if(input<0)
        string = string.substring(0,1)+"0"+string.substring(1);
    }
    return string.substring(0,string.length()-decimalPlaces)+"."+string.substring(string.length()-decimalPlaces);
  } else {
    return String((int)input);
  }
}
