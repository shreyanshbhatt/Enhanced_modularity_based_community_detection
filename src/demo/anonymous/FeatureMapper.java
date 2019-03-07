package demo.anonymous;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class Mapper {
  public int getMappedFeature(String featureFile, HashMap<Integer, Integer> map) throws Exception {
    List<String> lines = Files.readAllLines(Paths.get(featureFile));
    HashSet<String> featuresSeen = new HashSet<String>();
    int current_line_number = 0;
    int current_feature_number = 0;
    
    for (String line : lines) {
      String splits[] = line.split(" ");
      String featNameSplits[] = splits[1].split(";");
      String featName = "";
      for (String featNameSplit : featNameSplits) {
        if (featNameSplit.equals("id") || featNameSplit.equals("anonymized")) {
          break;
        }
        featName += featNameSplit +",";
      }
      if (!featuresSeen.contains(featName)) {
        current_feature_number++;
        featuresSeen.add(featName);
      }
      map.put(current_line_number, current_feature_number);
      current_line_number++;
    }
    
    return (current_feature_number + 1);
  }
}

public class FeatureMapper {
  private static String featureFile = "/Users/bhat236/Downloads/facebook/0.featnames";
  public static void main(String []args) throws Exception {
    Mapper m = new Mapper();
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    m.getMappedFeature(featureFile, map);
    for(int key : map.keySet()) {
      System.out.println(key+" "+map.get(key));
    }
  }
}
