package customThreeVm;


import java.util.*;

public class Host {
    private Vm vms[];
    private Vm vm;
    private PriorityQueue<Task>highPriorityTasks;
    private PriorityQueue<Task>midPriorityTasks;
    private PriorityQueue<Task>lowPriorityTasks;

    private PriorityQueue<Task>highReadyTasks;
    private PriorityQueue<Task>midReadyTasks;
    private PriorityQueue<Task>lowReadyTasks;

    private int nextEventVmType;

    private double simulationTime;

    public Host(Vm highResourceVm, Vm midResourceVm, Vm lowResourceVm) {
        this.vms = new Vm[3];
        this.vms[0] = highResourceVm;
        this.vms[1] = midResourceVm;
        this.vms[2] = lowResourceVm;

        this.simulationTime = 0.0;
        this.highPriorityTasks = new PriorityQueue<>();
        this.midPriorityTasks = new PriorityQueue<>();
        this.lowPriorityTasks = new PriorityQueue<>();
        this.highReadyTasks = new PriorityQueue<>();
        this.midReadyTasks = new PriorityQueue<>();
        this.lowReadyTasks = new PriorityQueue<>();
    }

    public PriorityQueue<Task> getHighPriorityTasks() {
        return highPriorityTasks;
    }
    public PriorityQueue<Task> getMidPriorityTasks() {
        return midPriorityTasks;
    }

    public PriorityQueue<Task> getLowPriorityTasks() {
        return lowPriorityTasks;
    }

    private void updatePriority(List<Task> tasks) {
        // Create a set to keep track of visited tasks
        Set<Task> visited = new HashSet<>();

        // Iterate through each task in the list
        for (Task task : tasks) {
            // Perform DFS for unvisited tasks
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
        int maxPriorityLevel = currentTask.getOriginalPriorityLevel();
        for (Task childTask : currentTask.getChildTasks()) {
            if (!visited.contains(childTask)) {
                dfs(childTask, visited);
            }
            priorityScore += (childTask.getPriorityScore() + (4 - childTask.getOriginalPriorityLevel()));
            maxPriorityLevel = Math.min(maxPriorityLevel, childTask.getImposedPriorityLevel());
        }
        currentTask.setPriorityScore(priorityScore);
        currentTask.setImposedPriorityLevel(maxPriorityLevel);
    }


    public void submitTasks(List<Task> tasks) {
        updatePriority(tasks);

        for (Task task : tasks) {

            if (task.getImposedPriorityLevel() == 1) {
                task.setVmID(0);
                highPriorityTasks.add(task);
            } else if (task.getImposedPriorityLevel() == 2) {
                task.setVmID(1);
                midPriorityTasks.add(task);
            } else if (task.getImposedPriorityLevel() == 3) {
                task.setVmID(2);
                lowPriorityTasks.add(task);
            }
        }
    }

    public void initReadyTasks() {
        for(Task task : highPriorityTasks) {
            if(task.getParentTasks().isEmpty()) {
                highReadyTasks.add(task);
            }
        }
        for(Task task : highReadyTasks) {
            highPriorityTasks.remove(task);
        }
        for(Task task : midPriorityTasks) {
            if(task.getParentTasks().isEmpty()) {
                midReadyTasks.add(task);
            }
        }
        for(Task task : midReadyTasks) {
            midPriorityTasks.remove(task);
        }
        for(Task task : lowPriorityTasks) {
            if(task.getParentTasks().isEmpty()) {
                lowReadyTasks.add(task);
            }
        }
        for(Task task : lowReadyTasks) {
            lowPriorityTasks.remove(task);
        }

    }

    public void initVMs() {
        if(!highReadyTasks.isEmpty()) {
            vms[0].setNextEventTime(0.0);
        }
        if(!midReadyTasks.isEmpty()) {
            vms[1].setNextEventTime(0.0);
        }
        if(!lowReadyTasks.isEmpty()) {
            vms[2].setNextEventTime(0.0);
        }
    }

    public void updateReadyQueue(Task task) {
        int vm_id = getMinLoadVM();
        task.setVmID(vm_id);
        if (task.getVmID() == 0) {
            highReadyTasks.add(task);
            highPriorityTasks.remove(task);
        } else if (task.getVmID() == 1) {
            midReadyTasks.add(task);
            midPriorityTasks.remove(task);
        } else if (task.getVmID() == 2) {
            lowReadyTasks.add(task);
            lowPriorityTasks.remove(task);
        }
        if(vms[vm_id].getStatus() == 0) {
            vms[vm_id].setNextEventTime(simulationTime);
        }
    }

    private int getMinLoadVM() {
        int min_length = Integer.MAX_VALUE;
        int vm_id = -1;
        if(highReadyTasks.size() < min_length) {
            min_length = highReadyTasks.size();
            vm_id = 0;
        }
        if(midReadyTasks.size() < min_length) {
            min_length = midReadyTasks.size();
            vm_id = 1;
        }
        if(lowReadyTasks.size() < min_length) {
            min_length = lowReadyTasks.size();
            vm_id = 2;
        }
        return vm_id;
    }

    public void timing() {
        double minTimeNextEvent = 1.0e+30;
        nextEventVmType = 0;
        for (int i = 0; i < 3; i++) {
            if (vms[i].getNextEventTime() < minTimeNextEvent) {
                minTimeNextEvent = vms[i].getNextEventTime();
                nextEventVmType = i;
            }
        }
        this.simulationTime = minTimeNextEvent;
    }

    public void simulate() {
        initReadyTasks();
        initVMs();
        while(!highReadyTasks.isEmpty() || !midReadyTasks.isEmpty() || !lowReadyTasks.isEmpty()) {
            timing();
            if(nextEventVmType == 0) {
                if(vms[0].getCurrentTask() != null) {
                    Task task = vms[0].getCurrentTask();
                    for(Task childTask : task.getChildTasks()) {

                        childTask.incNumParentsFinished();
                        if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                            updateReadyQueue(childTask);
                        }
                    }
                    vms[0].endTask();
                    highReadyTasks.remove(task);
                }
                if(vms[0].getCurrentTask() == null && !highReadyTasks.isEmpty()) {
                    Task task = highReadyTasks.peek();
                    task.setStartTime(simulationTime);
                    vms[0].startTask(task);
                }
            } else if(nextEventVmType == 1) {
                if(vms[1].getCurrentTask() != null) {
                    Task task = vms[1].getCurrentTask();
                    for(Task childTask : task.getChildTasks()) {
                        childTask.incNumParentsFinished();
                        if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                            updateReadyQueue(childTask);
                        }
                    }
                    vms[1].endTask();
                    midReadyTasks.remove(task);
                }
                if(vms[1].getCurrentTask() == null && !midReadyTasks.isEmpty()) {
                    Task task = midReadyTasks.peek();
                    task.setStartTime(simulationTime);
                    vms[1].startTask(task);
                }
            } else if(nextEventVmType == 2) {
                if(vms[2].getCurrentTask() != null) {
                    Task task = vms[2].getCurrentTask();
                    for(Task childTask : task.getChildTasks()) {
                        childTask.incNumParentsFinished();
                        if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                            updateReadyQueue(childTask);
                        }
                    }

                    vms[2].endTask();
                    lowReadyTasks.remove(task);
                }
                if(vms[2].getCurrentTask() == null && !lowReadyTasks.isEmpty()) {
                    Task task = lowReadyTasks.peek();
                    task.setStartTime(simulationTime);
                    vms[2].startTask(task);
                }
            }
        }

    }
}
