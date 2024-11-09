package sjf;


import java.util.*;

public class Host {
    //take a array of vm with size 3
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


    public void submitTasks(List<Task> tasks) {

        for (Task task : tasks) {

            if (task.getOriginalPriorityLevel() == 1) {
                task.setVmID(0);
                highPriorityTasks.add(task);
            } else if (task.getOriginalPriorityLevel() == 2) {
                task.setVmID(1);
                midPriorityTasks.add(task);
            } else if (task.getOriginalPriorityLevel() == 3) {
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
        if (task.getVmID() == 0) {
            highReadyTasks.add(task);
            highPriorityTasks.remove(task);
            if(vms[0].getStatus() == 0) {
                vms[0].setNextEventTime(simulationTime);
            }
        } else if (task.getVmID() == 1) {
            midReadyTasks.add(task);
            midPriorityTasks.remove(task);
            if(vms[1].getStatus() == 0) {
                vms[1].setNextEventTime(simulationTime);
            }
        } else if (task.getVmID() == 2) {
            lowReadyTasks.add(task);
            lowPriorityTasks.remove(task);
            if(vms[2].getStatus() == 0) {
                vms[2].setNextEventTime(simulationTime);
            }
        }
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
