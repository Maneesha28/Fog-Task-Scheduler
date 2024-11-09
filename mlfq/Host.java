package mlfq;


import java.util.*;

public class Host {
    private double timeSlices[];
    private Vm vm;

    double nextLevelUp;
    int currentLevel;
    double levelUpInterval = 20.0;
    private List<Task>allTasks;

    private List<Task>highReadyTasks;
    private List<Task>midReadyTasks;
    private List<Task>lowReadyTasks;

    private double simulationTime;

    public Host(Vm vm) {
        this.vm = vm;

        this.timeSlices = new double[3];
        this.timeSlices[0] = 1.0;
        this.timeSlices[1] = 2.0;
        this.timeSlices[2] = 3.0;

        this.nextLevelUp = 20.0;
        this.currentLevel = 0;

        this.simulationTime = 0.0;
        this.highReadyTasks = new LinkedList<>();
        this.midReadyTasks = new LinkedList<>();
        this.lowReadyTasks = new LinkedList<>();
    }

    public void submitTasks(List<Task> tasks) {
        this.allTasks = tasks;
        Collections.sort(allTasks);
        for(Task task : allTasks) {
            task.setInsRem(task.getNumMI());
        }
    }

    public void initReadyTasks() {
        for(Task task : allTasks) {
            if(task.getParentTasks().isEmpty()) {
//                System.out.println("Adding in high ready queue " + task.getTaskID());
                highReadyTasks.add(task);
            }
        }
        for(Task task : highReadyTasks) {
            allTasks.remove(task);
        }
    }


    public void levelUpTasks() {
        for(Task task : midReadyTasks) {
            midReadyTasks.remove(task);
            highReadyTasks.add(task);
        }
        for(Task task : lowReadyTasks) {
            lowReadyTasks.remove(task);
            highReadyTasks.add(task);
        }
        nextLevelUp = this.simulationTime + levelUpInterval;
    }
    public void simulate() {
        initReadyTasks();

        while(!highReadyTasks.isEmpty() || !midReadyTasks.isEmpty() || !lowReadyTasks.isEmpty()) {
            //timing();
            if(!highReadyTasks.isEmpty()) {
                this.currentLevel = 0;
            } else if(highReadyTasks.isEmpty() && !midReadyTasks.isEmpty()) {
                this.currentLevel = 1;
            } else {
                this.currentLevel = 2;
            }
            if(currentLevel == 0) {
                System.out.print("Current level " + currentLevel + " ");
                if(vm.getCurrentTask() != null) {
                    Task task = vm.getCurrentTask();
                    //check whether the task is finished in this time slice
                    double timeRem = task.getInsRem() / vm.getMips();
                    if(timeRem <= timeSlices[0]) {
                        simulationTime = simulationTime + timeRem;
                        task.setEndTime(this.simulationTime);
                        vm.endTask();
                        highReadyTasks.remove(task);
                        for(Task childTask : task.getChildTasks()) {
                            childTask.incNumParentsFinished();
                            if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                                highReadyTasks.add(childTask);
                                allTasks.remove(childTask);
                            }
                        }
                    } else {
                        task.setInsRem((int) (task.getInsRem() - vm.getMips() * timeSlices[0]));
                        simulationTime = simulationTime + timeSlices[0];
                        vm.endTimeSlice(simulationTime);
                        highReadyTasks.remove(task);
                        midReadyTasks.add(task);
                    }
                    if(simulationTime >= nextLevelUp) {
                        levelUpTasks();
                    }

                } else if(vm.getCurrentTask() == null && !highReadyTasks.isEmpty()) {
                    Task task = highReadyTasks.get(0);
                    if(task.getStartTime() < 0) {
                        task.setStartTime(simulationTime);
                        vm.startTask(task);
                    } else {
                        vm.startTimeSlice(task, simulationTime);
                    }
                }
            } else if(currentLevel == 1) {
                System.out.print("Current level " + currentLevel + " ");
                if(vm.getCurrentTask() != null) {
                    Task task = vm.getCurrentTask();
                    //check whether the task is finished in this time slice
                    double timeRem = task.getInsRem() / vm.getMips();
                    if (timeRem <= timeSlices[1]) {
                        simulationTime = simulationTime + timeRem;
                        task.setEndTime(this.simulationTime);
                        vm.endTask();
                        midReadyTasks.remove(task);
                        for (Task childTask : task.getChildTasks()) {
                            childTask.incNumParentsFinished();
                            if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                                highReadyTasks.add(childTask);
                                allTasks.remove(childTask);
                            }
                        }
                    } else {
                        task.setInsRem((int) (task.getInsRem() - vm.getMips() * timeSlices[1]));
                        simulationTime = simulationTime + timeSlices[1];
                        vm.endTimeSlice(simulationTime);
                        midReadyTasks.remove(task);
                        lowReadyTasks.add(task);
                    }
                    if(simulationTime >= nextLevelUp) {
                        levelUpTasks();
                    }
                } else if(vm.getCurrentTask() == null && !midReadyTasks.isEmpty()) {
                    Task task = midReadyTasks.get(0);
                    if(task.getStartTime() < 0) {
                        task.setStartTime(simulationTime);
                        vm.startTask(task);
                    } else {
                        vm.startTimeSlice(task, simulationTime);
                    }
                }
            } else {
                System.out.print("Current level " + currentLevel + " ");
                if(vm.getCurrentTask() != null) {
                    Task task = vm.getCurrentTask();
                    double timeRem = task.getInsRem() / vm.getMips();
                    if (timeRem <= timeSlices[2]) {
                        simulationTime = simulationTime + timeRem;
                        task.setEndTime(this.simulationTime);
                        vm.endTask();
                        lowReadyTasks.remove(task);
                        for (Task childTask : task.getChildTasks()) {
                            childTask.incNumParentsFinished();
                            if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                                highReadyTasks.add(childTask);
                                allTasks.remove(childTask);
                            }
                        }
                    } else {
                        task.setInsRem((int) (task.getInsRem() - vm.getMips() * timeSlices[2]));
                        simulationTime = simulationTime + timeSlices[2];
                        vm.endTimeSlice(simulationTime);
                        lowReadyTasks.remove(task);
                        lowReadyTasks.add(task);
                    }
                    if(simulationTime >= nextLevelUp) {
                        levelUpTasks();
                    }
                } else if(vm.getCurrentTask() == null && !lowReadyTasks.isEmpty()) {
                    Task task = lowReadyTasks.get(0);
                    if(task.getStartTime() < 0) {
                        task.setStartTime(simulationTime);
                        vm.startTask(task);
                    } else {
                        vm.startTimeSlice(task, simulationTime);
                    }
                }
            }

        }

    }
}
