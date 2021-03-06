/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.midiacom.ereno.evaluation;

import br.uff.midiacom.ereno.outputManager.*;
import br.uff.midiacom.ereno.abstractclassification.GeneralParameters;
import br.uff.midiacom.ereno.outputManager.model.Pid;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.NonNull;
import br.uff.midiacom.ereno.outputManager.model.Detail;
import br.uff.midiacom.ereno.outputManager.model.Iteration;
import br.uff.midiacom.ereno.outputManager.model.Error;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author silvio
 */
public class FirebaseOutputProcessor {

    private DatabaseReference mDatabase;
    private String[] graspMethods = {"grasp", "grasp_vnd", "grasp_rvnd"};
    private String graspMethod;// = graspMethods[0];

    private String[] datasets = {"wsn", "kdd", "cicids"};
    private String dataset;// = datasets[0];

    private String[] classifiers = {"J48", "NaiveBayes", "RandomForest", "RandomTree", "REPTree"};
    private String classifier;// = classifiers[0];

    private String[] hosts = {"cluster01", "cluster04", "cluster05"};
    private String host;// = hosts[0];

    String pid;
    private boolean alreadyPrintedHeader = false;
    int currentIterationNumber = 1;
    boolean offlinemode = false;
    boolean debug = false;
    boolean printReference = false;

    public static void main(String[] args) {
        FirebaseOutputProcessor fb = new FirebaseOutputProcessor();
//        fb.initialize(fb.graspMethod, fb.dataset);
//        fb.discoverPIDs(fb.classifier, fb.host);
        fb.initialize();
//        fb.discoveryAll();
        fb.discoverPIDsComplete("wsn", "grasp");
        while (true) {
        }
    }

    public void discoveryAll() {
        //kdd,wsn, cicids
        for (String currentMethod : graspMethods) {
            this.graspMethod = currentMethod;
            for (String currentDataset : datasets) {
                this.dataset = currentDataset;
                for (String currentClassifier : classifiers) {
                    this.classifier = currentClassifier;
                    for (String currentHost : hosts) {
                        this.host = currentHost;
                        System.out.println("Will discover to: " + dataset + "/" + graspMethod + "/" + classifier + "/" + host);
                        discoverPIDs(classifier, host);
                    }
                }
            }
        }

        //- grasp
        //- grasp_rvnd
        //- grasp_vnd
        //-- J48
        //-- NaiveBayes
        //-- RandomForest
        //-- RandomTree
        //-- REPTree
        //---- cluster01
        //---- cluster04
        //---- cluster05
        //------ <PID> []
        //-------- begin_time
        //-------- last_evaluation
        //-------- iterations
        //----------- 0 []
        //----------- N [N].currentTime
        //----------- N [N].noImprovments
        //i = N - noImprovments
        //[i].currentTime
    }

    public FirebaseOutputProcessor initialize() {
        this.graspMethod = graspMethod;
        if (!offlinemode) {
            FileInputStream serviceAccount = null;
            try {
                serviceAccount = new FileInputStream("ereno-9326b-firebase-adminsdk-n4abg-a77d35e070.json");
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://ereno-9326b.firebaseio.com")
                        .build();
                FirebaseApp.initializeApp(options);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                if (printReference)
                    System.out.println("mDatabase:" + mDatabase);
                serviceAccount.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FirebaseOutputProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FirebaseOutputProcessor.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        return this;

    }

    public void discoverPIDsComplete(String datasetToDiscover, String graspMethodToDiscover) {
        this.graspMethod = graspMethodToDiscover;
        this.dataset = datasetToDiscover;

        final DatabaseReference iterationsReference = mDatabase.child(datasetToDiscover).child(graspMethodToDiscover);//.child(Classifier).child(host);//.child(pid).child("iterations");//.child("1").child("details").child("1884");
        iterationsReference.addValueEventListener(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                                          if (printReference)
                                                              System.out.println("Method Ref: " + dataSnapshot.getRef());
                                                          for (DataSnapshot classifierRef : dataSnapshot.getChildren()) {
                                                              classifier = classifierRef.getKey();
                                                              if (printReference)
                                                                  System.out.println("classifierRef: " + classifierRef.getRef());
                                                              for (DataSnapshot hostRef : classifierRef.getChildren()) {
                                                                  host = hostRef.getKey();
                                                                  if (printReference)
                                                                      System.out.println("hostRef: " + hostRef.getRef());
                                                                  String pids = "Found " + hostRef.getChildrenCount() + " PIDs on " + host + ": [";
                                                                  for (DataSnapshot pidRef : hostRef.getChildren()) {
                                                                      pid = pidRef.getKey();

                                                                      if (printReference)
                                                                          System.out.println("pidRef: " + pidRef.getRef());
                                                                      pids = pids.concat(pidRef.getKey() + ",");
                                                                      if (pidRef.getChildrenCount() > 1) {
                                                                          ArrayList<Iteration> iterations = new ArrayList<>();
                                                                          for (DataSnapshot itRef : pidRef.child("iterations").getChildren()) {
                                                                              if (printReference)
                                                                                  System.out.println("itRef: " + itRef.getRef());

                                                                              try {
                                                                                  Iteration it = new Iteration();

                                                                                  try {
                                                                                      long numberEval = itRef.child("numberEvaluation").getValue(Long.class);
                                                                                      it.setNumberEvaluation((int) numberEval);
                                                                                  } catch (Exception e) {
                                                                                      it.setNumberEvaluation(-1);
                                                                                  }

                                                                                  try {
                                                                                      long noImprovments = itRef.child("noImprovments").getValue(Long.class);
                                                                                      it.setNoImprovments((int) noImprovments);
                                                                                  } catch (Exception e) {
                                                                                      it.setNoImprovments(-1);
                                                                                  }

                                                                                  try {
                                                                                      long iterationNumber = itRef.child("iterationNumber").getValue(Long.class);
                                                                                      it.setIterationNumber((int) iterationNumber);
                                                                                  } catch (Exception e) {
                                                                                      it.setIterationNumber(-1);
                                                                                  }

                                                                                  try {
                                                                                      it.setCurrentTime((String) itRef.child("currentTime").getValue(String.class));
                                                                                  } catch (Exception e) {
                                                                                      it.setCurrentTime(String.valueOf(-1));
                                                                                  }
                                                                                  try {
                                                                                      it.setAccuracy((String) itRef.child("accuracy").getValue(String.class));
                                                                                  } catch (Exception e) {
                                                                                      it.setAccuracy(String.valueOf(-1));
                                                                                  }

                                                                                  try {
                                                                                      it.setSubset((String) itRef.child("subset").getValue(String.class));
                                                                                  } catch (Exception e) {
                                                                                      it.setSubset("{}");
                                                                                  }

                                                                                  iterations.add(it);
                                                                              } catch (Exception e) {
                                                                                  e.printStackTrace();
                                                                              }
                                                                          }
                                                                          printBesIteration((ArrayList<Iteration>) iterations.clone());

                                                                      } else if (dataSnapshot.getChildrenCount() == 0) {
                                                                          System.out.println("No complete iterations. " + dataSnapshot.getRef());
                                                                          Detail detail = (Detail) dataSnapshot.child("details").getValue();
                                                                          ArrayList<Detail> details = new ArrayList<>();
                                                                          details.add(detail);
                                                                          printBestDetail(details);
                                                                      } else {
                                                                          System.out.println("One complete iteration. " + dataSnapshot.toString());
                                                                          Iteration iteratino = (Iteration) dataSnapshot.getValue();
                                                                          printBestDetail(iteratino.details);
                                                                      }
                                                                  }
                                                                  pids = pids.concat("]");
                                                                  pids = pids.replace(",]", "]");
                                                                  if (debug) System.out.println(pids);
                                                              }
                                                          }
                                                      }

                                                      @Override
                                                      public void onCancelled(@NonNull DatabaseError databaseError) {
                                                          System.out.println("Erro ao salvar detalhe: " + databaseError);
                                                      }

                                                  }
        );

    }


    public void readDetails(String Classifier, String host, String pid, String it) {
        final DatabaseReference iterationsReference = mDatabase.child(dataset).child(graspMethod).child(Classifier).child(host).child(pid).child("iterations").child(it).child("details");//.child("1884");
        if (printReference) {
            System.out.println("iterationsReferences: " + iterationsReference);
        }
        iterationsReference.addValueEventListener(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                                          System.out.println("Data Change");
                                                          if (dataSnapshot.getChildrenCount() > 1) {
                                                              ArrayList<Detail> details = new ArrayList<>();
                                                              for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                                  //System.out.println("Aq: " + messageSnapshot);
                                                                  Detail detail = messageSnapshot.getValue(Detail.class);
                                                                  details.add(detail);
                                                                  //System.out.println("It: " + interaction.accuracy);
                                                              }
                                                              printBestDetail(details);

                                                          } else if (dataSnapshot.getChildrenCount() == 0) {
                                                              System.out.println("No complete iterations. " + dataSnapshot.getRef());
//                                                              Detail detail = (Detail) dataSnapshot.child("details").getValue();
//                                                              ArrayList<Detail> details = new ArrayList<>();
//                                                              details.add(detail);
//                                                              printBestDetail(details);
                                                          } else {
                                                              System.out.println("One complete iteration. " + dataSnapshot.getRef());
//                                                              Iteration iteratino = (Iteration) dataSnapshot.getValue();
//                                                              printBestDetail(iteratino.details);
                                                          }
                                                      }

                                                      @Override
                                                      public void onCancelled(@NonNull DatabaseError databaseError) {
                                                          System.out.println("Erro ao salvar detalhe: " + databaseError);
                                                      }

                                                  }
        );

    }

    public void readBestIteration(String Classifier, String host, String pid) {
        final DatabaseReference iterationsReference = mDatabase.child(dataset).child(graspMethod).child(Classifier).child(host).child(pid).child("iterations");//.child("1").child("details").child("1884");
        if (printReference)
            System.out.println("iterationsReferences: " + iterationsReference);
        iterationsReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (debug)
                            System.out.println("Found " + dataSnapshot.getChildrenCount() + " iterations...");
                        if (dataSnapshot.getChildrenCount() > 1) {
                            ArrayList<Iteration> iterations = new ArrayList<>();
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                try {
//                                    String keyChild = messageSnapshot.getKey();
//                                    System.out.println("Key: " + keyChild);
//                                    DataSnapshot messageSnapshotChild = messageSnapshot.child("numberEvaluation");
                                    Iteration it = new Iteration();

                                    try {
                                        long numberEval = messageSnapshot.child("numberEvaluation").getValue(Long.class);
                                        it.setNumberEvaluation((int) numberEval);
                                    } catch (Exception e) {
                                        it.setNumberEvaluation(-1);
                                    }

                                    try {
                                        long noImprovments = messageSnapshot.child("noImprovments").getValue(Long.class);
                                        it.setNoImprovments((int) noImprovments);
                                    } catch (Exception e) {
                                        it.setNoImprovments(-1);
                                    }

                                    try {
                                        long iterationNumber = messageSnapshot.child("iterationNumber").getValue(Long.class);
                                        it.setIterationNumber((int) iterationNumber);
                                    } catch (Exception e) {
                                        it.setIterationNumber(-1);
                                    }

                                    try {
                                        it.setCurrentTime((String) messageSnapshot.child("currentTime").getValue(String.class));
                                    } catch (Exception e) {
                                        it.setCurrentTime(String.valueOf(-1));
                                    }
                                    try {
                                        it.setAccuracy((String) messageSnapshot.child("accuracy").getValue(String.class));
                                    } catch (Exception e) {
                                        it.setAccuracy(String.valueOf(-1));
                                    }

                                    try {
                                        it.setSubset((String) messageSnapshot.child("subset").getValue(String.class));
                                    } catch (Exception e) {
                                        it.setSubset("{}");
                                    }


//                                    System.out.println("Aq: " + messageSnapshot);
//                                                                      Iteration iteraction = messageSnapshot.getValue(Iteration.class);
//                                    it.print();
//                                    System.out.println("It: " + iteraction.accuracy);
//                                    printIteration(it);
                                    iterations.add(it);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            printBesIteration((ArrayList<Iteration>) iterations.clone());

                        } else if (dataSnapshot.getChildrenCount() == 0) {
                            System.out.println("No complete iterations. " + dataSnapshot.getRef());
                            Detail detail = (Detail) dataSnapshot.child("details").getValue();
                            ArrayList<Detail> details = new ArrayList<>();
                            details.add(detail);
                            printBestDetail(details);
                        } else {
                            System.out.println("One complete iteration. " + dataSnapshot.toString());
                            Iteration iteratino = (Iteration) dataSnapshot.getValue();
                            printBestDetail(iteratino.details);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Erro ao salvar detalhe: " + databaseError);
                    }

                }
        );

    }

    public void discoverPIDs(String Classifier, String host) {
        final DatabaseReference iterationsReference = mDatabase.child(dataset).child(graspMethod).child(Classifier).child(host);//.child(pid).child("iterations");//.child("1").child("details").child("1884");
        iterationsReference.addValueEventListener(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                                          String pids = "Found " + dataSnapshot.getChildrenCount() + " PIDs on " + host + ": [";
                                                          for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                              if (printReference)
                                                                  System.out.println("PID Ref: " + dataSnapshot.getRef());
                                                              pids = pids.concat(messageSnapshot.getKey() + ",");
                                                              readBestIteration(classifier, host, messageSnapshot.getKey());
                                                          }
                                                          pids = pids.concat("]");
                                                          pids = pids.replace(",]", "]");
                                                          if (debug) System.out.println(pids);
                                                      }

                                                      @Override
                                                      public void onCancelled(@NonNull DatabaseError databaseError) {
                                                          System.out.println("Erro ao salvar detalhe: " + databaseError);
                                                      }

                                                  }
        );

    }


    private void printBestDetail(ArrayList<Detail> details) {
        Detail child = details.get(0); //@TODO refatoar para verificar o melhor
        System.out.println(child.time + ";" + child.accuracy + ";" + child.evaluation + ";" + child.subset);
    }

    private void printIteration(Iteration iteratino) {
        System.out.println(iteratino.currentTime + ";" + iteratino.accuracy + ";" + iteratino.iterationNumber + ";" + iteratino.noImprovments + ";" + iteratino.numberEvaluation + ";" + iteratino.subset);
    }

    private void printBesIteration(ArrayList<Iteration> iterations) {
        Iteration lastIt = iterations.get(iterations.size() - 1);
        int lastImprovment = lastIt.getIterationNumber() - lastIt.noImprovments;
        Iteration improvedInt = iterations.get(lastImprovment);
//        System.out.println("Time: "+improvedInt.getCurrentTime());
        if (!alreadyPrintedHeader) {
            System.out.println("dataset,graspMethod,host,classifier,pid,lastImprovment,improvmentTime,lastTime");
            alreadyPrintedHeader = true;
        }
        System.out.println(dataset + ";" + graspMethod + ";" + host + ";" + classifier + ";"+pid+ ";"+ lastImprovment + "/" + (iterations.size() - 1) + ";" + improvedInt.getCurrentTime() + ";" + lastIt.getCurrentTime());


//        improvedInt.print();
//        System.out.println("Last improvmnet: "+lastImprovment);

//        System.out.println(improvedInt.getCurrentTime() + ";" + improvedInt.accuracy + ";" + improvedInt.iterationNumber + ";" + improvedInt.noImprovments + ";" + improvedInt.numberEvaluation + ";" + improvedInt.subset);
    }

    //@TODO: testar o código de cima, se nao funcionar, programar algo assim:
    //kdd
    //wsn
    //cicids
    //- grasp
    //- grasp_rvnd
    //- grasp_vnd
    //-- J48
    //-- NaiveBayes
    //-- RandomForest
    //-- RandomTree
    //-- REPTree
    //---- cluster01
    //---- cluster04
    //---- cluster05
    //------ <PID> []
    //-------- begin_time
    //-------- last_evaluation
    //-------- iterations
    //----------- 0 []
    //----------- N [N].currentTime
    //----------- N [N].noImprovments
    //i = N - noImprovments
    //[i].currentTime
}
