package hps.nyu.fa14;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SolutionEvaluator {
  private double epsilon = 0.05;
  private String goldFile = "";
  private ArrayList<Double> splitLocations;
  private ArrayList<Boolean> moleculeValidity;
  private ArrayList<Boolean> flipList;
  
  public SolutionEvaluator(String goldFile) {
    this.goldFile = goldFile;
    splitLocations = new ArrayList<Double>();
    moleculeValidity = new ArrayList<Boolean>();
    flipList = new ArrayList<Boolean>();
    init();
  }
  
  private void init() {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(goldFile));
      String line = null;
      int lineNumber = 0;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if(lineNumber == 1) {
          for(String splitLocation:line.split(" ")) {
            double splitLoc = Double.parseDouble(splitLocation);
            splitLocations.add(splitLoc);
          }
        }
        else {
          String [] values = line.split(" ");
          Boolean isTarget = (Integer.parseInt(values[0]) == 1) ? true : false;
          Boolean isFlipped = false;
          if(isTarget) {
            isFlipped = (Integer.parseInt(values[1]) == 1) ? true : false;
          }
          moleculeValidity.add(isTarget);
          flipList.add(isFlipped);
        }
        //line = br.readLine();
      }
      br.close();
    } 
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block      
      e.printStackTrace();
    }
  }
  
  private double calculateFP(ArrayList<Double> locations) {
    double score = 0.0;
    for(Double loc:locations) {
      int valid = 1;
      for(Double location:splitLocations) {
        if(loc >= (location-epsilon) || (loc <= location+epsilon)) {
          valid = 0;
          break;
        }
      }
      score += valid;
    }
    return score;
  }
  
  private double calculateFN(ArrayList<Double> locations) {
    double score = 0.0;
    for(Double location:splitLocations) {
      int valid = 1;
      for(Double loc:locations) {
        if(loc >= (location-epsilon) || (loc <= location+epsilon)) {
          valid = 0;
          break;
        }
      }
      score += valid;
    }
    return score;
  }
  
  private double calculateHammingDistance(ArrayList<Boolean> validity,ArrayList<Boolean> flips) {
    double score = 0.0;
    Iterator<Boolean> validityIterator = validity.iterator();
    Iterator<Boolean> actualValidityIterator = moleculeValidity.iterator();
    Iterator<Boolean> flipsIterator = flips.iterator();
    Iterator<Boolean> actualFlipsIterator = flipList.iterator();
    while(validityIterator.hasNext()) {
      boolean isTarget = validityIterator.next();
      boolean isActualTarget = actualValidityIterator.next();
      boolean isFlipped = flipsIterator.next();
      boolean isActualFlipped = actualFlipsIterator.next();
      if(isTarget != isActualTarget) {
        score += 1;
      }
      else {
        if(isActualTarget && (isFlipped != isActualFlipped)) {
          score += 1;
        }
        else {
          //do nothing
        }
      }
    }
    return score * (80.0/200);
  }
  
  public double evaluateSolution(String solutionFileName) {
    double score = 0.0;
    
    BufferedReader br = null;
    ArrayList<Double> locations = new ArrayList<Double>();
    ArrayList<Boolean> validity = new ArrayList<Boolean>();
    ArrayList<Boolean> flips = new ArrayList<Boolean>();
    try {
      br = new BufferedReader(new FileReader(solutionFileName));
      String line = null;
      int lineNumber = 0;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if(lineNumber == 1) {
          for(String splitLocation:line.split(" ")) {
            double splitLoc = Double.parseDouble(splitLocation);
            locations.add(splitLoc);
          }
        }
        else {
          Boolean isTarget = Boolean.valueOf(line.split(" ")[0]);
          Boolean isFlipped = Boolean.valueOf(line.split(" ")[1]);
          validity.add(isTarget);
          flips.add(isFlipped);
        }
        //line = br.readLine();
      }
      br.close();
    } 
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block      
      e.printStackTrace();
    }
    
    score += calculateFP(locations);
    score += calculateFN(locations);
    score += calculateHammingDistance(validity,flips);
    return score;
  }
}
