For starting, create .env file in main folder with params params below and write all values that you want to be after =.

#.env file
POSTGRESDB_USER=
POSTGRESDB_ROOT_PASSWORD=
POSTGRES_ORDER_DATABASE=
POSTGRES_STOCK_DATABASE=

POSTGRESDB_DOCKER_STOCKDB_PORT=
POSTGRESDB_LOCAL_STOCKDB_PORT=

POSTGRESDB_DOCKER_ORDERDB_PORT=
POSTGRESDB_LOCAL_ORDERDB_PORT=

SPRING_DOCKER_ORDER_SERVICE_PORT=
SPRING_DOCKER_STOCK_SERVICE_PORT=

SPRING_LOCAL_ORDER_SERVICE_PORT=
SPRING_LOCAL_STOCK_SERVICE_PORT=

KAFKA_BROKER_1_EXTERNAL_PORT=
KAFKA_BROKER_2_EXTERNAL_PORT=
KAFKA_BROKER_3_EXTERNAL_PORT=

KAFKA_BROKER_PORT=
KAFKA_CONTROLLER_PORT=

You can start all services with one comand, but all services will be at one container: 
1) docker-compose up

Or you can start all services in several containers with commands:
1) docker-compose -f ./docker-compose-kafka.yml up
2) docker-compose -f ./docker-compose-orderdb.yml up
3) docker-compose -f ./docker-compose-stockdb.yml up
4) docker-compose -f ./docker-compose-order-service.yml up
5)  docker-compose -f ./docker-compose-stock-service.yml up

