package kaling.replacementalgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaling on 12/5/15.
 */
public class ReplacementAlgorithm {

    private static List<Integer>[] blockList;
    private static List<Integer>[] pageList;
    private static List excusedOderList;
    private static final int BLOCK_NUM = 4;             //内存块数量
    private static final int PAGE_NUM = 32;             //页数量
    private static final int ODER_DEFAULT_NUM = 10;     //每页的指令数
    private static final int ODER_SUM = 320;            //总的指令数
    private static int replaceTimes = 0;                //页替换的次数
    private static  int excusedTimes = 0;               //指令执行的总次数

    public static void main(String[] args) {
        System.out.println("生成随机指令中");
        init();
        showPageFaultFren();
    }

    public static void init() {
        blockList = (List<Integer>[]) new ArrayList[BLOCK_NUM];
        pageList = (List<Integer>[]) new ArrayList[PAGE_NUM];
        excusedOderList = new ArrayList<>();

        //初始化内存块
        for (int i = 0; i < blockList.length; i++) {
            blockList[i] = new ArrayList<>();
        }

        //生成320个随机指令并存入pageList中
        for (int i = 0; i < PAGE_NUM; i++) {
            pageList[i] = new ArrayList<>();
            for (int j = 0; j < ODER_DEFAULT_NUM; j++) {
                int tempOder = (int) (ODER_SUM * Math.random());
                pageList[i].add(tempOder);
            }
        }

        //将指令随机执行，直到所有的指令都执行完毕
        while (excusedOderList.size() < 319){
            int tempPageIndex = (int) (PAGE_NUM * Math.random());
            int tempOderIndex = (int) (ODER_DEFAULT_NUM * Math.random());
            int tempOder = pageList[tempPageIndex].get(tempOderIndex);
            List<Integer> tempPage = pageList[tempPageIndex];
            if (isExcused(tempPageIndex,tempOderIndex)){
                excusedTimes++;
                continue;
            }else {
                int address = visitOder(tempOder, tempPage);
                excusedTimes++;
                addToExcusedList(tempPageIndex, tempOderIndex);
                System.out.println("这条指令名为：" + tempOder + ".这条指令的物理地址是：" + address);
            }
        }


    }

    /**
     * 获得缺页率
     */
    public static void showPageFaultFren() {
        System.out.println();
        System.out.println("页面置换的次数是：" + replaceTimes);
        double pageFaultfreq = (double)(replaceTimes + BLOCK_NUM) / ODER_SUM;
        System.out.println("缺页率是" + pageFaultfreq);
    }

    /**
     * 在内存块中访问指令，有就返回物理地址，没有就其它操作之后返回物理地址
     *
     * @param oder 指令
     * @return 指令地址
     */
    public static int visitOder(int oder, List page) {
        int address = -1;

        //指令已经在内存块中
        if (haveOder(oder)) {
            System.out.print("指令在内存块中，");
            address = getOderAddress(oder);
        }

        //指令不在内存块中
        else {
            //有空闲的内存，直接将页装入
            if (isBlockFree()) {
                loadPage(page);
                address = getOderAddress(oder);
                System.out.print("指令不在在内存块中，但是有空闲的内存块，");
            }
            //没有空闲的内存块，按FIFO置换
            else {
                loadPageBlockFull(page);
                addReplaceTimes();
                address = getOderAddress(oder);
                System.out.print("指令不在内存块中，并且没有空闲的内存块，将页载入内存块，");
            }
        }

        return address;
    }

    /**
     * 查看指令是否在内存块中
     *
     * @param oder
     * @return
     */
    public static boolean haveOder(int oder) {
        for (int i = 0; i < blockList.length; i++) {
            if (blockList[i].contains(oder))
                return true;
        }
        return false;
    }

    /**
     * 获取指令的物理地址
     *
     * @param oder
     * @return 指令的物理地址
     */
    public static int getOderAddress(int oder) {
        int address = 0;                 //实际物理地址
        int singleBlockAddress = 0;      //单个内存块里的地址
        for (int i = 0; i < blockList.length; i++) {
            if (blockList[i].contains(oder)) {
                singleBlockAddress = blockList[i].indexOf(oder);
                address = i * ODER_DEFAULT_NUM + singleBlockAddress;
            }
        }

        return address;
    }

    /**
     * 检测是否有空闲的内存块
     *
     * @return
     */
    public static boolean isBlockFree() {
        boolean isfree = false;
        for (List tempList : blockList) {
            if (tempList.isEmpty()) {
                isfree = true;
            }
        }
        return isfree;
    }

    /**
     * 当内存块有闲余的时候，将页装入内存块
     *
     * @param page 需要装入的页
     */
    public static void loadPage(List page) {
        for (int i = 0; i < blockList.length; i++) {
            if (blockList[i].isEmpty()) {
                blockList[i] = page;
                break;
            }
        }
    }

    /**
     * 在内存块不够的情况下装入页面
     *
     * @param page 需要哦装入的页
     */
    public static void loadPageBlockFull(List page) {
        for (int i = 0; i < blockList.length - 1; i++) {
            blockList[i] = blockList[i + 1];
        }
        blockList[blockList.length - 1] = page;
    }


    /**
     * 获取替换页的次数
     * @return
     */
    public static void addReplaceTimes() {
        ++replaceTimes;
    }

    /**
     * 指令是否执行过
     * @param pageListIndex
     * @param oderIndex
     * @return
     */
    public static boolean isExcused(int pageListIndex, int oderIndex){
        int tempOder = pageListIndex * ODER_DEFAULT_NUM + oderIndex;
        if (excusedOderList.contains(tempOder)){
            return true;
        }
        return false;
    }

    /**
     * 添加指令到已经执行过的列表
     * @param pageListIndex
     * @param oderIndex
     */
    public static void addToExcusedList(int pageListIndex, int oderIndex){
        int excusedOderTemp = pageListIndex * ODER_DEFAULT_NUM + oderIndex;
        excusedOderList.add(excusedOderTemp);
    }
}
