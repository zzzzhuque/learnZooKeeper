package com.zhutao.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock {
    private String connectionString = "172.17.0.12:2181,172.17.0.13:2181,172.17.0.14:2181";
    private int sessionTimeOut = 2000;
    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    private String waitPath;
    private String currentMode;

    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        // 获取连接
        zk = new ZooKeeper(connectionString, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                // connectLatch如果连接上zk，可以释放
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }

                // waitLatch如果是删除该节点的操作，可以释放
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });

        // 阻塞，直到zk成功连接
        countDownLatch.await();

        // pan判断/locks是否存在
        Stat stat = zk.exists("/locks", false);

        if (stat == null) {
            // 创建一个根节点
            zk.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    // 对zk加锁
    public void zkLock() throws InterruptedException, KeeperException {
        // 创建对应的临时带序号节点
        currentMode = zk.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // 判断创建的节点是否是最小序号节点，如果是则获取到锁；如果不是，监听该序号的前一个节点
        List<String> children = zk.getChildren("/locks", false);

        // 如果children只有一个值，那就直接获取锁；如果有多个节点，需要判断谁最小
        if (children.size() == 1) {
            return;
        } else {
            Collections.sort(children);

            // 获取节点名称seq-00000
            String thisNode = currentMode.substring("/locks/".length());
            // 通过节点名称获取当前节点的位置
            int index = children.indexOf(thisNode);

            // 判断
            if (index == -1) {
                System.out.println("数据异常");
            } else if (index == 0) {
                // /locks下只包含当前节点
                return;
            } else {
                // 需要监听前一个节点
                waitPath = "/locks/" + children.get(index - 1);
                zk.getData(waitPath, true, null);

                // 等待监听结束，获取到锁后返回
                waitLatch.await();
                return;
            }
        }
    }

    // 对zk解锁
    public void zkUnLock() throws InterruptedException, KeeperException {
        // 删除节点
        zk.delete(currentMode, -1);
    }
}
