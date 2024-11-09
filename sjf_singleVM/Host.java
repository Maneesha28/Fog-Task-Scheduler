package sjf_singleVM;

import java.util.*;

public class Host {
    private Vm vm;
    private List<Task>allTasks;
    private double simulationTime;

    public Host(Vm vm) {
        this.vm = vm;
        this.simulationTime = 0.0;
        this.allTasks = new LinkedList<>();
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }

    private void updatePriority(List<Task> tasks) {
        // Create a set to keep track of visited tasks
        Set<Task> visited = new HashSet<>();

        // Iterate through each task in the list
        for (Task task : tasks) {
            // Perform BFS for unvisited tasks
            if (!visited.contains(task)) {
                dfs(task, visited);
            }
        }
    }

    private void dfs(Task currentTask, Set<Task> visited) {
        // Mark the current task as visited
        visited.add(currentTask);
        // Recursively visit child tasks
        int priorityScore = 0;
        for (Task childTask : currentTask.getChildTasks()) {
            if (!visited.contains(childTask)) {
                dfs(childTask, visited);
            }
            priorityScore += (childTask.getPriorityScore() + (4 - childTask.getOriginalPriorityLevel()));
        }
        currentTask.setPriorityScore(priorityScore);
    }

    private void sortTasks() {
        Collections.sort(this.allTasks);
    }
    public void submitTasks(List<Task> tasks) {
        updatePriority(tasks);
        allTasks.addAll(tasks);
        sortTasks();
        System.out.println("print start in submitTasks");
        for(Task task : allTasks) {
            System.out.println(task);
        }
        System.out.println("print end in submitTasks");
    }

    public void simulate() {
        //initVM();
        while(!allTasks.isEmpty() ) {
            //timing();
            if(vm.getCurrentTask() != null) {
                Task task = vm.getCurrentTask();
                for(Task childTask : task.getChildTasks()) {
                    childTask.incNumParentsFinished();
                }
                vm.endTask();
                this.simulationTime = task.getEndTime();
                allTasks.remove(task);
            } else if(vm.getCurrentTask() == null && !allTasks.isEmpty()) {
                for(int i = 0; i < allTasks.size(); i++) {
                    Task task = allTasks.get(i);
                    if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                        task.setStartTime(simulationTime);
                        vm.startTask(task);
                        break;
                    }
                }
            }
        }

    }
}
