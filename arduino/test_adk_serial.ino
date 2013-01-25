
// This program is using for sending debug data from serial data to 
// android mobile phone via ADK.
// The USB lib it using is from circuit home. It can be find at here
// https://github.com/felis/USB_Host_Shield_2.0

#include <Usb.h>
#include <adk.h>

USB Usb;
ADK adk(&Usb,
	"TKJElectronics", // Manufacturer Name
	"ArduinoBlinkLED", // Model Name
	"Example sketch for the USB Host Shield", // Description (user-visible string)
	"1.0", // Version
	"http://www.tkjelectronics.dk/uploads/ArduinoBlinkLED.apk", // URL 
	//(web page to visit if no installed apps support the accessory)
	"123456789"); // Serial Number (optional)

#define LED 13 // Pin 13 is occupied by the SCK pin on a normal Arduino (Uno, Duemilanove etc.), so use a different pin

//ADK Communication Frequency (Hz)
#define COMM_FREQ 200
#define BATT_FREQ 0.5

//Main Timer
long timer_comm = millis();
long timer_batt = millis();
uint16_t i=0;

void setup() {
  Serial.begin(115200);
  Serial.setTimeout(1000);
  Serial.print("\r\nADK demo start");
  if (Usb.Init() == -1) {
    Serial.print("\r\nOSCOKIRQ failed to assert");
    while(1); //halt
  }
  pinMode(LED, OUTPUT);
}


void loop() {    
  Usb.Task();
  uint8_t msg[3] = { 0x00 };  
  char sendmsg[3];
  uint8_t rcode;
  uint16_t len;

  if(adk.isReady() && millis()-timer_comm >= 1000/COMM_FREQ) {
    timer_comm = millis();
    len = sizeof(msg);
    rcode = adk.RcvData(&len, msg);
    if(rcode && rcode != hrNAK)
      USBTRACE2("Data rcv. :", rcode);
    
  } 

  if(Serial.available() > 0) {
    Serial.readBytes(sendmsg, 3);
    rcode = adk.SndData(3, (uint8_t *)sendmsg);
    if(rcode) {
       //USBTRACE2("Data send: ", rcode );    
    }
    i++;
  }
}
