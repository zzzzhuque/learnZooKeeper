package com.zhutao.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributedLockTest {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // 设置成final否则线程无法运行
        final DistributedLock lock1 = new DistributedLock();
        final DistributedLock lock2 = new DistributedLock();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.zkLock();
                    System.out.println("线程1启动，获取到锁");
                    Thread.sleep(5 * 1000);
                    lock1.zkUnLock();
                    System.out.println("线程1释放锁");
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.zkLock();
                    System.out.println("线程2启动，获取到锁");
                    Thread.sleep(5 * 1000);
                    lock2.zkUnLock();
                    System.out.println("线程2释放锁");
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
