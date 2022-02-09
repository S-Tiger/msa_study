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
