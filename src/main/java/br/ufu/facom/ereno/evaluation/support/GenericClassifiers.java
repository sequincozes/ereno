/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.evaluation.support;

import br.ufu.facom.ereno.evaluation.support.ClassifierExtended;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

/**
 *
 * @author silvio
 */
public class GenericClassifiers {

    public static final ClassifierExtended RANDOM_TREE = new ClassifierExtended(true, new RandomTree(), "RandomTree");
    public static final ClassifierExtended RANDOM_FOREST = new ClassifierExtended(true, new RandomForest(), "RandomForest");
    public static final ClassifierExtended NAIVE_BAYES = new ClassifierExtended(true, new NaiveBayes(), "NaiveBayes");
    public static final ClassifierExtended REP_TREE = new ClassifierExtended(true, new REPTree(), "REPTree");
    public static final ClassifierExtended J48 = new ClassifierExtended(true, new J48(), "J48");
    public static final ClassifierExtended KNN = new ClassifierExtended(true, new IBk(), "KNN");
//    public static final ClassifierExtended[] all = {RANDOM_TREE, J48};
    public static ClassifierExtended[] allCustom = {RANDOM_TREE, J48, REP_TREE, NAIVE_BAYES, RANDOM_FOREST};

        public static final ClassifierExtended[] all = {RANDOM_TREE, J48, REP_TREE, NAIVE_BAYES, RANDOM_FOREST};
//    public static final ClassifierExtended[] all = {J48, NAIVE_BAYES, RANDOM_FOREST, RANDOM_TREE, REP_TREE};
    //public static final ClassifierExtended[] all = {NAIVE_BAYES, NAIVE_BAYES, NAIVE_BAYES, NAIVE_BAYES, NAIVE_BAYES};
}
