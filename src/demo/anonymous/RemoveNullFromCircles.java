package demo.anonymous;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RemoveNullFromCircles {
  public static void main(String []args) throws Exception {
    for (final File fileEntry : new File("./gplus_data/").listFiles()) {
      if (!fileEntry.getName().endsWith(".circles_sequenced")) {
        continue;
      }
      List<String> lines_crs = Files.readAllLines(Paths.get(fileEntry.getAbsolutePath()));
      BufferedWriter bw = new BufferedWriter(new FileWriter(fileEntry.getAbsolutePath()));
      for (String line_cr : lines_crs) {
        String splits[] = line_cr.split("\t");
        ArrayList<String> filteredlabels = new ArrayList<String>();
        for (int i = 1; i < splits.length; i++) {
          if (splits[i].equals("null")) {
            continue;
          }
          filteredlabels.add(""+Integer.parseInt(splits[i]));
        }
        if (filteredlabels.size() > 0) {
          bw.write(splits[0]);
          for (String lab : filteredlabels) {
            bw.write("\t"+lab);
          }
          bw.newLine();
        }
      }
      bw.close();
    }
  }
}
