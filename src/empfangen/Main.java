import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fazecast.jSerialComm.SerialPort;

public class Main
{
    private static final int Key = 3;

    public static void main(String[] args)
    {
        int selection = GetSerialPortSelection(SerialPort.getCommPorts());

        if (selection == -1)
        {
            System.out.println("WARNUNG: Falsche Eingabe - es wurde kein Gerät ausgewählt.");
            System.exit(1);
        }

        // Port öffnen
        SerialPort comPort = SerialPort.getCommPorts()[selection];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

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
                String datenpaket = sensorReader.nextLine();
                Matcher paketMatcher = zeichen.matcher(datenpaket);

                if (paketMatcher.find())
                {
                    String[] werteArray = paketMatcher.group().split("[|]");
                    System.out.println("Temperatur: " + Encryption.caesarDecrypt(werteArray[1], Key) + "\n"
                            + "Luftdruck: " + Encryption.caesarDecrypt(werteArray[2], Key) + "\n");
                }
            }
            sensorReader.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}