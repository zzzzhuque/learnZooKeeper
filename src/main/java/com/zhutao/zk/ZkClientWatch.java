package com.zhutao.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ZkClientWatch {
    // 字符串中不能有空格
    private String connectString = "172.17.0.12:2181,172.17.0.13:2181,172.17.0.14:2181";
    // 单位毫秒
    private int sessionTimeOut = 2000;
    private ZooKeeper zkClient;

    // 创建客户端
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("----------------------------------");
                List<String> children = null;
                try {
                    children = zkClient.getChildren("/", true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
                for (String child : children) {
                    System.out.println(child);
                }
                System.out.println("----------------------------------");
            }
        });
    }

    // 通过客户端创建子节点
    public void create() throws InterruptedException, KeeperException {
        String nodeCreated = zkClient.create("/alibaba", "duolong".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 获取指定目录下所有节点并监听节点变化，true表示复用客户端初始化的监听器
    public void getChildren() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    // 判断节点是否存在
    public void exist() throws InterruptedException, KeeperException {
        Stat stat = zkClient.exists("/alibaba", false);
        System.out.println(stat);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZkClientWatch zkClientWatch = new ZkClientWatch();
        zkClientWatch.init();
        zkClientWatch.create();
        zkClientWatch.getChildren();
        zkClientWatch.exist();
    }
}
