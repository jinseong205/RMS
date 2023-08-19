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
                          | ZooKeeper    |         | Database     |
                          +--------------+         +--------------+ 
```

**요약**

예약 관리 시스템을 간소화 하여 구현하였습니다.
DiscordBot이 수집한 예약 정보 시스템을 Apache Kafka의 이벤트 메세징을 이용하여 Server로 전달합니다.
해당 Server에서는 예약 정보를 데이터베이스에 저장함과 동시에 웹소켓을 통해 실시간으로 화면을 통해 전달합니다.
전달된 예약 정보는 사용자가 승인 / 거절을 진행합니다. 

**개발/운영 환경**

 Server는 Spring Boot 를 Clinet는 Discord와 Thymeleaf 를 이용하여 구현하였습니다. Docker Continer를 이용하여 Kafka, ZooKeeper, MySQL의 환경을 관리하였습니다. Docker-Compose를 통해 빌드 된 jar 파일과 컨테이너를 실행합니다.

**Apache Kafka**

[Producer] Discord bot에서 수집한 예약 정보 이벤트 메세징을 생성합니다.[Consumer] Server에서는 수집한 이벤트 예약정보를 DB에 저장 및 웹소켓을 통해 화면으로 전달합니다.

**Model & REST API**

 기본 조회 화면에 대해서는 viewResolver와 model을 이용하여 view와 데이터를 전달합니다.
 이외의 CURD에 대해서는 server측으로 REST API를 이용하여 데이터의 전달하였습니다.


**실행방법 (Docker-Compose)**
  - discordbot , demo 는 별도의 Maven build 필요함 - mvn clean package
  - discordbot token 미포함
