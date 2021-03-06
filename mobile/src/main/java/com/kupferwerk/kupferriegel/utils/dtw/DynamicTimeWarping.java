package com.kupferwerk.kupferriegel.utils.dtw;

import java.util.List;

/**
 * (c) Daniel Lemire, 2008 (c) Earlence Fernandes, Vrije Universiteit Amsterdam 2011 This C++
 * library implements dynamic time warping (DTW). This library includes the dynamic programming
 * solution for vectored input signals represented by the class Point. Currently, it has 3
 * dimensions - x, y, z. More can easily be added to this class. No change would be required to the
 * DTW class. Only keep in mind that the distance code has to be updated to accomodate more
 * dimensions. Time series are represented using STL vectors.
 */

public class DynamicTimeWarping {

   public static double dtw(final List<DTWModel> v, final List<DTWModel> w) {
      //     COST MATRIX:
      //   5|_|_|_|_|_|_|E| E = min Global Cost
      //   4|_|_|_|_|_|_|_| S = Start point
      //   3|_|_|_|_|_|_|_| each cell = min global cost to get to that point
      // j 2|_|_|_|_|_|_|_|
      //   1|_|_|_|_|_|_|_|
      //   0|S|_|_|_|_|_|_|
      //     0 1 2 3 4 5 6
      //            i
      //   access is M(i,j)... column-row

      // Check if there is content.
      if (v.size() > 0 && w.size() > 0) {
         // perform Dtw
         double[][] dtw = new double[v.size()][w.size()];
         int maxI = v.size() - 1;
         int maxJ = w.size() - 1;

         // Calculate the values for the first column, from the bottom up.
         dtw[0][0] = v.get(0).distance(w.get(0));
         for (int j = 1; j <= maxJ; j++) {
            dtw[0][j] = dtw[0][j - 1] + v.get(0).distance(w.get(j));
         }

         for (int i = 1; i <= maxI; i++) {  // i = columns
            // Calculate the value for the bottom row of the current column
            //    (i,0) = LocalCost(i,0) + GlobalCost(i-1,0)
            dtw[i][0] = dtw[i - 1][0] + v.get(i).distance(w.get(0));

            for (int j = 1; j <= maxJ; j++) {  // j = rows
               // (i,j) = LocalCost(i,j) + minGlobalCost{(i-1,j),(i-1,j-1),(i,j-1)}
               double minGlobalCost = min(dtw[i - 1][j], dtw[i - 1][j - 1], dtw[i][j - 1]);
               dtw[i][j] = minGlobalCost + v.get(i).distance(w.get(j));
            }  // end for loop
         }  // end for loop

         return dtw[maxI][maxJ];
      } else {
         return Double.MAX_VALUE;
      }
   }

   private static double min(double x, double y) {
      return Math.min(x, y);
   }

   private static double min(double x, double y, double z) {
      return min(min(x, y), z);
   }
}
