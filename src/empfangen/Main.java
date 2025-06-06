import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fazecast.jSerialComm.SerialPort;


public class Main
{
    private static final int Key = 3;

    public static void main(String[] args)
    {
        int selection = Selection.GetSerialPortSelection(SerialPort.getCommPorts());

        if (selection == -1)
        {
            System.out.println("WARNUNG: Falsche Eingabe - es wurde kein Gerät ausgewählt.");
            System.exit(1);
        }

        // Port öffnen
        SerialPort comPort = SerialPort.getCommPorts()[selection];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (!comPort.isOpen()) {
            System.out.println("FEHLER: Port konnte nicht geöffnet werden!");
            System.exit(1);
        }

        System.out.println("Port erfolgreich geöffnet. Warte auf Daten...");
        Scanner sensorReader = new Scanner(comPort.getInputStream());
        Empfangen(sensorReader);

        sensorReader.close();
        comPort.closePort();
    }

    private static void Empfangen(Scanner sensorReader) {
        try {
            Pattern zeichen = Pattern.compile("OETTI([0-9A-Za-z|.]+)OETTI");
            for (int j = 0; j < 1000; ++j)
            {
                // Prüfen ob Daten verfügbar sind
                if (sensorReader.hasNextLine()) {
                    String datenpaket = sensorReader.nextLine();
                    System.out.println("Empfangen: " + datenpaket);
                    
                    Matcher paketMatcher = zeichen.matcher(datenpaket);

                    if (paketMatcher.find())
                    {
                        String[] werteArray = paketMatcher.group().split("[|]");
                        if (werteArray.length >= 3) {
                            System.out.println("Temperatur: " + Encryption.caesarDecrypt(werteArray[1], Key));
                            System.out.println("Luftdruck: " + Encryption.caesarDecrypt(werteArray[2], Key));
                            System.out.println();
                        }
                    }
                } else {
                    // Kurz warten wenn keine Daten verfügbar sind
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Unterbrochen.");
                        break;
                    }
                }
            }
        } 
        catch (Exception e) {
            System.out.println("Fehler beim Empfangen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}