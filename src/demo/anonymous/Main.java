package demo.anonymous;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
  // The graph has to have the vertex ids from 0 - num_vertices - 1
  // The graph has to be undirected graph
  public static String id = "";
  public static int outIters;
  public static int innerIters;
  public static int mainIters;
  public static boolean printAreaScore;
  public static double initWeight = 0.5;
  public static double wn;
  public static double lambda = 0.0000001;
  //public static String graphFile = "/Users/bhat236/Documents/untitled folder/CommunityDetection/edges"+id;
  public static String graphFile;
  public static String extra;

  private static int getNumTopics(String graphFile) throws IOException {
    return Files.readAllLines(Paths.get(graphFile)).get(0).split(" ").length - 2;
  }
  /*
   * 0 0  0 0  ___  0 0 0 0
   * 1 0  3 1  ___  1 0 2 1
   * 2 0  5 2  ___  2 2
   * 3 3
   * 4 3
   * 5 5
   */
  private static int getNumVertices(String graphFile) throws IOException {
    int num_vertices = 0;
    List<String> lines = Files.readAllLines(Paths.get(graphFile));
    for (String line : lines) {
      String splits[] = line.split(" ");
      int start_node = Integer.parseInt(splits[0]);
      int end_node = Integer.parseInt(splits[splits.length - 1]);
      if (start_node > num_vertices) {
        num_vertices = start_node;
      }
      if (end_node > num_vertices) {
        num_vertices = end_node;
      }
    }
    return (num_vertices + 1);
  }

  public void computeClusters() throws Exception {
    //NormalizeInput ni = new NormalizeInput();
    //graphFile = ni.normalizeInput(graphFile);

    int num_topics = getNumTopics(graphFile);
    int num_vertices = getNumVertices(graphFile);
    double []comm_weights = new double[num_topics];
    for (int i = 0; i < num_topics; i++) {
      comm_weights[i] = Main.initWeight;
    }
    GradientDescent gd = new GradientDescent(num_vertices, num_topics);
    Graph g = new Graph(num_vertices, num_topics, comm_weights, graphFile, gd, extra);
    for (int i = 0; i < mainIters; i++) {
      g.startProcessWithPhases(i);
      gd.performGradientDescent(g);
    }
  }

  public static void main(String []args) throws Exception {
  }
}
