package demo.anonymous;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class ScriptToEvaluate {
  public static void main(String args[]) throws Exception {
    int ids[] = {0, 1684, 1912, 3980, 414, 698};
    int outIters[] = {5};
    int innerIters [] = {50};
    int mainIters [] = {3};
    double wns[] = {0.5,1.0,2.0};
    BufferedWriter bw = new BufferedWriter(new FileWriter("results_all_fb_new_nonoverlap.csv"));
    bw.write("id,outIter,mainIter,innerIter,wn,fMeasure,fmeasureCesna,jMeasure");
    bw.newLine();
    FacebookDataGenerator fd = new FacebookDataGenerator();
    Main m = new Main();
    for (int id : ids) {
      fd.genData(id);
      for (int outIter : outIters) {
        for (int mainIter : mainIters) {
          for (int innerIter : innerIters) {
            for (double wn : wns) {
              Main.id = ""+id;
              Main.outIters = outIter;
              Main.innerIters = innerIter;
              Main.mainIters = mainIter;
              Main.wn = wn;
              Main.graphFile = "edges"+id;
              String extra = "fbedges_"+id+"_"+wn+"_"+innerIter+"_"+mainIter+"_"+outIter;
              Main.extra = extra;
              m.computeClusters();
              String detFile = "foundLabels"+extra;
              CommunityFMeasureCesna fcfc = new CommunityFMeasureCesna();
              double fMeasure_second = fcfc.measureFScore(id, detFile);
              CommunityJaccard cjc = new CommunityJaccard();
              double jMeasure = cjc.getJaccard(id, detFile);
              new File("foundLabels"+extra).delete();
              bw.write(id+","+outIter+","+mainIter+","+innerIter+","+wn+","+fMeasure_second+","+jMeasure);
              bw.newLine();
            }
          }
        }
      }
    }
    bw.close();
  }
}
