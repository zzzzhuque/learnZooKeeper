#!/bin/bash

case $1 in
"start"){
  for i in 172.17.0.12 172.17.0.13 172.17.0.14
  do
    echo ------------ zookeeper $i start --------------
    ssh $i "/root/zookeeper-3.5.9/bin start"
  done
}
;;
"stop"){
  for i in 172.17.0.12 172.17.0.13 172.17.0.14
  do
    echo ------------ zookeeper $i stop --------------
    ssh $i "/root/zookeeper-3.5.9/bin stop"
  done
}
;;
"status"){
for i in 172.17.0.12 172.17.0.13 172.17.0.14
  do
    echo ------------ zookeeper $i status --------------
    ssh $i "/root/zookeeper-3.5.9/bin status "
  done
}
;;
esac