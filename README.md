# TraceCompletion-planning

Please add the following main class to src/main/java and use it to launch the software.


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

      /* try {
            FileWriter fwr = new FileWriter(new File("out/stat"));
            PlannerCaller.completeTracePlanlength("/Users/demas/Lavoro/Paperi/DataWFNets-planning/experiments/manual-net-encoding-trace.plan", "prova1", 19, fwr);
        } catch (IOException e) {
            e.printStackTrace();
        } */

        try {
            FileWriter fwa = new FileWriter(new File("out/stat1"));
            FileWriter fwi = new FileWriter(new File("out/stat2"));
            PlannerCaller.completeTrace("/Users/demas/Lavoro/Paperi/DataWFNets-planning/experiments/manual-net-encoding-trace.plan", "prova1", fwa, fwi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
