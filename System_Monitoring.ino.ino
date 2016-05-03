/*  Development by Yujum Studio
 *  Sistem Monitoring Temperature, Humidity, DewPoint 
 *  more information please visit : www.studio.yujum.com
 */

#include <Sensirion.h>    // Library untuk jenis sensor yang digunakan


// Constanta penggunaan Pin sensor SHT11
const uint8_t dataPin  =  2;       // perintah untuk menentukan posisi pin DATA yang digunakan untuk sensor SHT11 PIN A2 pada Arduino
const uint8_t clockPin =  3;       // perintah untuk menentukan posisi pin SINYAL CLOCK yang digunakan untuk sensor SHT11 PIN A3 pada Arduino

// Variabel untuk relay yang digunakan
int Pompa  = 4;      // perintah untuk menentukan posisi pin RELAY 1 yang digunakan untuk Relay 1 pada PIN 4
int Heater  = 5;      // perintah untuk menentukan posisi pin RELAY 2 yang digunakan untuk Relay 2 pada PIN 5

// Variabel untuk Sensor Suhu SHT11
float temperature;  // Variabel untuk temperature (Suhu)
float humidity;     // Variabel untuk humidity (Kelembapan)  
float dewpoint;     // Variabel untuk dewpoint (Titik Embun)

// Data Serial Temporary
int dataIn=0, dataBuff=0;

// Menggunakan Sensor
Sensirion Sht11 = Sensirion(dataPin, clockPin); // initialisasi port yang digunakan

float dataSuhu,dataHumidity;

void setup()
{
  Serial.begin(9600);   // Memulai untuk mengirim menggunakan data serial
  pinMode(Pompa, OUTPUT);
  pinMode(Heater, OUTPUT);
}


void loop()
{ 
digitalWrite(Pompa,HIGH);
digitalWrite(Heater,HIGH);
 
Sht11.measure(&temperature, &humidity, &dewpoint);    // perintah untuk mengukur suhu, kelembaban, dan titik embun 

  // Mengirim Data Temperature
  //Serial.print("#");
  //Serial.print("temperature : ");
  Serial.print(temperature);        // Mengirim pengukuran sensor temperature
  Serial.print(";");
  //Serial.println("");

  // Mengirim Dara Humidity
  //Serial.print("Humidity : ");
  Serial.print(humidity);           // Mengirim hasil pengukuran humidity       
  //Serial.print("%");
  //Serial.print(";");
  
  // Mengirim titik embun
  //Serial.print("DewPoint : ");
  //Serial.print(dewpoint);           // Mengirim hasil pengukuran dewpoint
  //Serial.print("C");
  //Serial.print(";");
  Serial.println("");
  
  delay(2000);

if (Serial.find('a')){
      dataIn = Serial.parseInt();
      dataBuff= Serial.parseInt();
      
      // Baca Data Serial
      //Serial.print("Data Max : ");
      //Serial.print(dataIn);
      //Serial.println();
      //Serial.print("Data Buff : ");
      //Serial.print(dataBuff);
      //Serial.println();
        
        if (dataIn == 1&&dataBuff>temperature){
                     digitalWrite(Pompa,LOW);       
        }

        else if (dataIn == 1&&dataBuff<temperature){
                     digitalWrite(Pompa,HIGH);       
        }

        else if (dataIn==3&&dataBuff>humidity){
                     digitalWrite(Heater,LOW);   
        }

        else if (dataIn==3&&dataBuff<humidity){
                     digitalWrite(Heater,HIGH);   
        }
  }
}
