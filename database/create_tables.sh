#!/bin/sh

psql -U user -d presence_control -f create_tables.sql
