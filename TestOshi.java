import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class TestOshi {
    public static void main(String[] args) {
        HardwareAbstractionLayer hal = new SystemInfo().getHardware();
        
        System.out.println("CPU:");
        var proc = hal.getProcessor();
        System.out.println(proc.getPhysicalProcessorCount() + " Physical, " + proc.getLogicalProcessorCount() + " Logical Cores @ " + (proc.getMaxFreq() / 1000000000.0) + " GHz");

        System.out.println("\nDisplays:");
        hal.getDisplays().forEach(d -> System.out.println(d.toString()));

        System.out.println("\nPower:");
        hal.getPowerSources().forEach(p -> System.out.println("Capacity: " + p.getCurrentCapacity() + "/" + p.getMaxCapacity() + " (Plugged in: " + p.isPowerOnLine() + ") - Health: " + p.getDesignCapacity()));
    }
}
