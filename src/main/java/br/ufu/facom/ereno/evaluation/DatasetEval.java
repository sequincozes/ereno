package br.ufu.facom.ereno.evaluation;

import br.ufu.facom.ereno.evaluation.support.*;
import weka.core.Instances;

public class DatasetEval {
    public static void main(String[] args) throws Exception {
        runWithoutCV();
    }


    public static void runWithoutCV() throws Exception {
        GeneralParameters.DATASET = "C:\\Users\\zomca\\IdeaProjects\\ereno-uc09\\datasets\\oriented_grayhole\\8000_5_5.arff";
        Instances train = Util.loadSingleFile(false);
        train.setClassIndex(train.numAttributes() - 1);

        GeneralParameters.DATASET = "C:\\Users\\zomca\\IdeaProjects\\ereno-uc09\\datasets\\oriented_grayhole\\2000_5_5.arff";
        Instances test = Util.loadSingleFile(false);
        test.setClassIndex(test.numAttributes() - 1);

        GeneralParameters.SINGLE_CLASSIFIER_MODE = GenericClassifiers.RANDOM_FOREST;
        GenericResultado resultados = GenericEvaluation.runSingleClassifierJ48(train, test);
        resultados.printResults();

        int[][] confusionMatrix = resultados.getConfusionMatrix();

        System.out.println("Confusion matrix:");
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix.length; j++) {
                System.out.print(" " + confusionMatrix[i][j]);
            }
            System.out.println(" ");
        }
//        GenericEvaluation.runSingleClassifier(train, train);
//        GenericEvaluation.runSingleClassifierJ48(train, train).printResults();
//        GeneralParameters.SINGLE_CLASSIFIER_MODE = GenericClassifiers.J48;
//    GenericEvaluation.runSingleClassifier(train, test);
    }
}
