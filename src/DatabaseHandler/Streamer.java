package DatabaseHandler;

import java.io.*;

public class Streamer {

    public void pgnTodata(String filename, String ouptuFile){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(filename));
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(ouptuFile));
            String line;
            String outputLine = "";
            char[] linec;
            while(bufferedReader.ready()){
                outputLine="";
                line= bufferedReader.readLine();
                if(line.equals("")||line.charAt(0)=='['&&line.charAt(2)!='e'){
                    continue;
                }
                linec=line.toCharArray();
                boolean braceFlag=false;
                for (int i = 0; i < linec.length; i++) {
                    if(linec[i]=='{'){braceFlag=true;}
                    else if(linec[i]=='}'){braceFlag=false;continue;}
                    if(braceFlag){continue;}
                    outputLine+=linec[i];
                }
                bufferedWriter.write(outputLine+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
