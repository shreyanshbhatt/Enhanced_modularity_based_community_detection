package demo.anonymous;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import scala.Tuple2;


public class ScriptToEvaluateTwitter {

  private static Tuple2<Double, Double> processFile(
      List<String> lines, String fileName, int []defender_indexes) throws Exception {

    TreeMap<String, HashSet<String>> circleToNames = new TreeMap<String, HashSet<String>>();

    TreeMap<String, HashSet<String>> circleToNamesGt = new TreeMap<String, HashSet<String>>();

    circleToNames.put("defender", new HashSet<String>());
    circleToNames.put("forward", new HashSet<String>());
    circleToNames.put("midfielder", new HashSet<String>());

    circleToNamesGt.put("defender", new HashSet<String>());
    circleToNamesGt.put("forward", new HashSet<String>());
    circleToNamesGt.put("midfielder", new HashSet<String>());

    HashMap<String, String> user_to_affinity = new HashMap<String, String>();

    List<String> lines_gt = Files.readAllLines(Paths.get("twitter/circle_info.tsv"));

    for (String line_gt : lines_gt) {
      String splits[] = line_gt.split("\t");
      for (int i = 1; i < splits.length; i++) {
        user_to_affinity.put(splits[i], splits[0]);
        circleToNamesGt.get(splits[0]).add(splits[i]);
      }
    }
    HashSet<String> toPut = null;
    double []counts = new double[3];
    int []totalTrue = new int[3];

    String curAff = "";
    int index = 0;
    for (String line : lines) {
      if (line.startsWith("cluster")) {
        String splits[] = line.split(",");
        double a1 = Double.parseDouble(splits[defender_indexes[0]]);
        double a2 = Double.parseDouble(splits[defender_indexes[1]]);
        double a3 = Double.parseDouble(splits[defender_indexes[2]]);
        if (Double.compare(a1, a2) > 0 && Double.compare(a1, a3) > 0) {
          toPut = circleToNames.get("defender");
          curAff = "defender";
          index = 0;
        }
        else if (Double.compare(a2, a1) > 0 && Double.compare(a2, a3) > 0) {
          toPut = circleToNames.get("forward");
          curAff = "forward";
          index = 1;
        }
        else if (Double.compare(a3, a1) > 0 && Double.compare(a3, a2) > 0) {
          toPut = circleToNames.get("midfielder");
          curAff = "midfielder";
          index = 2;
        }
        else if (a1 == a2 && a2 == a3) {
          toPut = null;
          index = -1;
          curAff = "";
        }
        else {
          System.out.println(a1+","+a2+","+a3);
        }
        continue;
      }
      if (index == -1) {
        continue;
      }
      if (user_to_affinity.get(line) == null) {
        continue;
      }
      String gt_aff = user_to_affinity.get(line);
      if (gt_aff.equals("defender")) {
        counts[0]++;
      }
      if (gt_aff.equals("forward")) {
        counts[1]++;
      }
      if (gt_aff.equals("midfielder")) {
        counts[2]++;
      }
      if (gt_aff.equals(curAff)) {
        if (gt_aff.equals("defender")) {
          totalTrue[0]++;
        }
        if (gt_aff.equals("forward")) {
          totalTrue[1]++;
        }
        if (gt_aff.equals("midfielder")) {
          totalTrue[2]++;
        }
      }
      else {
        //System.out.println(line+","+gt_aff+","+curAff);
      }
      toPut.add(line);
    }

    CommunityFMeasureCesna cfc = new CommunityFMeasureCesna();
    CommunityJaccard cjc = new CommunityJaccard();
    double fscore = cfc.measureFScoreGplus(fileName, "twitter/circle_info.tsv");
    double jscore = cjc.getJaccardGplus(fileName, "twitter/circle_info.tsv");
    return new Tuple2<Double, Double>(fscore, jscore);
  }


  public static void main(String args[]) throws Exception {
    int outIters[] = {3};
    HashMap<String, HashSet<String>> playerInfo = new HashMap<String, HashSet<String>>();
    String inputFile = "./twitter/twitter_user_edges.txt";
    int innerIters [] = {50, 100};
    int mainIters [] = {1, 3};
    double wns[] = {0.5, 1.0};
    BufferedWriter bwfinal = new BufferedWriter(new FileWriter("results_twitter_dataset.csv"));
    Main m = new Main();
    for (int outIter : outIters) {
      for (int mainIter : mainIters) {
        for (int innerIter : innerIters) {
          for (double wn : wns) {
            Main.id = "twitter";
            Main.outIters = outIter;
            Main.innerIters = innerIter;
            Main.mainIters = mainIter;
            Main.wn = wn;
            Main.graphFile = inputFile;
            String fileNameEx = "twitter"+"_"+outIter+"_"+mainIter+"_"+innerIter+"_"+wn;
            Main.extra = "edges_"+fileNameEx;
            Main.printAreaScore = true;
            m.computeClusters();

            List<String> community_lines = Files.readAllLines(Paths.get("foundLabelsedges_"+fileNameEx));
            List<String> user_names = Files.readAllLines(Paths.get("twitter/node_features_revised_ids.csv"));
            HashMap<Integer, String> id_to_name = new HashMap<Integer, String>();
            for (int i = 0; i < user_names.size(); i++) {
              id_to_name.put(i, user_names.get(i).split(",")[0]);
            }
            HashMap<String, HashSet<String>> combined_circles = new HashMap<String, HashSet<String>>();
            combined_circles.put("defender", new HashSet<String>());
            combined_circles.put("forwards", new HashSet<String>());
            combined_circles.put("mid", new HashSet<String>());
            for (String community_line : community_lines) {
              String splits[] = community_line.split("\t");
              double a1 = Double.parseDouble(splits[splits.length-3]);
              double a2 = Double.parseDouble(splits[splits.length-2]);
              double a3 = Double.parseDouble(splits[splits.length-1]);
              String cname = "";
              if (Double.compare(a1, a2) > 0 && Double.compare(a1, a3) > 0) {
                cname = "defender";
              }
              else if (Double.compare(a2, a1) > 0 && Double.compare(a2, a3) > 0) {
                cname = "forwards";
              }
              else if (Double.compare(a3, a1) > 0 && Double.compare(a3, a2) > 0) {
                cname = "mid";
              }
              for (int i = 1; i < splits.length - 3; i++) {
                combined_circles.get(cname).add(id_to_name.get(Integer.parseInt(splits[i])));
              }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter("foundLabelsedges_"+fileNameEx+".txt"));
            for (String key : combined_circles.keySet()) {
              bw.write(key);
              for (String split : combined_circles.get(key)) {
                if (!playerInfo.containsKey(split)) {
                  HashSet<String> newOne = new HashSet<String>();
                  newOne.add(key);
                  playerInfo.put(split, newOne);
                }
                else {
                  playerInfo.get(split).add(key);
                }
                bw.write("\t"+split);
              }
              bw.newLine();
            }
            bw.close();

            List<String> lines = Files.readAllLines(Paths.get("foundLabelsedges_"+fileNameEx+".txt"));

            int defender_indexes[] = {1, 2, 3};
            Tuple2<Double, Double> scores = processFile(lines, "foundLabelsedges_"+fileNameEx+".txt", defender_indexes);
            new File("foundLabelsedges_"+fileNameEx+".txt").delete();
            bwfinal.write(outIter+","+mainIter+","+wn+","+scores._1+","+scores._2);
            bwfinal.newLine();
          }
        }
      }
    }
    bwfinal.close();
    
  }
}
