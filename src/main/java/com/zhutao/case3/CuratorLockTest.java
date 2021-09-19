package com.zhutao.case3;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorLockTest {
    public static void main(String[] args) {
        // 创建分布式锁1
        InterProcessMutex lock1 = new InterProcessMutex(getCuratorFramework(), "/locks");
        // 创建分布式锁2
        InterProcessMutex lock2 = new InterProcessMutex(getCuratorFramework(), "/locks");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程1获取到锁");
                    lock1.acquire();
                    System.out.println("线程1再次获取到锁");
                    Thread.sleep(5 * 1000);
                    lock1.release();
                    System.out.println("线程1释放锁");
                    lock1.release();
                    System.out.println("线程1再次释放锁");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.acquire();
                    System.out.println("线程2获取到锁");
                    lock2.acquire();
                    System.out.println("线程2再次获取到锁");
                    Thread.sleep(5 * 1000);
                    lock2.release();
                    System.out.println("线程2释放锁");
                    lock2.release();
                    System.out.println("线程2再次释放锁");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    private static CuratorFramework getCuratorFramework() {
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(3000, 3);

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("172.17.0.12:2181,172.17.0.13:2181,172.17.0.14:2181")
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(2000)
                .retryPolicy(policy)
                .build();

        // 启动客户端
        client.start();
        System.out.println("zookeeper 启动成功");
        return client;
    }
}
