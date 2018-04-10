package edu.bk.thesis.biodiary.core.voice;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.dtw.DTWSimilarity;

import java.util.ArrayList;


public class DTWcustom extends DTWSimilarity
{
    private double pointDistance(int i, int j, double[][] ts1, double[][] ts2)
    {
        double diff = 0;
        for (int k = 0; k < 13; ) {
            diff = diff + (ts1[i][k] - ts2[j][k]) * (ts1[i][k] - ts2[j][k]);
            k++;
        }
        return diff;
    }

    private double distance2Similarity(double x)
    {
        return (1.0 - (x / (1 + x)));
    }

    @Override
    public double measure(Instance x, Instance y)
    {

        ArrayList<ArrayList<Double>> l1 = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> l2 = new ArrayList<ArrayList<Double>>();
        int                          i, j;

        /** Filter NaNs */
        for (i = 0; i < x.noAttributes() / 13; ) {
            ArrayList<Double> temp = new ArrayList<Double>();
            for (j = 0; j < 13; ) {
                double value = x.value(i * 13 + j);
                if (!Double.isNaN(value)) {
                    temp.add(value);
                }
                if (j == 12) {
                    l1.add(temp);
                }
                j++;
            }
            i++;
        }

        for (i = 0; i < y.noAttributes() / 13; ) {
            ArrayList<Double> temp = new ArrayList<Double>();
            for (j = 0; j < 13; ) {
                double value = y.value(i * 13 + j);
                if (!Double.isNaN(value)) {
                    temp.add(value);
                }
                if (j == 12) {
                    l2.add(temp);
                }
                j++;
            }
            i++;
        }

        /** Transform the examples to vectors */
        double[][] ts1 = new double[l1.size()][13];
        double[][] ts2 = new double[l2.size()][13];

        for (i = 0; i < ts1.length; ) {
            for (j = 0; j < 13; ) {
                ts1[i][j] = l1.get(i).get(j);
                j++;
            }
            i++;
        }

        for (i = 0; i < ts2.length; ) {
            for (j = 0; j < 13; ) {
                ts2[i][j] = l2.get(i).get(j);
                j++;
            }
            i++;
        }

        /** Build a point-to-point distance matrix */
        double[][] dP2P = new double[ts1.length][ts2.length];

        for (i = 0; i < ts1.length; ) {
            for (j = 0; j < ts2.length; ) {
                dP2P[i][j] = this.pointDistance(i, j, ts1, ts2);
                j++;
            }
            i++;
        }


        if (ts1.length != 0 && ts2.length != 0) {
            if (ts1.length == 1 && ts2.length == 1) {
                return this.distance2Similarity(Math.sqrt(dP2P[0][0]));
            }
            else {
                double[][] D = new double[ts1.length][ts2.length];
                D[0][0] = dP2P[0][0];

                for (i = 1; i < ts1.length; ++i) {
                    D[i][0] = dP2P[i][0] + D[i - 1][0];
                }

                double sum;
                if (ts2.length == 1) {
                    sum = 0.0D;

                    for (i = 0; i < ts1.length; ++i) {
                        sum += D[i][0];
                    }

                    return this.distance2Similarity(Math.sqrt(sum) / (double) ts1.length);
                }
                else {
                    for (j = 1; j < ts2.length; ++j) {
                        D[0][j] = dP2P[0][j] + D[0][j - 1];
                    }

                    if (ts1.length == 1) {
                        sum = 0.0D;

                        for (j = 0; j < ts2.length; ++j) {
                            sum += D[0][j];
                        }

                        return this.distance2Similarity(Math.sqrt(sum) / (double) ts2.length);
                    }
                    else {
                        double dist;
                        for (i = 1; i < ts1.length; ++i) {
                            for (j = 1; j < ts2.length; ++j) {
                                double[] steps = new double[]{ D[i - 1][j - 1], D[i - 1][j],
                                                               D[i][j - 1] };
                                dist = Math.min(steps[0], Math.min(steps[1], steps[2]));
                                D[i][j] = dP2P[i][j] + dist;
                            }
                        }

                        i = ts1.length - 1;
                        j = ts2.length - 1;
                        int k = 1;

                        for (dist = D[i][j]; i + j > 2; dist += D[i][j]) {
                            if (i == 0) {
                                --j;
                            }
                            else if (j == 0) {
                                --i;
                            }
                            else {
                                double[] steps = new double[]{ D[i - 1][j - 1], D[i - 1][j],
                                                               D[i][j - 1] };
                                double min = Math.min(steps[0], Math.min(steps[1], steps[2]));
                                if (min == steps[0]) {
                                    --i;
                                    --j;
                                }
                                else if (min == steps[1]) {
                                    --i;
                                }
                                else if (min == steps[2]) {
                                    --j;
                                }
                            }

                            ++k;
                        }

                        return this.distance2Similarity(Math.sqrt(dist) / (double) k);
                    }
                }
            }
        }
        else {
            return 0.0D / 0.0;
        }
    }
}
