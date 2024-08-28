# **Spring Cloud Netflix Eureka**
---
### **Service Discovery (서비스 디스커버리)**
MSA 에서는 여러 서비스 간의 호출로 구성이 된다.\
일반적으로 IP와 포트를 통해 호출을 하는데, 클라우드 환경에서는 IP가 동적으로 변경되는 일이 많기 때문에 정확한(유효한) 위치를 알아내는 기능이 필요하다.\
서비스 디스커버리가 이 일을 한다.


Eureka와의 통합을 위한 Spring Cloud 라이브러리는 <u>**클라이언트**</u>와 <u>**서버**</u>의 두 부분으로 구성돼 있다.


> **Server**

* 스프링 부트 애플리케이션으로 실행한다.
* 서버 API 구성
 * 등록된 서비스의 목록을 수집하기 위한 API
 * 새로운 서비스를 네트워크 위치 주소와 함께 등록하기 위한 API
* 서버의 상태를 다른 서버로 복제함으로써 안정성과 가용성을 높일 수 있다.


1.  pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

2. Application.class
```java
@EnableEurekaServer  
@SpringBootApplication  
public class DiscoveryserviceApplication {  
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryserviceApplication.class, args);
    }
}
```

3. application.yml
```yaml
server:
  port: 8761 # Eureka server 대부분 8761 사용
eureka:  
  client:  
    register-with-eureka: false # Eureka 서버에 등록한다. 서버자체는 등록 불필요 default ture
    fetch-registry: false # Eureka 서버로 부터 인스턴스들의 정보를 주기적으로 가져올 것 인지 설정하는 속성 default true
```


> **Client**

* 마이크로서비스 애플리케이션에 의존성을 포함시켜 사용한다.
* 기능
 * 애플리케이션 시작 후 서버에 등록한다.
 * 종료 전 서버에서 등록 해제를 담당한다.
 * 유레카 서버로부터 주기적으로 최신 서비스 목록을 받아온다.


1. pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

2. Application.class
```java
@EnableEurekaClient
@SpringBootApplication
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
```

3. application.yml
```yaml
spring:
  application:
    name: product # 해당 애플리케이션명으로 Eureka 서버에 등록된다.
eureka:
  client:
    register-with-eureka: true   # Eureka 서버에 등록한다 default ture
    fetch-registry: true  # Eureka 서버로 부터 인스턴스들의 정보를 주기적으로 가져올 것 인지 설정하는 속성 default true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka   # Eureka클라이언트를 등록할 Eureka서버의 주소
```




# **Spring Cloud Netflix Ribbon**
---
### **Load Balance (로드 밸런스)**
우리가 일반적으로 사용하는 LoadBalancer는 서버 사이드 로드밸런싱을 처리하는 L4 Switch와 같은 하드웨어 장비였다.\
하지만 MSA에서는 이런 장비보다는 <u>**소프트웨어적으로 구현된 클라이언트 사이드 로드밸런싱**</u>을 주로 이용한다.\
서버 사이드 로드밸런서의 단점은 기본적으로 별도의 스위치 장비가 필요하기 때문에 상대적으로 비용이 많이 소모되게 되며 유연성도 떨어지게 된다. 또한 서버 목록의 추가는 수동으로 보통 이루어진다. 이러한 단점 때문에 클라이이언트 사이드 로드밸런서가 MSA에서는 주로 사용된다.
> **Ribbon**

* Client side Load Balancer
* 서비스 이름으로 호출
* Health Check
* 비동기화 처리가 잘되지 않기 때문에 <u>**최근에는 사용하지 않는다.**</u>
  (Spring Boot 2.4에서 Maintenance 상태)
* <u>**Spring Cloud Loadbalancer**</u> 사용 권장


1. pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

2. Application.class
```java
@SpringBootApplication
public class DisplayApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(DisplayApplication.class);
    }
}
```

3. application.yml
```yaml
product: # 대상 클라이언트 명
  ribbon:
    #listOfServers: localhost:8082,localhost:7777 # 작성하지 않으면 Eureka 서버에서 해당 주소를 가져온다.
    MaxAutoRetries: 0 # 첫 실패시 같은 서버로 재시도 하는 수
    MaxAutoRetriesNextServer: 1 # 첫 실패시 다음 서버로 재시도 하는 수
```


# **Spring Cloud Netflix Hystrix**

---
### **Circuit Breaker (서킷 브레이커)**
MSA는 시스템을 여러 서비스를 컴포넌트로 나누고 각 컴포넌트끼리 서로 호출을 하는 패턴이다.\
이 패턴의 한계는 서버가 서로 종속적이라는 점이다.\
즉, Server A가 Server B를 호출 했을때 Server B가 응답을 못하거나 응답 시간이 길어진다면 Server A는 <u>**응답을 계속 기다리게 되는 한계**</u>가 있다.\
이러한 한계를 극복한 것이 **Circuit Breaker** 패턴이다.

> **Hystrix**

* Thread timeout, 장애 대응 등을 설정해 장애시 정해진 루트를 따르도록 할 수 있다.
* 미리 정해진 임계치를 넘으면 장애가 있는 로직을 실행하지 않고 우회 하도록 할 수 있다.
* <u>**최근에는 사용하지 않는다.**</u> (Spring Boot 2.4에서 Maintenance 상태)
* <u>**Resilience4j**</u> 사용 권장


1. pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

2. Application.class
```java
@EnableCircuitBreaker
@SpringBootApplication
public class DisplayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisplayApplication.class);
    }
}
```

3. service.class
```java
@Service
public class ProductRemoteServiceImpl implements ProductRemoteService {

    private static final String url = "http://product/products/";
    private final RestTemplate restTemplate;

    public ProductRemoteServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @HystrixCommand(commandKey = "productInfo", fallbackMethod = "getProductInfoFallback")
    public String getProductInfo(String productId) {
        return this.restTemplate.getForObject(url + productId, String.class);
    }

    /**
     * fallback 메소드 서킷이 오픈되거나 실패할 경우 예외처리의 역할을 하는 해당 메소드가 호출이 됨.
     */
    public String getProductInfoFallback(String productId, Throwable t) {
        System.out.println("t = " + t);
        return "[ this product is sold out ]";
    }
}
```


3. application.yml
```yaml
hystrix:
  command:
    productInfo:    # command key. use 'default' for global setting.
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 1000 # 타임아웃 시간 default 1,000ms
      circuitBreaker:
        requestVolumeThreshold: 20   # 서킷 브레이커 활성화를 위한 최소 요청횟수. default 20
        errorThresholdPercentage: 50 # 서킷 오픈에 대한 오류 백분율. default 50
```


# **Spring Cloud Netflix Zuul**
---
### **API Gateway (API 게이트웨이)**
MSA에서 언급되는 컴포넌트 중 하나이며, 모든 클라이언트 요청에 대한 end point를 통합하는 서버이다.\
마치 프록시 서버처럼 동작한다. 그리고 인증 및 권한, 모니터링, logging 등 추가적인 기능이 있다.\
모든 비지니스 로직이 하나의 서버에 존재하는 Monolithic Architecture와 달리 MSA는 도메인별 데이터를 저장하고 도메인별로 하나 이상의 서버가 따로 존재한다.\
한 서비스에 한개 이상의 서버가 존재하기 때문에 이 서비스를 사용하는 클라이언트
입장에서는 다수의 end point가 생기게 되며, end point를 변경이 일어났을때, 관리하기가 힘들다.\
그래서 MSA 환경에서 서비스에 대한 도메인인 하나로 통합할 수 있는 API GATEWAY가 필요한 것이다.

> **Zuul**

* Groovy 언어로 작성된 다양한 형태의 Filter를 실행한다.
* Filter에 기능을 정의하고, 이슈사항에 발생시 적정한 Filter를 추가함으로써 이슈사항을 대비할 수 있다.
* Spring Librarie와의 호환성 문제로 <u>**최근에는 사용하지 않는다.**</u> (Spring Boot 2.4에서 Maintenance 상태)
* <u>**Spring Cloud Gateway**</u> 사용 권장


1. pom.xml
```xml
<!-- 2.4 이상 버전에서는 실행X -->
<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.3.1.RELEASE</version>
</parent>
```
```xml
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
			<version>2.2.10.RELEASE</version>
</dependency>
```

2. Application.class
```java
@EnableZuulProxy
@SpringBootApplication
public class ZuulServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulServiceApplication.class, args);
	}
}
```

3. Filter.class
```java
@Slf4j
@Component
public class ZuulLoggingFilter extends ZuulFilter {

    @Override
    public Object run() throws ZuulException {  // 실행
        log.info("*********** printng logs: ");

        RequestContext ctx =RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("***********" + request.getRequestURI());

        return null;
    }

    @Override
    public String filterType() {    // 필터타입 사전 : pre , 경로 : route , 사후 : post , 에러 : error
        return "pre";
    }

    @Override
    public int filterOrder() {  // 필터순서
        return 1;
    }

    @Override
    public boolean shouldFilter() { // 필터사용여부
        return true;
    }
}
```

4. application.yml
```yaml
zuul:
  routes:
    first-service:
      path: /first-service/**
      url:  http://localhost:8081
    second-service:
      path: /second-service/**
      url:  http://localhost:8082
```





# **Zuul vs SCG**
---
### **Zuul와 Spring Cloud Gateway (SCG)의 차이**

1. Blocking vs non-Blocking
2. Filter only vs Predicates+Filters
3. Tomcat vs Netty


> **Blocking vs non-Blocking**

Blocking 방식은 요청을 보내고 응답이 올때까지 다음으로 진행하지 않고 기다린다. \
non-Blocking방식은 요청을 보내고 바로 다음으로 진행하여 다른 일을 하다가, 응답이 오면 그에 맞는 처리를 한다. \
<u>**Zuul 1.x는 blocking방식**</u>의 단점을 해결하기 위해 **Thread pool**을 사용. 각 트랜잭션이 별도의 Pool에서 수행되므로, 어느 정도는 blocking방식의 문제를 해결할 수 있었다. \
하지만 한계가 있었던지, <u>**Zuul 2.x에서는 non-blocking방식**</u>으로 바꿨다. \
그러나, **Zuul의 태생이 Netflix OSS여서 그런지 Spring Cloud와는 잘 안 맞는 면이 있었다.** \
그래서 Spring Cloud 커뮤니티에서 내놓은 새로운 API Gateway가 Spring Cloud Gateway이다. \
Zuul 1.x와 SCG의 성능을 비교한 몇몇 사이트가 있는데, 초기에는 Zuul이 더 좋은것으로 보고하는 사이트가 있었지만, 장기 지속 연결에 적합치 않은 측정툴을 사용하였기 때문이라는 반박을 받았다. \
최근의 테스트에서는 SCG가 훨씬 더 나은 성능을 보인다.


> **Filter only vs Predicates+Filters**

Zuul과 SCG는 동작원리 측면에서도 많이 다르다. \
Zuul이 Filter들만으로 동작하는 반면에, SCG는 Predicates(수행을 위한 사전 요구조건)와 Filter를 조합하여 동작. 

Gateway Handler Mapping이 Predicates에 지정한 경로와 일치하는지 판단하고, 
Gateway Web Handler는 지정된 필터들을 통해 요청을 전송한다. \
필터들은 요청과 응답에 대한 처리를 수행한다.


> **Tomcat vs Netty**

Zuul은 Web/WAS로 Tomcat을 사용하고, SCG는 Netty를 사용한다. \
Netty는 비동기 네트워킹을 지원하는 어플리케이션 프레임워크이다. 




# **Spring Cloud Gateway**
---

1. pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```


2. application.yml
```yaml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters: # 전체 필터 설정
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway Global Filter
            preLogger: true
            postLogger: true
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/** # 추가 작업이 없을시 Zuul과 다르게 http://localhost:8081/first-service/** 식으로 패스 경로가 붙는다.
          filters:
#            - AddRequestHeader=first-request, first-requests-header # 해당 방식으로 Header key , value로 추가 가능
#            - AddResponseHeader=first-response, first-response-header
            - CustomFilter # 라우팅 별로 필터 지정할시
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
#            - AddRequestHeader=second-request, second-requests-header
#            - AddResponseHeader=second-response, second-response-header
            - CustomFilter
```


3. route설정 java로 할시
```java
@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                            .filters(f -> f.addRequestHeader("first-request","first-request-header")
                                           .addResponseHeader("first-response","first-response-header"))
                            .uri("http://localhost:8081"))
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request","first-request-header")
                                .addResponseHeader("first-response","first-response-header"))
                        .uri("http://localhost:8081"))
                .build();
    }
}
```


4. CustomFilter.class
```java
@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
    public CustomFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE filter: request id -> {}", request.getId());

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter: response code -> {}", response.getStatusCode());
            }));
        };
    }

    public static class Config{
        // Put the configuration properties
    }
}
```


5. GlobalFilter.class
```java
@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            if (config.isPreLogger()){
                log.info("Global Filter Start: request id -> {}", request.getId());
            }
            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()){
                    log.info("Global Filter End: request code -> {}", response.getStatusCode());
                }
            }));
        };
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
```




# **Spring Cloud Config Server**
---
### **Spring Config Server**
Spring Config Server는 각 애플리케이션에의 Config 설정을 중앙 서버에서 관리를 하는 서비스이다. \
중앙 저장소로 Git Repository뿐만 아니라 JDBC, REDIS, AWS, ... 으로도 사용 가능하다. \
Spring Config Server를 이용하면 /actuator/refresh, /actuator/busrefresh 를 통해 \
<u>**서버를 재배포 없이 설정값을 변경할 수 있다는 큰 장점이 있다.**</u>

1.  pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

2. Application.class
```java
@EnableConfigServer  
@SpringBootApplication  
public class DiscoveryserviceApplication {  
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryserviceApplication.class, args);
    }
}
```

3. application.yml
```yaml
server:
  port: 8888
  
spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: 
          username: 
          password: 
```


# **Spring Cloud Bus**
---
### **Spring Config Server**
Spring Config Server는 각 애플리케이션에의 Config 설정을 중앙 서버에서 관리를 하는 서비스이다. \
중앙 저장소로 Git Repository뿐만 아니라 JDBC, REDIS, AWS, ... 으로도 사용 가능하다. \
Spring Config Server를 이용하면 /actuator/refresh, /actuator/busrefresh 를 통해 \
<u>**서버를 재배포 없이 설정값을 변경할 수 있다는 큰 장점이 있다.**</u>

1.  pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2. Application.class
```java
@EnableConfigServer  
@SpringBootApplication  
public class DiscoveryserviceApplication {  
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryserviceApplication.class, args);
    }
}
```

3. application.yml
```yaml
server:
  port: 8888
  
spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: 
          username: 
          password: 
```


# **Spring Cloud Bus**
---
1. 분산 시스템의 노드를 경량 메시지 브로커와 연결
2. 상태 및 구성에 대한 변경 사항을 연결된 노드에게 전달(Broadcast)



*   AMOP(Advanced Message Queuing Protocol), 메시지 지향 미들웨어를 쥐한 개방형 표준 응용 계층 프로토콜

 - 메시지 지향, 큐잉, 라우팅 (P2P, Publisher-Subcriber), 신뢰성, 보안
 - Erlang, RabbitMQ에서 사용

*   Kafka 프로젝트

 - Apache Software Foundation이 Scalar언어로 개발한 오픈 소스 메시지 브로커 프로젝트
 - 분산형 스트리밍 플랫폼
 - 대용량의 데이터를 처리 가능한 메시징 시스템


* RabbitMQ

 - 메시지 브로커
 - 초당 20+ 메시지를 소비자에게 전달
 - 메시지 전달 보장, 시스템 간 메시지 전달
 - 브로커, 소비자 중심


* Kafka

 - 초당 100k+ 이상의 이벤트 처리
 - Pub/Sub, Topic에 메시지 전달
 - Ack를 기다리지 않고 전달 가능
 - 생산자 중심


# **Spring Cloud Netflix Feign Client**


* FeignClient -> HTTP Client
  - REST Call을 추상화 한 Spring Cloud Netflix 라이브러리
* 사용방법
  - 호출하려는 HTTP Endpoint에 대한 Interface를 생성
  - @FeignClient 선언
* Load balanced 지원


1.  pom.xml
```xml
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

2. Application.class
```java
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication  
public class UserserviceApplication {  
    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }
}
```

3. FeignClient.interface
```java
@FeignClient(name="order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
```

* Feign Client에서 로그 사용

1. application.yml
```yml
logging:
  level:
    com.example.userservice.client: DEBUG
```

2. Application.class
```java
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication  
public class UserserviceApplication {  
    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```


3. ServiceMethod
```java
try {
            orderServiceClient.getOrders(userId);
} catch (FeignException ex){
            log.error(ex.getMessage());
}
```
<br/>

* FeignErrorDecoder

<br/>

1. FeignErrorDecoder.class
```java
@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final Environment env;

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
            case 400:
                break;
            case 404:
                if (methodKey.contains("getOrders")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                             env.getProperty("order_service.exception.orders_is_empty"));
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}
```



# **Apache Kafka**
<br/>

* Apache Software Foundation의 Scalar 언어로 된 오픈 소스 메시지 브로커 프로젝트
  - Open Source Message Broker Project

* 링크드인(Linked-in)에서 개발, 2011년 오픈 소스화
  - 2014년 11월 링크드인에서 Kafka를 개발하던 엔지니어들이 Kafka개발에 집중하기 위해 Confluent라는 회사 창립

* 실시간 데이터 피드를 관리하기 위해 통일된 높은 처리량, 낮은 지연 시간을 지닌 플랫폼 제공

* Apple, Netflix, Shopify, Yelp, Kakao, New York Times 등이 사용
<br/>

1. Producer/Consumer 분리
2. 메세지를 여러 Consumer에게 허용
3. 높은 처리량을 위한 메시지 최적화
4. Scale-out 기능
5. Eco-system
<br/>


> **Kafaka Broker**

* 실행 된 Kafka 애플리케이션 서버
* 3대 이상의 Broker Cluster 구성
* Zookeeper 연동
  - 역할: 메타데이터 (Broker ID, Controller ID 등) 저장
  - Controller 정보 저장
* n개 Broker 중 1대는 Controller 기능 수행
  - Controller 역할
    - 각 Broker에게 담당 파티션 할당 수행
    - Broker 정상 동작 모니터링 관리
<br/>

> **KafkaClient**

* Kafka와 데이터를 주고받기 위해 사용하는 Java Library
  - https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
* Producer, Consumer, Admin, Stream 등 Kafka관련 API 제공
* 다양한 3rd party library 존재: C/C++, Node.js, Python, .NET 등
  - https://cwiki.apache.org/confluence/display/KAFKA/Clients

* Kafka 서버 기동

  * Zookeeper 및 Kafka 서버 구동
    - \$KAFKA_HOME/bin/zookeeper-server-start.sh  \$KAFKA_HOME/config/zookeeper.properties
    - \$KAFKA_HOME/bin/kafka-server-start.sh  \$KAFKA_HOME/config/server.properties
  * Topic 생성
    - \$KAFKA_HOME/bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092 --partitions 1
  * Topic 목록 확인
    - \$KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
  * Topic 정보 확인
    - \$KAFKA_HOME/bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092

* Kafka Producer/Consumer 테스트

  * 메시지 생산

    - \$KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic quickstart-events

  * 메시지 소비

    - \$KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic quickstart-events -from-beginning
<br/>

> **Kafka Connect**

* Kafka Connect를 통해 Data를 Import/Export 가능
* 코드 없이 Configuration으로 데이터를 이동
* Standalone mode, Distribution mode 지원
  - RESTful API 통해 지원
  - Stream 또는 Batch 형태로 데이터 전송 가능
  - 커스텀 Connector를 통한 다양한 Plugin 제공 (File,S3,Hive,Mysql,etc...)
* 가져오는 쪽을 Kafka Connect Source 보내는 쪽을 Kafka Connect Sink라 칭함
  - Source System -> Kafka Connect Source -> Kafka Cluster -> Kafka Connect Sink -> Target System
* Kafka Connect 구동
  
  - \$KAFKA_CONNECT_HOME/bin/connect-distributed \$KAFKA_CONNECT_HOME/etc/kafka/connect-distributed.properties
* Kafka Source Connect 사용
```java
echo'
{
    "name" : "my-source-connect", ## 커넥터명
    "config" : {
        "connector.class" : "io.confluent.connect.jdbc.JdbcSourceConnector",
        "connection.url":"jdbc:mysql://localhost:3306/db", 
        "connection.user":"",
        "connection.password":"",
        "mode": "incrementing", ## 자동증가모드
        "incrementing.column.name" : "", ## 자동증가 컬럼
        "table.whitelist":"", ## 변경감지할 테이블
        "topic.prefix" : "", ## 저장할토픽명 prefix_테이블 
        "tasks.max" : "1"
    }
}
' | curl -X POST -d @- http://localhost:8083/connectors --header "content-Type:application/json"
```
 * Kafka Source Connect 사용
 ```java
echo'
{
    "name" : "my-sink-connect", ## 커넥터명
    "config" : {
        "connector.class" : "io.confluent.connect.jdbc.JdbcSourceConnector",
        "connection.url":"jdbc:mysql://localhost:3306/db", 
        "connection.user":"",
        "connection.password":"",
        "auto.create":"", ##디비에 해당 토픽과 같은 이름의 테이블 생성(true/false)
	"auto.evolve":"true",
	"delete.enabled":"false",
	"tasks.max":"1",
	"topics":"" ## 토픽의 이름
    }
}
' | curl -X POST -d @- http://localhost:8083/connectors --header "content-Type:application/json"
```

