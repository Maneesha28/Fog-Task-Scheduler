package sjf_singleVM;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static String directory = "..\\Data Generator\\Dataset\\Varying_ratio";
    public static String outputDir = ".\\src\\Varying Ratio Results";
    public static void runMultiple() {
        // loop through all the files located in the directory "E:\4-1\Thesis\Thesis\Data Generator\Varying_num_tasks"
        // and run the simulation for each file
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        int itr = 0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                //need the number from the fileName to be used as the iteration number
                String[] parts = fileName.split("_");
                // strip the .csv from the end
                parts[2] = parts[2].substring(0, parts[2].length() - 4);
                runSingleSimulation(file.getName(), Integer.parseInt(parts[2]));

            }
        }
    }

    public static void runSingleSimulation(String csvFileName, int itr) {
        Vm vm = new Vm(0, 17500);
        //create a host
        Host host = new Host(vm);

        //create 10 tasks of different priority levels
        List<Task> tasks = new ArrayList<Task>();

        // read from csv file
        String csvFile = directory + "\\" + csvFileName;
        String line = "";
        String csvSplitBy = ","; // CSV delimiter
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Use comma as separator
                String[] taskData = line.split(csvSplitBy);

                int taskId = Integer.parseInt(taskData[0]);
                int priority = Integer.parseInt(taskData[1]);
                int lengthMIPS = Integer.parseInt(taskData[2]);
                int deadline = Integer.parseInt(taskData[3]);

                List<Integer> predecessors = new ArrayList<>();

                if (taskData.length > 4 && !taskData[4].isEmpty()) {
                    predecessors = Arrays.stream(taskData[4].split(";"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                }

                Task task = new Task(taskId, priority, lengthMIPS, deadline);

                tasks.add(task);

                // add parent, child
                for (Integer predecessor : predecessors) {
                    for (Task t : tasks) {
                        if (t.getTaskID() == predecessor) {
                            t.addChildTasks(task);
                            task.addParentTasks(t);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //submit tasks to host
        host.submitTasks(tasks);

        System.out.println("Submitted tasks");


        System.out.println("All tasks:");
        for(Task task : host.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println();
        //start simulation
        host.simulate();

        //print out results
        System.out.println("Simulation finished");
        System.out.println("Total number of tasks: " + tasks.size());

        writeToFile(tasks, itr);
    }

    public static void writeToFile(List<Task> tasks, int itr) {
        //writing to a csv file
        String outputFilename = outputDir + "\\SingleVM\\result_" + itr + ".csv";

        try {
            FileWriter csvWriter = new FileWriter(outputFilename);
            csvWriter.append("TaskID");
            csvWriter.append(",");
            csvWriter.append("originalPriorityLevel");
            csvWriter.append(",");
            csvWriter.append("imposedPriorityLevel");
            csvWriter.append(",");
            csvWriter.append("VmID");
            csvWriter.append(",");
            csvWriter.append("priorityScore");
            csvWriter.append(",");

            csvWriter.append("MI");
            csvWriter.append(",");
            csvWriter.append("Start Time");
            csvWriter.append(",");
            csvWriter.append("End Time");
            csvWriter.append(",");
            csvWriter.append("Deadline");
            csvWriter.append(",");
            csvWriter.append("Deadline Violation Time");
            csvWriter.append("\n");
            for(Task task : tasks) {
                System.out.println(task);
                csvWriter.append(String.valueOf(task.getTaskID()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getOriginalPriorityLevel()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getImposedPriorityLevel()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getVmID()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getPriorityScore()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getNumMI()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getStartTime()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getEndTime()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getDeadline()));
                csvWriter.append(",");
                csvWriter.append(String.valueOf(task.getDeadlineViolationTime()));
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
            System.out.println("Results written to single_vm_results.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runMultiple();
    }
}