/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.evaluation.support;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import java.awt.*;
import java.util.Arrays;

/**
 * @author silvio
 */
public class GenericEvaluation {

    static boolean rawOutput = false;
    static boolean debug = false;
    static boolean SIMPLE = false;
    static boolean ERROR = false;
    static double normalClass;

    public static GenericResultado[] runMultipleClassifier(Instances train, Instances test) throws Exception {
        GenericResultado[] results = new GenericResultado[GeneralParameters.CLASSIFIERS_FOREACH.length];
        int run = 0;
        for (ClassifierExtended classififer : GeneralParameters.CLASSIFIERS_FOREACH) {
            GenericResultado r = testaEssaGalera(classififer, train, test);
            results[run++] = r;
            if (GeneralParameters.CSV) {
                System.out.println(
                        classififer.getClassifierName() + ";"
                                + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
                                + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
                                + String.valueOf(r.getRecall()).replace(",", ".") + ";"
                                + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
                                + r.getVP() + ";"
                                + r.getVN() + ";"
                                + r.getFP() + ";"
                                + r.getFN() + ";"
                                + r.getTime()
                );
            } else if (SIMPLE) {
                System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getAcuracia()).substring(0, 5) + "%");
            } else if (ERROR) {
                //WTF?
            }
        }
        return results;
    }

    public static GenericResultado[] TEMP(Instances train, Instances test) throws Exception {
        GenericResultado[] results = new GenericResultado[GeneralParameters.CLASSIFIERS_FOREACH.length];
        int run = 0;
        for (ClassifierExtended classififer : GeneralParameters.CLASSIFIERS_FOREACH) {
            GenericResultado r = testaEssaGalera(classififer, train, test);
            results[run++] = r;
            if (GeneralParameters.CSV) {
                System.out.println(
                        classififer.getClassifierName() + ";"
                                + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
                                + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
                                + String.valueOf(r.getRecall()).replace(",", ".") + ";"
                                + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
                                + r.getVP() + ";"
                                + r.getVN() + ";"
                                + r.getFP() + ";"
                                + r.getFN() + ";"
                                + r.getTime()
                );
            } else if (SIMPLE) {
                System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getAcuracia()).substring(0, 5) + "%");
            } else if (ERROR) {
                //WTF?
            }
        }
        return results;
    }

    public static GenericResultado runSingleClassifierWithDeadline(Instances train, Instances test, long deadline) throws Exception {
        ClassifierExtended classififer = GeneralParameters.SINGLE_CLASSIFIER_MODE;
        GenericResultado r = testaEssaGaleraWithDeadline(classififer, train, test, false, deadline);

        if (GeneralParameters.CSV) {
            System.out.println(
                    r.Cx + ";"
                            + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
                            + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
                            + String.valueOf(r.getRecall()).replace(",", ".") + ";"
                            + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
                            + r.getVP() + ";"
                            + r.getVN() + ";"
                            + r.getFP() + ";"
                            + r.getFN() + ";"
                            + r.getTime()
            );
        } else if (SIMPLE) {
            System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getAcuracia()).substring(0, 5) + "%");
        }

        return r;
    }

    public static GenericResultado runSingleClassifierBinaryMatrix(Instances trainBinary, Instances testBinary, Instances trainMulticlass, Instances testMulticlass) throws Exception {
        ClassifierExtended classififer = GeneralParameters.SINGLE_CLASSIFIER_MODE;
        GenericResultado r = testaEssaGaleraBinaryMatrix(classififer, trainBinary, testBinary, trainMulticlass, testMulticlass, false);

        if (GeneralParameters.CSV) {
            System.out.println(
                    r.Cx + ";"
                            + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
                            + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
                            + String.valueOf(r.getRecall()).replace(",", ".") + ";"
                            + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
                            + r.getVP() + ";"
                            + r.getVN() + ";"
                            + r.getFP() + ";"
                            + r.getFN() + ";"
                            + r.getTime()
            );
        } else if (SIMPLE) {
            System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getF1Score()).substring(0, 5) + "%");
        }
        return r;
    }

    public static GenericResultado runSingleClassifierJ48(Instances train, Instances test) throws Exception {
        GeneralParameters.SINGLE_CLASSIFIER_MODE = GenericClassifiers.J48;
        ClassifierExtended classififer = GeneralParameters.SINGLE_CLASSIFIER_MODE;
        GenericResultado r = testaEssaGaleraJ48(classififer, train, test);

        if (GeneralParameters.CSV) {
            System.out.println(
                    r.Cx + ";"
                            + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
                            + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
                            + String.valueOf(r.getRecall()).replace(",", ".") + ";"
                            + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
                            + r.getVP() + ";"
                            + r.getVN() + ";"
                            + r.getFP() + ";"
                            + r.getFN() + ";"
                            + r.getNanotime() + ";"
                            + Arrays.toString(r.usedFS)
            );

        } else if (SIMPLE) {
            System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getF1Score()).substring(0, 5) + "%");
        }

        return r;
    }

    public static GenericResultado runSingleClassifier(Instances train, Instances test) throws Exception {
        ClassifierExtended classififer = GeneralParameters.SINGLE_CLASSIFIER_MODE;
        GenericResultado r = testaEssaGalera(classififer, train, test);

        if (GeneralParameters.CSV) {
            System.out.println(
                    r.Cx + ";"
                            + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
                            + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
                            + String.valueOf(r.getRecall()).replace(",", ".") + ";"
                            + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
                            + r.getVP() + ";"
                            + r.getVN() + ";"
                            + r.getFP() + ";"
                            + r.getFN() + ";"
                            + r.getNanotime() + ";"
                            + Arrays.toString(r.usedFS)
            );

        } else if (SIMPLE) {
            System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getF1Score()).substring(0, 5) + "%");
        }

        return r;
    }

//    public static GenericResultado runJ48(Instances train, Instances test) throws Exception {
//        ClassifierExtended classififer = GenericClassifiers.J48;
//        GenericResultado r = testaEssaGaleraJ48(classififer, train, test);
//
//        if (GeneralParameters.CSV) {
//            System.out.println(
//                    r.Cx + ";"
//                            + String.valueOf(r.getAcuracia()).replace(",", ".") + ";"
//                            + String.valueOf(r.getPrecision()).replace(",", ".") + ";"
//                            + String.valueOf(r.getRecall()).replace(",", ".") + ";"
//                            + String.valueOf(r.getF1Score()).replace(",", ".") + ";"
//                            + r.getVP() + ";"
//                            + r.getVN() + ";"
//                            + r.getFP() + ";"
//                            + r.getFN() + ";"
//                            + r.getNanotime() + ";"
//                            + Arrays.toString(r.usedFS)
//            );
//
//        } else if (SIMPLE) {
//            System.out.println("Classificador: " + classififer.getClassifierName() + " -> " + String.valueOf(r.getF1Score()).substring(0, 5) + "%");
//        }
//
//        return r;
//    }


    private static GenericResultado testaEssaGaleraBinaryMatrix(ClassifierExtended selectedClassifier, Instances trainBinary, Instances testBinary, Instances trainMulticlass, Instances testMulticlass, boolean timeTest) throws Exception {
        selectedClassifier.getClassifier().buildClassifier(trainBinary);
        if (timeTest) {
            System.out.println("---------");
            System.out.println(selectedClassifier.getClassifierName());
        }

        // Resultados
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;

        long begin = System.nanoTime();
        int[][] confusionMatrix = new int[GeneralParameters.NUM_CLASSES][GeneralParameters.NUM_CLASSES];

        for (int i = 0; i < testBinary.size(); i++) {
            try {
                Instance testando = testBinary.instance(i);
                Instance testandoMulticlass = testMulticlass.instance(i);

                double resultado = selectedClassifier.getClassifier().classifyInstance(testando);

                if (resultado == normalClass) {
                    if (resultado == testando.classValue()) {
                        VN = VN + 1; // resultado normal, resultado verdadeiro
                    } else {
                        FN = FN + 1; // resultado normal, resultado falso
                    }
                } else { //Intrusion detected!
                    if (normalClass == testando.classValue()) {
                        FP = FP + 1; // resultado ataque, resultado falso
                    } else {
                        VP = VP + 1; // resultado ataque, resultado verdadeiro
                    }
                }
                ///
                double esperado = testandoMulticlass.classValue();

                // Confusion matrix:
                confusionMatrix[(int) esperado][(int) resultado] = confusionMatrix[(int) esperado][(int) resultado] + 1;
            } catch (Exception e) {
                System.out.println("Erro: " + e.getLocalizedMessage());
            }
        }
        long end = System.nanoTime();

        if (ERROR) {
            System.out.println("Results");
        }
        long time = (end - begin) / testBinary.size(); //nano time

        GenericResultado r = new GenericResultado(selectedClassifier.getClassifierName(),
                VP, FN, VN, FP, time, confusionMatrix);
        //System.out.println(r.getCx()+" - "+r.getAcuracia()+" (F1: "+r.getF1Score());
        return r;

    }

    public static void showTree(J48 j48) throws Exception {
        // display classifier
        final javax.swing.JFrame jf =
                new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
        jf.setSize(500, 400);
        jf.getContentPane().setLayout(new BorderLayout());
        TreeVisualizer tv = new TreeVisualizer(null,
                j48.graph(),
                new PlaceNode2());
        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });

        jf.setVisible(true);
        tv.fitToScreen();
    }

    private static GenericResultado testaEssaGaleraJ48(ClassifierExtended selectedClassifier, Instances train, Instances test) throws Exception {
        long beginTraining = System.nanoTime();
        selectedClassifier.getClassifier().buildClassifier(train);
        if (selectedClassifier.getClassifier() instanceof J48) {
            // Shows the decision trees
            try {
                showTree((J48) selectedClassifier.getClassifier());
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        long endTraining = System.nanoTime();
        if (GeneralParameters.PRINT_TRAINING_TIME) {
            System.out.println("Tempo de treinamento = " + (endTraining - beginTraining));
        }


        // Resultados
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;
        long beginNano = System.nanoTime();
        long beginMili = System.currentTimeMillis();

        int[][] confusionMatrix = new int[GeneralParameters.NUM_CLASSES][GeneralParameters.NUM_CLASSES];

        long testSize;
        if (GeneralParameters.PRINT_TESTING_TIME) {
            testSize = 10000;
        } else {
            testSize = test.size();
        }

        for (int i = 0; i < testSize; i++) {
            try {
                Instance testando = test.instance(i);
                double resultado = selectedClassifier.getClassifier().classifyInstance(testando);
                if (resultado == normalClass) {
                    if (resultado == testando.classValue()) {
                        VN = VN + 1; // resultado normal, resultado verdadeiro
                    } else {
                        FN = FN + 1; // resultado normal, resultado falso
                    }
                } else { //Intrusion detected!
                    if (normalClass == testando.classValue()) {
                        FP = FP + 1; // resultado ataque, resultado falso 
                    } else {
                        VP = VP + 1; // resultado ataque, resultado verdadeiro
                    }
                }
                ///
                double esperado = testando.classValue();
                // Confusion matrix:
//                System.out.println("Esperado: "+esperado+" / resultado: "+resultado);
                confusionMatrix[(int) esperado][(int) resultado] = confusionMatrix[(int) esperado][(int) resultado] + 1;
            } catch (ArrayIndexOutOfBoundsException a) {
                System.err.println("Erro: " + a.getLocalizedMessage());
                System.err.println("DICA: " + "Tem certeza que o número de classes está definido corretamente?");

                System.exit(1);
            } catch (Exception e) {
                System.err.println("Erro: " + e.getLocalizedMessage());
                System.exit(1);

            }
        }
        long endano = System.nanoTime();
        long endMili = System.currentTimeMillis();

        if (ERROR) {
            System.out.println("Results");
        }
        float timeNano = (Float.valueOf(endano - beginNano)) / testSize; //nano time
        float timeMili = (Float.valueOf(endMili - beginMili)) / testSize; //nano time

        GenericResultado r = new GenericResultado(selectedClassifier.getClassifierName(), VP, FN, VN, FP, timeNano, confusionMatrix);
        if (GeneralParameters.PRINT_TESTING_TIME) {
            System.out.println(selectedClassifier.getClassifierName() + ";" + timeNano + ";" + timeMili);
        }
        return r;

    }

    public static GenericResultado testaEssaGalera(ClassifierExtended selectedClassifier, Instances train, Instances test) throws Exception {
        long beginTraining = System.nanoTime();
        selectedClassifier.getClassifier().buildClassifier(train);
//        if (selectedClassifier.getClassifier() instanceof J48) {
//            // Shows the decision trees
//            Consistency2021.showTree((J48) selectedClassifier.getClassifier());
//        }
        long endTraining = System.nanoTime();
        if (GeneralParameters.PRINT_TRAINING_TIME) {
            System.out.println("Tempo de treinamento = " + (endTraining - beginTraining));
        }


        // Resultados
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;
        long beginNano = System.nanoTime();
        long beginMili = System.currentTimeMillis();

        int[][] confusionMatrix = new int[GeneralParameters.NUM_CLASSES][GeneralParameters.NUM_CLASSES];

        long testSize;
        if (GeneralParameters.PRINT_TESTING_TIME) {
            testSize = 10000;
        } else {
            testSize = test.size();
        }

        for (int i = 0; i < testSize; i++) {
            try {
                Instance testando = test.instance(i);
                double resultado = selectedClassifier.getClassifier().classifyInstance(testando);
                double esperado = testando.classValue();
                if (resultado == esperado) { // good prediction
                    if (resultado == normalClass) {
                        VN = VN + 1;
                    } else {
                        VP = VP + 1;
                    }
                } else { // bad prediction
                    if (resultado == normalClass) {
                        FN = FN + 1;
                    } else {
                        FP = FP + 1;
                    }
                }

                // Confusion matrix:
//                System.out.println("Esperado: "+esperado+" / resultado: "+resultado);
                confusionMatrix[(int) esperado][(int) resultado] = confusionMatrix[(int) esperado][(int) resultado] + 1;
            } catch (ArrayIndexOutOfBoundsException a) {
                System.err.println("Erro: " + a.getLocalizedMessage());
                System.err.println("DICA: " + "Tem certeza que o número de classes está definido corretamente?");

                System.exit(1);
            } catch (Exception e) {
                System.err.println("Erro: " + e.getLocalizedMessage());
                System.exit(1);

            }
        }
        long endano = System.nanoTime();
        long endMili = System.currentTimeMillis();

        if (ERROR) {
            System.out.println("Results");
        }
        float timeNano = (Float.valueOf(endano - beginNano)) / testSize; //nano time
        float timeMili = (Float.valueOf(endMili - beginMili)) / testSize; //nano time

        GenericResultado r = new GenericResultado(selectedClassifier.getClassifierName(), VP, FN, VN, FP, timeNano, confusionMatrix);
        if (GeneralParameters.PRINT_TESTING_TIME) {
            System.out.println(selectedClassifier.getClassifierName() + ";" + timeNano + ";" + timeMili);
        }
        return r;

    }

    private static GenericResultado testaEssaGaleraWithDeadline(ClassifierExtended selectedClassifier, Instances train, Instances test, boolean timeTest, long deadline) throws Exception {
        selectedClassifier.getClassifier().buildClassifier(train);
        if (timeTest) {
            System.out.println("---------");
            System.out.println(selectedClassifier.getClassifierName());
        }

        // Resultados
        double acuracia = 0;
        double txDec = 0;
        double txAFal = 0;
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;
        long time = System.nanoTime();
        long cumulativo = 0;
        int vectorPosErrors[] = new int[1000];

        for (int i = 0; i < test.size(); i++) {

            try {
                Instance testando = test.instance(i);
                long antes = System.nanoTime();
                double res1 = selectedClassifier.getClassifier().classifyInstance(testando);
                long depois = (System.nanoTime() - antes);
                if (timeTest) {
                    System.out.println(depois);
                }

                cumulativo = cumulativo + depois;
                if (cumulativo >= deadline) {
                    System.out.println(testando.numAttributes() + "," + i);
                    break;
                }
                if (res1 == testando.classValue()) {
                    if (res1 == normalClass) {
                        VN = VN + 1;
                    } else {
                        VP = VP + 1;
                    }
                } else {
                    //  String str = testando.toStringMaxDecimalDigits(10).substring(0, 3).replace("0,", "0").replace("1,", "0.1");
                    // float pos = Float.valueOf(str) * 10;
                    // int posi = (int) pos;

                    if (res1 == normalClass) {
                        FN = FN + 1;
                    } else {
                        FP = FP + 1;
                        // vectorPosErrors[posi] = vectorPosErrors[posi] + 1;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getLocalizedMessage());
            }
        }
        if (ERROR) {
            System.out.println("Results");
            int ll = 0;
        }

        try {
            acuracia = Float.valueOf(((VP + VN)) * 100) / Float.valueOf((VP + VN + FP + FN));
            txDec = Float.valueOf((VP * 100)) / Float.valueOf((VP + FN));  // Sensitividade ou Taxa de Detecção
            txAFal = Float.valueOf((FP * 100)) / Float.valueOf((VN + FP)); // Especificade ou Taxa de Alarmes Falsos
        } catch (ArithmeticException e) {
            System.out.println("Divisão por zero ((" + VP + " + " + VN + ") * 100) / (" + VP + " + " + VN + "+ " + FP + "+" + FN + "))");
        }

//        Resultado r = new Resultado(descricao, VP, FN, VN, FP, acuracia, txDec, txAFal, cumulativo / (VP + VN + FP + FN));
        GenericResultado r = new GenericResultado(selectedClassifier.getClassifierName(), VP, FN, VN, FP, cumulativo);
        return r;

    }

}
