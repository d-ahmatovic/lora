import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fazecast.jSerialComm.SerialPort;

public class Main {
    private static final int Key = 3;

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);

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

        // Checken, ob wir senden oder empfangen
        selection = GetEmpfaengerSenderSelection();

        try
        {
            if (selection == -1)
            {
                System.out.println("FEHLER: Falsche Eingabe - man kann entweder Sender oder Empfänger sein!");
                System.exit(2);
            }
            else if (selection == 0) // Sender
            {
                PrintWriter sensorWriter = new PrintWriter(comPort.getOutputStream());
                senden(sensorWriter);
            }
            else
            {
                Scanner sensorReader = new Scanner(comPort.getInputStream());
                empfangen(sensorReader);
            }
        }
        catch (Exception e)
        {
            if (comPort.isOpen())
            {
                comPort.closePort();
            }
        }
    }

    private static int GetSelection(String[] selectables)
    {
        // Vorbereitung der Datenverarbeitung
        Scanner sc = new Scanner(System.in);
        int selection = -1;

        System.out.println("Bitte wählen Sie einen der nachfolgenden seriellen Ports:\n");

        for (int i = 0; i < selectables.length; i++)
        {
            System.out.println("> " + (i + 1) + " | " + selectables[i]);
        }
        System.out.println("");

        // Verarbeitung der Eingabe
        System.out.print("Bitte geben Sie eine der oben genannten Zahlen ein: ");
        try
        {
            int num = sc.nextInt();

            if ((num - 1) < 0 || (num - 1) >= selectables.length)
            {
                System.out.println("FEHLER: Die Eingabe muss zwischen den Grenzen 1 und " + selectables.length + " liegen.");
            }

            selection = num - 1;
        }
        catch(Exception e)
        {
            System.out.println("FEHLER: Ungültige Eingabe! Bitte geben Sie ausschließlich kleine Ganzzahlen ein!");
        }

        // Rückgabe der Auswahl, ggf. aber auch -1
        return selection;
    }

    private static int GetEmpfaengerSenderSelection()
    {
        String[] selectables = new String[] { "Sender", "Empfänger" };
        return GetSelection(selectables);
    }

    private static int GetSerialPortSelection(SerialPort[] serialPorts)
    {
        String[] selectables = new String[serialPorts.length];
        for (int i = 0; i < serialPorts.length; i++)
        {
            selectables[i] = serialPorts[i].toString();
        }

        return GetSelection(selectables);
    }

    private static void senden(PrintWriter sensorWriter) throws InterruptedException {
        try
        {
            for (int i = 0; i < 1000; i++)
            {
                SensorData sensorData = Sensor.GetData();
                String payload = String.format("OETTI|%s|%s|OETTI\n",
                        Encryption.caesarEncrypt(sensorData.temp, Key),
                        Encryption.caesarEncrypt(sensorData.pressure, Key));
                sensorWriter.println(payload);
                sensorWriter.flush();
                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void empfangen(Scanner sensorReader) {
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