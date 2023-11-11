/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.evaluation.support;

import weka.classifiers.Classifier;

/**
 *
 * @author Silvio
 */
public class ClassifierExtended {

    private boolean includeOnTests;
    private Classifier classifier;
    private String ClassifierName;

    public ClassifierExtended(Classifier classifier, String ClassifierName) {
        this.includeOnTests = true;
        this.classifier = classifier;
        this.ClassifierName = ClassifierName;
    }

    public ClassifierExtended(boolean includeOnTests, Classifier classifier, String ClassifierName) {
        this.includeOnTests = includeOnTests;
        this.classifier = classifier;
        this.ClassifierName = ClassifierName;
    }

    public boolean isIncludeOnTests() {
        return includeOnTests;
    }

    public void setIncludeOnTests(boolean includeOnTests) {
        this.includeOnTests = includeOnTests;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public String getClassifierName() {
        return ClassifierName;
    }

    public void setClassifierName(String ClassifierName) {
        this.ClassifierName = ClassifierName;
    }

    public double getTempDecision() {
        return tempDecision;
    }

    public void setTempDecision(double tempDecision) {
        this.tempDecision = tempDecision;
    }
    private double tempDecision;
}
