For starting, create .env file in main folder with params params below and write all values that you want to be after =. <br>

#.env file <br>
#All values must be unique <br>
POSTGRESDB_USER= <br>
POSTGRESDB_ROOT_PASSWORD= <br>
POSTGRES_ORDER_DATABASE= <br>
POSTGRES_STOCK_DATABASE= <br>

POSTGRESDB_DOCKER_STOCKDB_PORT= <br>
POSTGRESDB_LOCAL_STOCKDB_PORT= <br>

POSTGRESDB_DOCKER_ORDERDB_PORT= <br>
POSTGRESDB_LOCAL_ORDERDB_PORT= <br>

SPRING_DOCKER_ORDER_SERVICE_PORT= <br>
SPRING_DOCKER_STOCK_SERVICE_PORT= <br>

SPRING_LOCAL_ORDER_SERVICE_PORT= <br>
SPRING_LOCAL_STOCK_SERVICE_PORT= <br>

KAFKA_BROKER_1_EXTERNAL_PORT= <br>
KAFKA_BROKER_2_EXTERNAL_PORT= <br>
KAFKA_BROKER_3_EXTERNAL_PORT= <br>

KAFKA_BROKER_PORT= <br>
KAFKA_CONTROLLER_PORT= <br>

You can start all services with one comand, but all services will be at one container: 
1) docker-compose up

Or you can start all services in several containers with commands:
1) docker-compose -f ./docker-compose-kafka.yml up
2) docker-compose -f ./docker-compose-orderdb.yml up
3) docker-compose -f ./docker-compose-stockdb.yml up
4) docker-compose -f ./docker-compose-order-service.yml up
5)  docker-compose -f ./docker-compose-stock-service.yml up

