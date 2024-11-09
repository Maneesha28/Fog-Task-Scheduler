package chowdhuryThreeVm;

import java.util.*;

public class Host {
    //take a array of vm with size 3
    private Vm vms[];
    private Vm vm;

    private int highQLimit;
    private int midQLimit;
    private List<Task>highPriorityTasks;
    private List<Task>midPriorityTasks;
    private List<Task>lowPriorityTasks;


    private int nextEventVmType;

    private double simulationTime;

    public Host(Vm highResourceVm, Vm midResourceVm, Vm lowResourceVm, int highQLimit, int midQLimit) {
        this.vms = new Vm[3];
        this.vms[0] = highResourceVm;
        this.vms[1] = midResourceVm;
        this.vms[2] = lowResourceVm;
        this.highQLimit = highQLimit;
        this.midQLimit = midQLimit;
        this.simulationTime = 0.0;
        this.highPriorityTasks = new LinkedList<>();
        this.midPriorityTasks = new LinkedList<>();
        this.lowPriorityTasks = new LinkedList<>();
    }

    public List<Task> getHighPriorityTasks() {
        return highPriorityTasks;
    }
    public List<Task> getMidPriorityTasks() {
        return midPriorityTasks;
    }

    public List<Task> getLowPriorityTasks() {
        return lowPriorityTasks;
    }


    private void sortTasks() {
        Collections.sort(highPriorityTasks);
        Collections.sort(midPriorityTasks);
        Collections.sort(lowPriorityTasks);
    }
    public void submitTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.getOriginalPriorityLevel() == 1) {
                if(highPriorityTasks.size() < highQLimit) {
                    task.setImposedPriorityLevel(1);
                    highPriorityTasks.add(task);
                } else if (midPriorityTasks.size() < midQLimit) {
                    task.setImposedPriorityLevel(2);
                    midPriorityTasks.add(task);
                } else {
                    task.setImposedPriorityLevel(3);
                    lowPriorityTasks.add(task);
                }
            } else if (task.getOriginalPriorityLevel() == 2) {
                if(midPriorityTasks.size() < midQLimit) {
                    task.setImposedPriorityLevel(2);
                    midPriorityTasks.add(task);
                } else {
                    task.setImposedPriorityLevel(3);
                    lowPriorityTasks.add(task);
                }
            } else {
                task.setImposedPriorityLevel(3);
                lowPriorityTasks.add(task);
            }
        }

        for (Task task : highPriorityTasks) {
            task.setVmID(0);
        }
        for (Task task : midPriorityTasks) {
            task.setVmID(1);
        }
        for (Task task : lowPriorityTasks) {
            task.setVmID(2);
        }
        sortTasks();
    }

    public void initVMs() {
        for(int i = 0; i < highPriorityTasks.size(); i++) {
            Task task = highPriorityTasks.get(i);
            if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                vms[0].setNextEventTime(simulationTime);
                break;
            }
        }
        for(int i = 0; i < midPriorityTasks.size(); i++) {
            Task task = midPriorityTasks.get(i);
            if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                vms[1].setNextEventTime(simulationTime);
                break;
            }
        }
        for(int i = 0; i < lowPriorityTasks.size(); i++) {
            Task task = lowPriorityTasks.get(i);
            if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                vms[2].setNextEventTime(simulationTime);
                break;
            }
        }
    }

    public void updateVMNextEvent(Task task) {
        if (task.getVmID() == 0) {
            if(vms[0].getStatus() == 0) {
                vms[0].setNextEventTime(simulationTime);
            }
        } else if (task.getVmID() == 1) {
            if(vms[1].getStatus() == 0) {
                vms[1].setNextEventTime(simulationTime);
            }
        } else if (task.getVmID() == 2) {
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
        initVMs();
        while(!highPriorityTasks.isEmpty() || !midPriorityTasks.isEmpty() || !lowPriorityTasks.isEmpty()) {
            timing();
            if(nextEventVmType == 0) {
                if(vms[0].getCurrentTask() != null) {
                    System.out.println("Ending task");
                    Task task = vms[0].getCurrentTask();
                    for(Task childTask : task.getChildTasks()) {
                        childTask.incNumParentsFinished();
                        if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                            updateVMNextEvent(childTask);
                        }
                    }
                    vms[0].endTask();
                    highPriorityTasks.remove(task);
                }
                if(vms[0].getCurrentTask() == null && !highPriorityTasks.isEmpty()) {
                    for(int i = 0; i < highPriorityTasks.size(); i++) {
                        Task task = highPriorityTasks.get(i);
                        if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                            task.setStartTime(simulationTime);
                            vms[0].startTask(task);
                            break;
                        }
                    }
                }
            } else if(nextEventVmType == 1) {
                if(vms[1].getCurrentTask() != null) {
                    Task task = vms[1].getCurrentTask();
                    for(Task childTask : task.getChildTasks()) {
                        childTask.incNumParentsFinished();
                        if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                            updateVMNextEvent(childTask);
                        }
                    }
                    vms[1].endTask();
                    midPriorityTasks.remove(task);
                }
                if(vms[1].getCurrentTask() == null && !midPriorityTasks.isEmpty()) {
                    for(int i = 0; i < midPriorityTasks.size(); i++) {
                        Task task = midPriorityTasks.get(i);
                        if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                            task.setStartTime(simulationTime);
                            vms[1].startTask(task);
                            break;
                        }
                    }
                }
            } else if(nextEventVmType == 2) {
                if(vms[2].getCurrentTask() != null) {
                    Task task = vms[2].getCurrentTask();
                    for(Task childTask : task.getChildTasks()) {
                        childTask.incNumParentsFinished();
                        if(childTask.getParentTasks().size() == childTask.getNumParentsFinished()) {
                            updateVMNextEvent(childTask);
                        }
                    }

                    vms[2].endTask();
                    lowPriorityTasks.remove(task);
                }
                if(vms[2].getCurrentTask() == null && !lowPriorityTasks.isEmpty()) {
                    for(int i = 0; i < lowPriorityTasks.size(); i++) {
                        Task task = lowPriorityTasks.get(i);
                        if(task.getParentTasks().size() == task.getNumParentsFinished()) {
                            task.setStartTime(simulationTime);
                            vms[2].startTask(task);
                            break;
                        }
                    }
                }
            }

        }

    }
}
