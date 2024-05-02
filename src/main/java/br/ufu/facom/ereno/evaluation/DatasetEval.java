package br.ufu.facom.ereno.evaluation;

import br.ufu.facom.ereno.evaluation.support.*;
import weka.core.Instances;

public class DatasetEval {
    public static void main(String[] args) throws Exception {
        runWithoutCV();
    }


    public static void runWithoutCV() throws Exception {
        GeneralParameters.DATASET = "E:\\ereno dataset\\ereninho\\multiclass_train.arff";
        Instances train = Util.loadSingleFile(false);
        train.setClassIndex(train.numAttributes() - 1);

        GeneralParameters.DATASET = "E:\\ereno dataset\\ereninho\\multiclass_test.arff";
        Instances test = Util.loadSingleFile(false);
        test.setClassIndex(test.numAttributes() - 1);

        GeneralParameters.SINGLE_CLASSIFIER_MODE = GenericClassifiers.RANDOM_FOREST;
        GenericResultado resultados = GenericEvaluation.testaEssaGalera(GenericClassifiers.RANDOM_FOREST, train, test);
        resultados.printResults();

        int[][] confusionMatrix = resultados.getConfusionMatrix();

        System.out.println("Confusion matrix:");
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix.length; j++) {
                System.out.print(" " + confusionMatrix[i][j]);
            }
            System.out.println(" ");
        }
        GenericEvaluation.runSingleClassifier(train, train);
//        GenericEvaluation.runSingleClassifierJ48(train, train).printResults();
//        GeneralParameters.SINGLE_CLASSIFIER_MODE = GenericClassifiers.J48;
//    GenericEvaluation.runSingleClassifier(train, test);
    }
}
