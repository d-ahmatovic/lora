import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;


public class Selection
{
    public static int GetSelection(String[] selectables)
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

    public static int GetSerialPortSelection(SerialPort[] serialPorts)
    {
        String[] selectables = new String[serialPorts.length];
        for (int i = 0; i < serialPorts.length; i++)
        {
            selectables[i] = serialPorts[i].toString();
        }

        return Selection.GetSelection(selectables);
    }
}