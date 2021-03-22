#!/bin/bash
set -e

echo "Inserting database data"

PGPASSWORD=${DB_APP_PASS} psql --username ${DB_APP_USER} ${DB_APP_NAME} --file /data/dummy_data.sql
