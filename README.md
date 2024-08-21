### Charmroom-Back

## 설정 파일

<details>
  
<summary> application.yml, application-test.yml </summary>

```yml
spring:
  application:
    name: charmroom
  datasource:
    url: jdbc:mysql://[DB주소]/[테이블명]?serverTimezone=Asia/Seoul
    username: [DB계정]
    password: '[DB계정 비밀번호]'
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: create #스키마 생성 후 none으로 변경
  data:
    redis:
      host: [redis 주소]
      port: [redis port]
  security:
    oauth2:
      client:
        registration:
          naver:
            client-name: naver
            client-secret: [naver client-secret]
            client-id: [naver client-id]
            redirect-uri: http://[주소]/login/oauth2/code/naver
            authorization-grant-type: authorization_code
            scope: name, email, profile_image
          google:
            client-name: google
            client-secret: [google client-secret]
            client-id: [google client-id]
            redirect-uri: http://[주소]/login/oauth2/code/google
            authorization-grant-type: authorization_code
            scope: profile, email
          kakao:
            client-name: kakao
            client-secret: [kakao client-secret]
            client-id: [kakao client-id]
            redirect-uri: http://[주소]/login/oauth2/code/kakao
            authorization-grant-type: authorization_code
            scope: profile_nickname, profile_image, account_email
            client-authentication-method: client_secret_post
        provider:
          kakao:
            user-name-attribute: id
            user-info-authentication-method: header
            user-info-uri: https://kapi.kakao.com/v2/user/me
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
          naver:
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            user-info-uri: https://openapi.naver.com/v1/nid/me
            user-name-attribute: response
            token-uri: https://nid.naver.com/oauth2.0/token
  
jwt:
  secret: [jwt secret]
charmroom:
  upload:
    image:
      path: [image upload path]
    attachment:
      path: [attachment upload path]
  cors:
    allowed-origins: [front 주소]
```
</details>

<details>
  
<summary> application.properties, application-test.properties </summary>

```properties
spring.application.name=charmroom

spring.datasource.url=jdbc:mysql://[DB주소]/[테이블명]?serverTimezone=Asia/Seoul
spring.datasource.username=[DB계정]
spring.datasource.password=[DB계정 비밀번호]
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=create


spring.data.redis.host=[redis 주소]
spring.data.redis.port=[redis port]


spring.security.oauth2.client.registration.naver.client-name=naver
spring.security.oauth2.client.registration.naver.client-secret=[naver client-secret]
spring.security.oauth2.client.registration.naver.client-id=[naver client-id]
spring.security.oauth2.client.registration.naver.redirect-uri=http://[주소]/login/oauth2/code/naver
spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name, email, profile_image

spring.security.oauth2.client.registration.google.client-name=google
spring.security.oauth2.client.registration.google.client-secret=[google client-secret]
spring.security.oauth2.client.registration.google.client-id=[google client-id]
spring.security.oauth2.client.registration.google.redirect-uri=http://[주소]/login/oauth2/code/google
spring.security.oauth2.client.registration.google.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.google.scope=profile, email

spring.security.oauth2.client.registration.kakao.client-name=kakao
spring.security.oauth2.client.registration.kakao.client-secret=[kakao client-secret]
spring.security.oauth2.client.registration.kakao.client-id=[kakao client-id]
spring.security.oauth2.client.registration.kakao.redirect-uri=http://[주소]/login/oauth2/code/kakao
spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.kakao.scope=profile_nickname, profile_image, account_email
spring.security.oauth2.client.registration.kakao.client-authentication-method=client_secret_post


spring.security.oauth2.client.provider.kakao.user-name-attribute=id
spring.security.oauth2.client.provider.kakao.user-info-authentication-method=header
spring.security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me
spring.security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token

spring.security.oauth2.client.provider.naver.authorization-uri=https://nid.naver.com/oauth2.0/authorize
spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me
spring.security.oauth2.client.provider.naver.user-name-attribute=response
spring.security.oauth2.client.provider.naver.token-uri=https://nid.naver.com/oauth2.0/token


jwt.secret=[jwt secret]


charmroom.upload.image.path=[image upload path]
charmroom.upload.attachment.path=[attachment upload path]

charmroom.cors.allowed-origins=[front 주소]

```

</details>

