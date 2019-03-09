#!/bin/sh

psql -U challenge -d presence_control -f insert_master_data.sql
