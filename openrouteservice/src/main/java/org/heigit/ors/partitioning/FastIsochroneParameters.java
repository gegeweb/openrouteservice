package org.heigit.ors.partitioning;



public class FastIsochroneParameters {

    public static final int FASTISO_MAXTHREADCOUNT = 1;
        //>> Partitioning
    public static final int PART__MIN_SPLITTING_ITERATION = 0;
    public static final int PART__MAX_SPLITTING_ITERATION = 67108864; //==2^26
    public static final int PART__MAX_CELL_NODES_NUMBER = 5000;
    public static final int PART__MIN_CELL_NODES_NUMBER = 10;
    public static final boolean PART__SEPARATECONNECTED = true;

    //>> Inertial Flow
    public static final byte INFL__GRAPH_EDGE_CAPACITY = 1;
    public static final byte INFL__LOW_GRAPH_EDGE_CAPACITY = 1;
    public static final int INFL__DUMMY_EDGE_CAPACITY = Integer.MAX_VALUE;
    public static final double FLOW__SET_SPLIT_VALUE = 0.2525;


    /**
     * Default tolerance.
     */
    public static final double CONCAVEHULL_THRESHOLD = 0.010;

}