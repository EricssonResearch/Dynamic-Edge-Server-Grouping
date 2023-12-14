/**
 * MainApp.java initializes all the parsing operations of the input files.
 * it triggers the task allocation algorithm
 */

package sample_app;

import core.ConfigSettings;
import task_allocation.TaskAllocation;

public class MainApp {
    public static void main(String[] args) {
        String edgeServerFile = args[0];
        String applicationFile = args[1];
        String ouputFolder = args[2];

        // load the configurations from the config files
        ConfigSettings cs = ConfigSettings.getInstance();
        if(cs.initialize(edgeServerFile, applicationFile) == false) {
            System.out.println("cannot initialize the configuration settings");
            System.exit(0);
        }
        
        // run task allocation algorithm
        TaskAllocation ta = TaskAllocation.getInstance();
        ta.init();
        
        // Generate output
    }
}