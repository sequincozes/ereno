/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.evaluation.support;

import weka.core.Instances;

public class GeneralParameters {

    public static final boolean NUMERIC_CLEANNER = true;
    public static boolean PRINT_CONFUSION_MATRIX = true;

    /* Modifications for GRASP-FS without crossvalidation*/
    public static Instances TRAIN;
    public static Instances TEST;
    public static boolean CROSS_VALIDATION = true;

    public static boolean PRINT_TRAINING_TIME = false;
    public static boolean PRINT_TESTING_TIME = false; // limit the analysis to the first 10k register in dataset

    public static final String SWAT30pct = "/home/silvio/datasets/SWAT/swat30pct.csv";
    public static boolean SINGLE_FOLD_MODE = true;
    public static boolean PRINT_SELECTION = true;
    public static boolean DEBUG_MODE = false;

    public static boolean CSV = true;
    public static String WSN_DATASET = "all_in_one_wsn.csv";
    public static String WSN_DATASET_MINI = "all_mini_one_wsn.csv";
    public static String TESTE_DATASET = "all_in_one_wsn.csv";
    public static String CICIDS_DATASET = "/home/silvio/datasets/CICIDS2017/all_in_one/cicids.csv";
    //    public static String CICIDS_DATASET = "all_in_one_cicids.csv";
    public static String KDD_DATASET = "all_in_one_kdd.csv";
    public static String classes1_13 = "/home/silvio/datasets/classes1-13.csv";
    public static String CONSISTENCYV4_DATASET_UC01 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc01/uc00_uc01.csv";
    public static String CONSISTENCYV4_DATASET_UC02 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc02/uc00_uc02.csv";
    public static String CONSISTENCYV4_DATASET_UC03 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc03/uc00_uc03.csv";
    public static String CONSISTENCYV4_DATASET_UC04 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc04/uc00_uc04.csv";
    public static String CONSISTENCYV4_DATASET_UC05 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc05/uc00_uc05.csv";
    public static String CONSISTENCYV4_DATASET_UC06 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc06/uc00_uc06.csv";
    public static String CONSISTENCYV4_DATASET_UC07 = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc07/uc00_uc07.csv";
    public static String CONSISTENCYV4_DATASET_UC01_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc01/uc00_uc01_10percent.csv";
    public static String CONSISTENCYV4_DATASET_UC02_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc02/uc00_uc02_10percent.csv";
    public static String CONSISTENCYV4_DATASET_UC03_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc03/uc00_uc03_10percent.csv";
    public static String CONSISTENCYV4_DATASET_UC04_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc04/uc00_uc04_10percent.csv";
    public static String CONSISTENCYV4_DATASET_UC05_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc05/uc00_uc05_10percent.csv";
    public static String CONSISTENCYV4_DATASET_UC06_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc06/uc00_uc06_10percent.csv";
    public static String CONSISTENCYV4_DATASET_UC07_10percent = "/home/silvio/datasets/Full_SV_2021/consistency_v4/uc07/uc00_uc07_10percent.csv";
    public static String CONSISTENCYV4_DATASET[] = {"", CONSISTENCYV4_DATASET_UC01, CONSISTENCYV4_DATASET_UC02, CONSISTENCYV4_DATASET_UC03, CONSISTENCYV4_DATASET_UC04, CONSISTENCYV4_DATASET_UC05, CONSISTENCYV4_DATASET_UC06, CONSISTENCYV4_DATASET_UC07};
    public static String CONSISTENCYV4_DATASET_10percent[] = {
            "",
            CONSISTENCYV4_DATASET_UC01_10percent,
            CONSISTENCYV4_DATASET_UC02_10percent,
            CONSISTENCYV4_DATASET_UC03_10percent,
            CONSISTENCYV4_DATASET_UC04_10percent,
            CONSISTENCYV4_DATASET_UC05_10percent,
            CONSISTENCYV4_DATASET_UC06_10percent,
            CONSISTENCYV4_DATASET_UC07_10percent};
    public static String CONSISTENCYV5_DATASET_UC01 = "uc01/uc00_uc01.csv";
    public static String CONSISTENCYV5_DATASET_UC02 = "uc02/uc00_uc02.csv";
    public static String CONSISTENCYV5_DATASET_UC03 = "uc03/uc00_uc03.csv";
    public static String CONSISTENCYV5_DATASET_UC04 = "uc04/uc00_uc04.csv";
    public static String CONSISTENCYV5_DATASET_UC05 = "uc05/uc00_uc05.csv";
    public static String CONSISTENCYV5_DATASET_UC06 = "uc06/uc00_uc06.csv";
    public static String CONSISTENCYV5_DATASET_UC07 = "uc07/uc00_uc07.csv";
    public static String CONSISTENCYV5_DATASET[] = {
            "", CONSISTENCYV5_DATASET_UC01, CONSISTENCYV5_DATASET_UC02, CONSISTENCYV5_DATASET_UC03, CONSISTENCYV5_DATASET_UC04, CONSISTENCYV5_DATASET_UC05, CONSISTENCYV5_DATASET_UC06, CONSISTENCYV5_DATASET_UC07
    };

    public static String CONSISTENCY_DATASET = "/home/silvio/datasets/Full_SV_2020/consistency_v3/full_goose_sv_consitency.csv";
    public static String CONSISTENCY_DATASET_10percent = "/home/silvio/datasets/Full_SV_2020/consistency_v3/full_goose_sv_consitency_10percent.csv";
    public static String CONSISTENCY_DATASET_1percent = "/home/silvio/datasets/Full_SV_2020/consistency_v3/full_goose_sv_consitency_1percent.csv";
    public static String CONSISTENCY_DATASET_1percent_binary = "/home/silvio/datasets/Full_SV_2020/consistency_v3/full_goose_sv_consitency_1percent_binary.csv";

    public static String DATASET = null;

    public static String WSN_5CLASS = "/home/silvio/datasets/wsn-ds/WSN-DS-5class.csv";
    public static String WSN_2CLASS = "/home/silvio/datasets/wsn-ds/WSN-DS-2class.csv";
    public static String WSN_ATTACKS = "/home/silvio/datasets/wsn-ds/separated/";

    public static String WSN_NORMAL_FLOODING = "/home/silvio/datasets/wsn-ds/separated/normal_flooding.csv";
    public static String WSN_NORMAL_GRAYHOLE = "/home/silvio/datasets/wsn-ds/separated/normal_grayhole.csv";
    public static String WSN_NORMAL_BLACKHOLE = "/home/silvio/datasets/wsn-ds/separated/normal_blackhole.csv";

    public static String WSN_CLUSTERING_FLOODING_TEST = "/home/silvio/datasets/wsn-ds/clustering2021/normal_flooding_20percent_test.csv";
    public static String WSN_CLUSTERING_GRAYHOLE_TEST = "/home/silvio/datasets/wsn-ds/clustering2021/normal_grayhole_20percent_test.csv";
    public static String WSN_CLUSTERING_BLACKHOLE_TEST = "/home/silvio/datasets/wsn-ds/clustering2021/normal_blackhole_20percent_test.csv";
    public static String WSN_CLUSTERING_FLOODING_TRAIN = "/home/silvio/datasets/wsn-ds/clustering2021/normal_flooding_80percent_train.csv";
    public static String WSN_CLUSTERING_GRAYHOLE_TRAIN = "/home/silvio/datasets/wsn-ds/clustering2021/normal_grayhole_80percent_train.csv";
    public static String WSN_CLUSTERING_BLACKHOLE_TRAIN = "/home/silvio/datasets/wsn-ds/clustering2021/normal_blackhole_80percent_train.csv";
    // GRASP
    public static String OUTPUT = "outputs/";
    public static int FOLDS = 5;
    public static int GRASP_SEED = 5;
    public static int EVALUATION_SEED = 7;

    // Run Settings java 
//  java -jar preGrasp.jar all_in_one_WSN.csv 4 >> pregrasp_WSN_RANDOM_FOREST.txt
    public static String[] DATASETS_FOREACH = {
            CICIDS_DATASET,
            KDD_DATASET,
            WSN_DATASET
    };


    public static ClassifierExtended[] CLASSIFIERS_FOREACH = {
            GenericClassifiers.RANDOM_TREE,
            GenericClassifiers.J48,
            GenericClassifiers.REP_TREE,
            GenericClassifiers.NAIVE_BAYES,
            GenericClassifiers.RANDOM_FOREST
    };

    public static ClassifierExtended SINGLE_CLASSIFIER_MODE = GenericClassifiers.NAIVE_BAYES;

    public static final boolean VERBOSO = true;

    public static boolean NORMALIZE = false;

    public static int[] FEATURE_SELECTION = {};// FeatureSubsets.RCL_CICIDS_IG;
    static int NUM_CLASSES = 11;

    public static int getTotalFeatures() {
        return FEATURE_SELECTION.length;
    }

}
