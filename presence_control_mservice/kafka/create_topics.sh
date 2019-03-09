#!/bin/sh

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic clockevents
