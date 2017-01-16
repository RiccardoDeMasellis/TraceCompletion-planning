import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by demas on 13/01/17.
 */
public class PlannerCaller {

    public static final int ITERATION__NUMBER = 1;

    public static String completeTracePlanlength(String encodingFile, String traceName, int planlength, FileWriter fwr){
        Set<String> complete = new HashSet<String>();
        double startTime = System.currentTimeMillis();
        complete = executeDlvk(encodingFile, planlength);
        double endTime = System.currentTimeMillis();

        TraceParser tp = new TraceParser();
        try {
            tp.createTraceXESfile(complete, "out/"+traceName+"_fixedPlanlength_Sol.xes");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fwr.write("Total time: "+ (endTime-startTime));
            fwr.flush();
            fwr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total time: "+ (endTime-startTime) + " ms.");
        return traceName+"_fixedPlanlength_Sol.xes";
    }

    public static String completeTrace(String encodingFile, String traceName, FileWriter fwa, FileWriter fwi){
        int i=0;
        Set<String> complete = new HashSet<String>();
        Set<String> filteredComplete = new HashSet<String>();
        String rootFileName, traceFileName;
        double avgEncodingTime = 0.0;

        try{

            double avgPlanningTime = 0.0;
            for (int j = 0; j < ITERATION__NUMBER; j++) {
                i=0;
                double t1 = System.currentTimeMillis();

                double totalPlanningTime = 0.0;
                boolean first = true;
                boolean oldEmpty = true;
                boolean nextIT = true;
                do{
                    System.out.print("ITERATION "+i+" :");
                    if (!first)
                       fwi.write(";");
                    oldEmpty = complete.isEmpty();
                    complete = new HashSet<String>();
                    double t3 = System.currentTimeMillis();
                    complete = executeDlvk(encodingFile, i++);
                    double t2 = System.currentTimeMillis();
                    System.out.println();
                    fwi.write((t2-t3)+"");
                    first = false;
                    totalPlanningTime+= (t2-t3);
                    if (!filteredComplete.isEmpty())
                        fwa.write("Trace length \t " + filteredComplete.size() + " \t " + totalPlanningTime + "\n");

                } while(complete.isEmpty());
                double t4 = System.currentTimeMillis();
                avgPlanningTime+=(t4-t1);
                fwi.write("\n");
            }
            TraceParser tp = new TraceParser();
            tp.createTraceXESfile(complete, "out/"+traceName+"_Sol.xes");

            avgEncodingTime = avgEncodingTime/ITERATION__NUMBER;
            avgPlanningTime = avgPlanningTime/ITERATION__NUMBER;
            fwa.write( traceName + " \t " +filteredComplete.size()+" \t "+avgEncodingTime+" \t "+avgPlanningTime+"\n");
            fwa.flush();
            fwa.close();
            fwi.flush();
            fwi.close();


        }catch(Exception e){
                e.printStackTrace();
            return null;
        }
        return traceName+"_Sol.xes";
    }


    private static Set<String> executeDlvk(String problem, int plsz){
        Set<String> plans = new HashSet<String>();
        Process p;

        try {
            switch(OsCheck.getOperatingSystemType()) {
                case Windows: p = Runtime.getRuntime().exec(new String[]
                        {"/dlvk/dlv.mingw.exe", problem, "-FPopt", "-planlength=" + plsz});
                break;

                case MacOS: p = Runtime.getRuntime().exec(new String[]
                        {"./dlvk/dlv", problem, "-FPopt", "-planlength=" + plsz});
                break;


                case Linux: p = Runtime.getRuntime().exec(new String[]
                        {"/dlvk/dlv.x86-64-linux-elf-static.bin", problem, "-FPopt", "-planlength=" + plsz});
                break;

                default: throw new RuntimeException("Operative system not recognized. Be a man and use linux!");

            }

            //p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            while ((line = reader.readLine())!= null) {
                if(line.startsWith("PLAN"))
                    //plans.add(line.replaceAll("; \\(no action\\)", "")
                    plans.add(line
                            .replaceAll( ": \\(no action\\);", ":")
                            .replaceAll("PLAN: ",""));
            }

            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = reader.readLine())!= null)
                System.err.println(line);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return plans;
    }


}
