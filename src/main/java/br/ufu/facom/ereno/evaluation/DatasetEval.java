package br.ufu.facom.ereno.evaluation;

import br.ufu.facom.ereno.evaluation.support.*;
import weka.core.Instances;

public class DatasetEval {
    public static void main(String[] args) throws Exception {
        runWithoutCV();
    }


    public static void runWithoutCV() throws Exception {
//        GeneralParameters.DATASET = "E:\\ereno dataset\\hibrid_dataset_GOOSE_train_binary.arff";
//        GeneralParameters.DATASET = "E:\\ereno dataset\\hibrid_dataset_GOOSE_train.arff";
//        GeneralParameters.DATASET = "E:\\ereno dataset\\hibrid_dataset_GOOSE_train.arff";
        GeneralParameters.DATASET = "E:\\datasets\\vagner\\ereninho\\train_dataset_mini.csv";


//        GeneralParameters.DATASET = "C:\\Users\\Silvio\\Downloads\\Dataset2023Treino.arff";
//        GeneralParameters.DATASET = "E:\\datasets\\vagner\\train.arff";
        Instances train = Util.loadSingleFile(false);
        train.setClassIndex(train.numAttributes() - 1);

//        System.out.println("Agora vem o segundo");
//        GeneralParameters.DATASET = "E:\\ereno dataset\\hibrid_dataset_GOOSE_test_binary.arff";
//        GeneralParameters.DATASET = "C:\\Users\\Silvio\\Downloads\\Dataset2023.arff";
//        GeneralParameters.DATASET = "E:\\datasets\\vagner\\test.arff";
        GeneralParameters.DATASET = "E:\\datasets\\vagner\\ereninho\\test_dataset_mini.csv";

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
//        GenericEvaluation.runSingleClassifier(train, train);
//        GenericEvaluation.runSingleClassifierJ48(train, train);
//        GeneralParameters.SINGLE_CLASSIFIER_MODE = GenericClassifiers.J48;
//    GenericEvaluation.runSingleClassifier(train, test);
    }
}
