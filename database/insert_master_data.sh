#!/bin/sh

psql -U user -d presence_control -f insert_master_data.sql
