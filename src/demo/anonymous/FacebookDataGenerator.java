package demo.anonymous;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

class DataGenerator {
  HashMap<Integer, Integer> featureMap;
  HashMap<Integer, ArrayList<Integer>> featureVector;
  int actualFeatures;
  String featureFileName;
  int id;
  String edgesFileName;
  String featureNames;
  String circles;
  HashMap<Integer, Integer> idMapper;

  public DataGenerator(String folder, int userId) {
    featureVector = new HashMap<Integer, ArrayList<Integer>>();
    featureFileName = folder+userId+".feat";
    edgesFileName = folder+userId+".edges";
    featureNames = folder+userId+".featnames";
    circles = folder+userId+".circles";
    idMapper = new HashMap<Integer, Integer>();
    featureMap = new HashMap<Integer, Integer>();
    id = userId;
  }

  private boolean isAMatch(Integer fromFeature, Integer toFeature) {
    if (fromFeature == 1 && toFeature == 1) {
      return true;
    }
    return false;
  }

  public ArrayList<Integer> getDistance(int fromId, int toId) {
    ArrayList<Integer> distanceValues = new ArrayList<Integer>();
    for (int i = 0; i < actualFeatures; i++) {
      distanceValues.add(0);
    }
    ArrayList<Integer> fromVector = featureVector.get(fromId);
    ArrayList<Integer> toVector = featureVector.get(toId);

    for (int i = 0; i < fromVector.size(); i++) {
      if (isAMatch(fromVector.get(i), toVector.get(i))) {
        int featureMatched = featureMap.get(i);
        distanceValues.set(featureMatched, 1);
      }
    }

    return distanceValues;
  }

  private void fillFeatureVector() throws Exception {
    List<String> lines = Files.readAllLines(Paths.get(featureFileName));
    for (String line : lines) {
      String splits[] = line.split(" ");
      int uid = Integer.parseInt(splits[0]);
      ArrayList<Integer> vector = new ArrayList<Integer>();
      for (int i = 1; i < splits.length; i++) {
        vector.add(Integer.parseInt(splits[i]));
      }
      featureVector.put(uid, vector);
    }
  }

  public void generateData() throws Exception {

    actualFeatures = new Mapper().getMappedFeature(featureNames, featureMap);
    System.out.println(featureMap.size());
    fillFeatureVector();
    List<String> edges = Files.readAllLines(Paths.get(edgesFileName));
    
    BufferedWriter bw = new BufferedWriter(new FileWriter("edges"+id));

    int new_id_starter = 0;

    for (String edge : edges) {
      String splits[] = edge.split(" ");
      int fromId = Integer.parseInt(splits[0]);
      int toId = Integer.parseInt(splits[1]);
      ArrayList<Integer> distances = getDistance(fromId, toId);

      int newFromId = 0;
      int newToId = 0;

      if (idMapper.containsKey(fromId)) {
        newFromId = idMapper.get(fromId);
      } else {
        newFromId = new_id_starter;
        idMapper.put(fromId, newFromId);
        new_id_starter++;
      }

      if (idMapper.containsKey(toId)) {
        newToId = idMapper.get(toId);
      } else {
        newToId = new_id_starter;
        idMapper.put(toId, newToId);
        new_id_starter++;
      }

      bw.write(""+newFromId);
      for (int distance : distances) {
        bw.write(" "+distance);
      }
      
      bw.write(" "+newToId);
      bw.newLine();
    }

    int [][]connView = new int[new_id_starter][new_id_starter];
    TreeMap<Integer, ArrayList<Integer>> featView = new TreeMap<Integer, ArrayList<Integer>>();

    for (String edge : edges) {
      String splits[] = edge.split(" ");
      int fromId = Integer.parseInt(splits[0]);
      int toId = Integer.parseInt(splits[1]);
      int newFromId = idMapper.get(fromId);
      int newToId = idMapper.get(toId);
      featView.put(newFromId, featureVector.get(fromId));
      featView.put(newToId, featureVector.get(toId));
      connView[newFromId][newToId] = 1;
      connView[newToId][newFromId] = 1;
    }
    
    bw.close();
  }
}

public class FacebookDataGenerator {
  private static String folder = "facebook/";
  private static int userId = 414;

  public void genData(int userIdToUse) throws Exception {
    DataGenerator dg = new DataGenerator(folder, userIdToUse);
    dg.generateData();    
  }

  public static void main(String []args) throws Exception {
    DataGenerator dg = new DataGenerator(folder, userId);
    dg.generateData();
  }
}
