/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.midiacom.ereno.outputManager.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 *
 * @author silvio
 */
@IgnoreExtraProperties
public class Iteration {

    public String accuracy;
    public String subset;
    public int iterationNumber;
    public int noImprovments;
    public String currentTime;
    public ArrayList<Detail> details;
    public int numberEvaluation;

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getSubset() {
        return subset;
    }

    public void setSubset(String subset) {
        this.subset = subset;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public int getNoImprovments() {
        return noImprovments;
    }

    public void setNoImprovments(int noImprovments) {
        this.noImprovments = noImprovments;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public ArrayList<Detail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Detail> details) {
        this.details = details;
    }

    public int getNumberEvaluation() {
        return numberEvaluation;
    }

    public void setNumberEvaluation(int numberEvaluation) {
        this.numberEvaluation = numberEvaluation;
    }

    public Iteration() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Iteration(String accuracy, String subset, int iteration, int noImprovments, int numberEvaluation, String currentTime) {
        this.accuracy = accuracy;
        this.subset = subset;
        this.iterationNumber = iteration;
        this.currentTime = currentTime;
        this.noImprovments = noImprovments;
        this.numberEvaluation = numberEvaluation;
    }

    public void print() {
        System.out.println("#" + iterationNumber + ": " + subset + " = " + accuracy);
    }

}
