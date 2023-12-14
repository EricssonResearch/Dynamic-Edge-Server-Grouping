
/**
 * it initializes all the parsing operation of the input files.
 * it triggers the taskformation algorithm
 */

package sample_app;

import core.ConfigSettings;
import task_formation.TaskFormation;

public class MainApp {

    public static void main(String[] args) {
        
        String edgeServerFile = args[0];
        String microServiceFile = args[1];
        String microServiceGraphFile = args[2];
        String outputFolder = args[3];

        // load the configurations from the configuration files
        ConfigSettings cs = ConfigSettings.getInstance();
        if(cs.initialize(edgeServerFile, microServiceFile, microServiceGraphFile) == false){
            System.out.println("cannot initialize the configuration settings");
            System.exit(0);
        }
        
        // run the task formation algorithm
        TaskFormation tf = TaskFormation.getInstance();
        tf.init();
    }
}