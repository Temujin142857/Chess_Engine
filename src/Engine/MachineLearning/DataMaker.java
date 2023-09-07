package Engine.MachineLearning;

import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataMaker {
    private final int batchSize=100000;
    //needs a method to generate the parameters to the player move method
    //meaning the board, the location1[] and the location2[]
    //format uses rows 0 through 63 to communicate the pieces on the board, and the 64th is the strength
    //each piece will be signified by their name, string, strength will be a double

    public void load_data(String filename) throws IOException {
        System.out.println("Start");
        DefaultDataset data=new DefaultDataset();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(filename));
        for (int i = 0; i < batchSize; i++) {
            String row=bufferedReader.readLine();
            System.out.println(row.toCharArray().length+"hi");
            System.out.println(row);
            System.out.println("ggg");
            double[] att=new double[193];
            int j=0;
            for (char c:row.toCharArray()) {
                if (c!=','){
                    if(j==192){
                        System.out.println(i);
                        if(Character.getNumericValue(c)==1)data.add(new DenseInstance(att,true));
                        else data.add(new DenseInstance(att,false));
                        continue;
                    }
                    att[j]=Character.getNumericValue(c);
                    j++;
                }
            }
        }
        //System.out.println(data);

    }
}
