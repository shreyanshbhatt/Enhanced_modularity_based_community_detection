package demo.anonymous;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FilterNodesBasedonComms {
  public void considerRelevantData(String groundTruth, String detected) throws Exception {
    List<String> lines_gt = Files.readAllLines(Paths.get(groundTruth));
    List<String> lines_dt = Files.readAllLines(Paths.get(detected));
    
    HashSet<Integer> groundTruthLabsAvail = new HashSet<Integer>();
    for (String line_gt : lines_gt) {
      String splits[] = line_gt.split("\t");
      for (int i = 1; i < splits.length; i++) {
        groundTruthLabsAvail.add(Integer.parseInt(splits[i]));
      }
    }

    BufferedWriter bw = new BufferedWriter(new FileWriter(detected));
    for (String line_dt : lines_dt) {
      String splits[] = line_dt.split("\t");
      ArrayList<String> toUpdate = new ArrayList<String>();
      for (int i = 1; i < splits.length; i++) {
        if (groundTruthLabsAvail.contains(Integer.parseInt(splits[i]))) {
          toUpdate.add(splits[i]);
        }
      }
      if (toUpdate.size() > 0) {
        bw.write(splits[0]);
        for (String founded : toUpdate) {
          bw.write("\t"+founded);
        }
        bw.newLine();
      }
    }
    bw.close();
  }
}
