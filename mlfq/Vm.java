package mlfq;


public class Vm {
    private int vmID;
    private int mips;

    private Task currentTask;
    private int status;

    private double nextEventTime;

    public Vm(int vmID, int mips) {
        this.vmID = vmID;
        this.mips = mips;
        this.status = 0;
        this.nextEventTime = 1.0e+30;
    }

    public int getVmID() {
        return vmID;
    }
    public int getMips() {
        return mips;
    }

    public void setMips(int mips) {
        this.mips = mips;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void startTask(Task currentTask) {
        this.currentTask = currentTask;
        if (currentTask != null) {
            this.status = 1;
        } else {
            this.status = 0;
        }
        currentTask.setVmID(this.vmID);
        System.out.println("Simulation Time: "+ currentTask.getStartTime() +" Vm is now running task " + currentTask.getTaskID());

    }

    public void startTimeSlice(Task currentTask, double simulationTime) {
        this.currentTask = currentTask;
        if (currentTask != null) {
            this.status = 1;
        } else {
            this.status = 0;
        }
        System.out.println("Simulation Time: "+ simulationTime +" Vm is now running time slice of task " + currentTask.getTaskID());
    }
    public int getStatus() {
        return status;
    }
    public void setNextEventTime(double nextEventTime) {
        this.nextEventTime = nextEventTime;
    }
    public double getNextEventTime() {
        return this.nextEventTime;
    }
    public int endTask() {
        int taskID = currentTask.getTaskID();
        System.out.println("Simulation Time: "+ currentTask.getEndTime() +" Vm has finished task " + taskID);
        this.currentTask = null;
        this.status = 0;
        return taskID;
    }

    public int endTimeSlice(Double simulationTime) {
        int taskID = currentTask.getTaskID();
        System.out.println("Simulation Time: "+ simulationTime +" Vm has finished time slice of task " + taskID);
        this.currentTask = null;
        this.status = 0;
        return taskID;
    }

}
