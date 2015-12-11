package kaling.dynamicalloc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kaling on 11/29/15.
 */
public class DynamicAllocDemo {
    private static String ASK = "ask";
    private static String RELEASE = "release";
    private static int SUM_MEMORY = 640;
    private static int USED_MEMORY = 0;

     public static class Task {
        private String action;
        private int space;
        private int id;

        Task(String action, int space, int id) {
            this.action = action;
            this.space = space;
            this.id = id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {

            return id;
        }


        public void setSpace(int space) {
            this.space = space;
        }

        public int getSpace() {

            return space;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    private static class MemoryNode {
        //是否可用
        private boolean available;
        //已用大小
        private int size;
        //用于标志占用内存的任务id
        private int taskID;



        public boolean isAvailable() {
            return available;
        }

        public void setTaskID(int taskID) {
            this.taskID = taskID;
        }

        public int getTaskID() {
            return taskID;
        }



        MemoryNode(int size, boolean available, int taskID) {
            this.size = size;
            this.available = available;
            this.taskID = taskID;
        }

        public int getSize() {
            return size;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }


    /**
     * 申请内存
     */
    public static void askMemory(List<MemoryNode> memoryNodeList, Task task) {
        MemoryNode memoryNodeTemp;
        for (int i = 0; i < memoryNodeList.size(); i++) {
            memoryNodeTemp = memoryNodeList.get(i);
            int taskSpace = task.getSpace();
            if (memoryNodeTemp.isAvailable() && memoryNodeTemp.getSize() <= taskSpace) {

                //如果内存节点的空间大于任务申请需要的节点空间，就增加一个节点
                if (memoryNodeTemp.getSize() < taskSpace) {
                    MemoryNode memoryNodeTempAdd = new MemoryNode(taskSpace - memoryNodeTemp.getSize(), true, task.getId());
                    memoryNodeList.add(i + 1, memoryNodeTempAdd);
                }

                memoryNodeTemp.setSize(task.getSpace());
                memoryNodeTemp.setAvailable(false);
            }
        }

    }

    /**
     * 合并内存
     *
     * @param memoryNodeList 合并空闲的内存链表
     */
    public static void mergeMemory(List<MemoryNode> memoryNodeList) {
        for (int i = 1; i < memoryNodeList.size(); i++) {
            MemoryNode memoryNodeTemp = memoryNodeList.get(i);
            MemoryNode memoryNodeTempSub = memoryNodeList.get(i - 1);

            if (memoryNodeTemp.isAvailable() && memoryNodeTempSub.isAvailable()) {
                memoryNodeTemp.setSize(memoryNodeTemp.getSize() + memoryNodeTempSub.getSize());
                memoryNodeList.remove(i - 1);
            }
        }
    }

    /**
     * 释放内存
     *
     * @param task
     */
    public static void realeaseMemory(List<MemoryNode> memoryNodeList, Task task) {
        for (int i = 0; i < memoryNodeList.size(); i++) {
            if (task.getId() == memoryNodeList.get(i).getTaskID()) {
                memoryNodeList.get(i).setAvailable(true);
            }
        }
    }

    /**
     * 统计内存链已用内存
     *
     * @param memoryNodeList
     */
    public static void usedMemory(List<MemoryNode> memoryNodeList) {
        for (int i = 0; i < memoryNodeList.size(); i++) {
            if (memoryNodeList.get(i).isAvailable()) {
                USED_MEMORY += memoryNodeList.get(i).getSize();
            }
        }
    }

    /**
     * 初始化任务
     *
     * @param list
     */
    public static void initTask(List<Task> list) {
        List<Task> taskList = list;

        Task task1 = new Task(ASK, 130, 1);
        taskList.add(task1);

        Task task2 = new Task(ASK, 60, 2);
        taskList.add(task2);

        Task task3 = new Task(ASK, 100, 3);
        taskList.add(task3);

        Task task4 = new Task(RELEASE, 60, 2);
        taskList.add(task4);

        Task task5 = new Task(ASK, 200, 4);
        taskList.add(task5);

        Task task6 = new Task(RELEASE, 100, 3);
        taskList.add(task6);

        Task task7 = new Task(RELEASE, 130, 1);
        taskList.add(task7);

        Task task8 = new Task(ASK, 140, 5);
        taskList.add(task8);

        Task task9 = new Task(ASK, 60, 6);
        taskList.add(task9);

        Task task10 = new Task(ASK, 50, 7);
        taskList.add(task10);

        Task task11 = new Task(RELEASE, 60, 6);
        taskList.add(task11);

    }


    public static void main(String[] args) {

        //初始化任务链表
        List<Task> listTask = new ArrayList<>();
        initTask(listTask);
        System.out.println("初始化任务链表完成");


        //初始化内存链表
        List<MemoryNode> memoryNodeList = new LinkedList<>();
        MemoryNode initMemoryListNode = new MemoryNode(SUM_MEMORY, true, 0);
        memoryNodeList.add(initMemoryListNode);
        System.out.println("初始化内存链表完成");

        System.out.println("任务链表任务申请和撤销开始");
        for (int i = 0; i < listTask.size(); i++) {
            if (listTask.get(i).getAction().equals(ASK)) {
                askMemory(memoryNodeList, listTask.get(i));
                usedMemory(memoryNodeList);
                System.out.println("已使用内存：" + USED_MEMORY + ";剩余内存为：" + (SUM_MEMORY - USED_MEMORY));
            }
            if (listTask.get(i).getAction().equals(RELEASE)) {
                realeaseMemory(memoryNodeList, listTask.get(i));
                usedMemory(memoryNodeList);
                System.out.println("已使用内存：" + USED_MEMORY + ";剩余内存为：" + (SUM_MEMORY - USED_MEMORY));
            }
        }


    }
}
