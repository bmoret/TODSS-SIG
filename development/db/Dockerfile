FROM postgres:13-alpine

COPY schema.sql     /data/schema.sql
COPY dummy_data.sql /data/dummy_data.sql

COPY create_users.sh /docker-entrypoint-initdb.d/01-create_users.sh
COPY create_dbs.sh /docker-entrypoint-initdb.d/02-create_dbs.sh
COPY import_schema.sh /docker-entrypoint-initdb.d/03-create_schema.sh
COPY insert_data.sh /docker-entrypoint-initdb.d/04-insert_data.sh

RUN sed -i -e 's/\r$//' /docker-entrypoint-initdb.d/01-create_users.sh && \
    sed -i -e 's/\r$//' /docker-entrypoint-initdb.d/02-create_dbs.sh && \
    sed -i -e 's/\r$//' /docker-entrypoint-initdb.d/03-create_schema.sh && \
    sed -i -e 's/\r$//' /docker-entrypoint-initdb.d/04-insert_data.sh

