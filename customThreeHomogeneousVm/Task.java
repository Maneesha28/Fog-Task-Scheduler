package customThreeHomogeneousVm;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class Task implements Comparable<Task> {
    private int taskID;
    private int numMI;
    private double deadline;
    private int vmID;
    private double startTime;
    private double endTime;
    private int originalPriorityLevel;
    private int imposedPriorityLevel;
    private List<Task>parentTasks;
    private int numParentsFinished;
    private List<Task>childTasks;
    private int priorityScore;
    private double waitingTime;
    private double deadlineViolationTime;

    public Task(int taskID, int priorityLevel, int numMI, double deadline) {
        this.taskID = taskID;
        this.originalPriorityLevel = priorityLevel;
        this.imposedPriorityLevel = priorityLevel;
        this.numMI = numMI;
        this.deadline = deadline;
        this.parentTasks = new ArrayList<>();
        this.numParentsFinished = 0;
        this.childTasks = new ArrayList<>();
    }

    public int getVmID() {
        return vmID;
    }
    public void setVmID(int vmID) {
        this.vmID = vmID;
    }

    public double getDeadline() {
        return deadline;
    }
    public double getDeadlineViolationTime() {
        return deadlineViolationTime;
    }

    public void setDeadlineViolationTime(double deadlineViolationTime) {
        this.deadlineViolationTime = deadlineViolationTime;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
        this.deadlineViolationTime = max(0, endTime - deadline);
    }

    public double getEndTime() {
        return endTime;
    }


    public int getNumMI() {
        return numMI;
    }

    public void setNumMI(int numMI) {
        this.numMI = numMI;
    }

    public int getOriginalPriorityLevel() {
        return originalPriorityLevel;
    }

    public void setOriginalPriorityLevel(int originalPriorityLevel) {
        this.originalPriorityLevel = originalPriorityLevel;
    }
    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public int getImposedPriorityLevel() {
        return imposedPriorityLevel;
    }

    public void setImposedPriorityLevel(int imposedPriorityLevel) {
        this.imposedPriorityLevel = imposedPriorityLevel;
    }

    public List<Task> getParentTasks() {
        return parentTasks;
    }

    public int getNumParentsFinished() {
        return numParentsFinished;
    }
    public void incNumParentsFinished() {
        numParentsFinished++;
    }
    public void addParentTasks(Task parentTask) {
        this.parentTasks.add(parentTask);
    }

    public List<Task> getChildTasks() {
        return childTasks;
    }

    public void setChildTasks(List<Task> childTasks) {
        this.childTasks = childTasks;
    }

    public void addChildTasks(Task childTask) {
        this.childTasks.add(childTask);
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskID=" + taskID +
                ", orgPriority=" + originalPriorityLevel +
                ", imposedPriority=" + imposedPriorityLevel +
                ", numOfParents=" + parentTasks.size() +
                ", pdScore=" + priorityScore +
                ", numMI=" + numMI +
                ", vmID=" + vmID +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", deadline=" + deadline +
                ", deadlineViolationTime=" + deadlineViolationTime +
                '}';
    }


    @Override
    public int compareTo(Task o) {
        if (this.deadline == o.deadline) {
            return Integer.compare(o.priorityScore, this.priorityScore);
        } else {
            return Double.compare(this.deadline, o.deadline);
        }
    }
}
