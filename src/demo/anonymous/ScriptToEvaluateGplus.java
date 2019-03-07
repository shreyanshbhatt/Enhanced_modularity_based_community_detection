package demo.anonymous;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class ScriptToEvaluateGplus {
  public static void main(String args[]) throws Exception {
    int outIters[] = {10};
    String inputFolder = "./gplus_all/";
    int innerIters [] = {100};
    int mainIters [] = {1, 3};
    double wns[] = {1.0};
    BufferedWriter bw = new BufferedWriter(new FileWriter("results_all_gplus.csv"));
    bw.write("id,outIter,mainIter,innerIter,wn,fMeasure,fmeasureCesna,jMeasure");
    bw.newLine();
    Main m = new Main();
    for (final File fileEntry : new File(inputFolder).listFiles()) {
      if (!fileEntry.getName().endsWith(".edges_annotated_sequenced")) {
        continue;
      }
      String userid = fileEntry.getName().split("\\.")[0];
      System.out.println(fileEntry.getName());
      for (int outIter : outIters) {
        for (int mainIter : mainIters) {
          for (int innerIter : innerIters) {
            for (double wn : wns) {
              Main.id = ""+userid;
              Main.outIters = outIter;
              Main.innerIters = innerIter;
              Main.mainIters = mainIter;
              Main.wn = wn;
              Main.graphFile = fileEntry.getAbsolutePath();
              String fileNameEx = ""+userid+"_"+outIter+"_"+mainIter+"_"+innerIter+"_"+wn;
              Main.extra = "edges_"+fileNameEx;
              m.computeClusters();
              FilterNodesBasedonComms fnbc = new FilterNodesBasedonComms();
              fnbc.considerRelevantData(inputFolder+"/"+userid+".circles_sequenced", "foundLabelsedges_"+fileNameEx);
              CommunityFMeasureCesna fcfc = new CommunityFMeasureCesna();
              double fMeasure_second = fcfc.measureFScoreGplus("foundLabelsedges_"+fileNameEx, inputFolder+"/"+userid+".circles_sequenced");
              CommunityJaccard cjc = new CommunityJaccard();
              double jMeasure = cjc.getJaccardGplus("foundLabelsedges_"+fileNameEx, inputFolder+"/"+userid+".circles_sequenced");
              new File("foundLabelsedges_"+fileNameEx).delete();
              bw.write(userid+","+outIter+","+mainIter+","+innerIter+","+wn+","+fMeasure_second+","+jMeasure);
              System.out.println(fMeasure_second+" "+jMeasure);
              bw.newLine();
            }
          }
        }
      }
    }
    bw.close();

  }
}
