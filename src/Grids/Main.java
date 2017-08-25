package Grids;

import Uniwork.Appl.NGApplication;

public class Main extends NGApplication {

    protected GridManager FGridManager;

    @Override
    public void DoInitialize() {
        super.DoInitialize();
        FGridManager.setLogManager(FLogManager);
        FGridManager.Initialize();
    }

    @Override
    public void DoAfterInitialize() {
        super.DoAfterInitialize();
        FGridManager.ShowStages();
    }

    @Override
    public void DoFinalize() {
        super.DoFinalize();
        FGridManager.Finalize();
    }

    public Main() {
        super();
        FGridManager = new GridManager(FPrimaryStage);
        FName = "Grids";
        FDescription = "Grids is a tool for pixel graphic";
        FConfigurationFilename = "resources/config.acf";
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
