import java.util.Scanner;
import java.io.PrintWriter;
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

        PrintWriter sensorWriter = new PrintWriter(comPort.getOutputStream());
        try {
            Senden(sensorWriter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sensorWriter.close();
            comPort.closePort();
        }
    }

    private static void Senden(PrintWriter sensorWriter) throws InterruptedException {
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
}