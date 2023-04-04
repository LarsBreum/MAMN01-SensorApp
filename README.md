# App för kursen: MAMN01 VT2023

## Lars Breum Hansen, la8177br-s

# Beskrivning och syfte

Målet med applikationen var att lära att använda Android Studio och få en introduktion till sensorer.

## Beskrivning

Applikationen består av två skärmar/ activities. En startskärm som möter användaren när appen startas och en "Spel" skärm. För att komma till spelskärmen måste användaren trycka på sidans enda knapp.
Applikationen användar accelerometret till två interaktioner.

1. Datan från accelerometret avläsas och skrivas ut på skärmen i vektor format (x, y, z).
2. Accelerationen i Z-riktningen används till beräkna en hastighet, som att användaren skulle köra en bil framåt. Denna hastigheten skrivas ut på skärmen och jämföras med en hastighetsgräns. Om användaren går över gränsen börjar telefonen att vibrera aggressivt, ge ett meddelande om att gränsen överstigs och färga texten på hastigheten röd. Om hastigheten är nära eller bara lite över gränsen är texten orange, men telefonen vibrera ej.

Interaktion (2) kan användas som bas till en gamifierat körskola eller bil-spel på telefonen. Vari svängninger - dvs. lutning i X och Y riktiningarna då ska inkluderas i logiken.

## Teknisk beskrivning

För att använda accelerometret används Androids inbyggda SensorManager.
https://source.android.com/docs/core/interaction/sensors
Aktiviteten som använder accelerometret implementerar interfacet "SensorEventListener", som kommer med två metoder: onSensorChanged() och OnAccuracyChanged(). Dvs. när en ändring i telefonens acceleration detekteras körs onSensorChanged() och datan hämtas ut från sensorEvent.values vektorn. Det har implementerats ett high-pass filter på Z-riktningen så att stora accelerationer inte medräknas i "bilens" acceleration. Det som skapar bilens accellaration är då tyngdkraften.

För att beräkna hastigheten används Java Instant och Duration klasser.
https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
Det skapas alltså ett Instant object när activity:n startas och varje gång en ändring i accelerometret detekteras. Tids differensen mellan förra Instant och den nuvarande, multiplicerat med den nuvarande acceleration i Z-riktningen läggs då till bilens hastighet. Detta göras i den privata metoden driver().
I driver() finns även logiken för att varna om att användaren kör för fort.

För att få telefonen vibrera används Android inbyggda Vibrator klass.
https://developer.android.com/develop/ui/views/haptics/haptic-feedback#java

För att varna användaren med ljud används MediaPlayer
https://developer.android.com/guide/topics/media/mediaplayer
