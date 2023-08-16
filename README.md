# RMS
### 예약관리 시스템

- Architecutre
 ```
+--------------+         +--------------+         +--------------+
| Discord Bot  | <--->   | Kafka        | <--->   | Server       |
+--------------+         +--------------+         +--------------+
                                 |                         |
                                 v                         v
                          +--------------+         +--------------+ 
                          | ZooKeeper    |         | DB           |
                          +--------------+         +--------------+ 
```

- Docker-Compose
  - discordbot , demo 는 별도의 Maven build 필요함 - mvn clean package
  - discordbot token 미포함
```
version: '3'

services:
  zookeeper:                                    # 서비스 명
    image: wurstmeister/zookeeper               # 사용할 이미지
    container_name: zookeeper                   # 컨테이너명 설정 
    ports:                                      # 접근 포트 설정 (컨테이너 외부:컨테이너 내부)
      - "2181:2181"       
    volumes:
      - ./zookeeper-data:/data
      - ./zookeeper-logs:/datalog
  kafka:                                        # 서비스 명
    image: wurstmeister/kafka                   # 사용할 이미지
    container_name: kafka                       # 컨테이너명 설정
    ports:                                      # 접근 포트 설정 (컨테이너 외부:컨테이너 내부)
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1      
      KAFKA_CREATE_TOPICS: "Topic:1:1"
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    volumes:
      - ./kafka-data:/var/lib/kafka/data
  discordbot:                                   
    build:
      context: ./discordbot
      dockerfile: Dockerfile
    depends_on:
      - kafka
  demo:
    build:
      context: ./demo
      dockerfile: Dockerfile
    depends_on:
      - kafka
volumes:
  kafka-data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: ./kafka-data
  zookeeper-data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: ./zookeeper-data
  zookeeper-logs:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: ./zookeeper-logs
```
