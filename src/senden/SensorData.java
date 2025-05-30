public class SensorData
{
    public String temp = "";
    public String pressure = "";

    public SensorData(double temp, double pressure)
    {
        this.temp = String.format("%.2f", temp);
        this.pressure = String.format("%.2f", pressure);
    }
}
