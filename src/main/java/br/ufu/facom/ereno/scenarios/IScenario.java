package br.ufu.facom.ereno.scenarios;

public interface IScenario {

    void run();

    void setupDevices();

    void runDevices();

    void exportDataset();
}
