
![header](https://capsule-render.vercel.app/api?type=waving&height=300&color=0:BE93C5,50:86A8E7,100:91EAE4&text=참방&fontAlign=50&fontAlignY=40&animation=fadeIn&rotate=0&textBg=false&fontColor=203A43&reversal=true&desc=Charmroom)

### 참방은 토이프로젝트로 수영 커뮤니티 참방을 만들 목표로 만들어진 팀 입니다.
<br>

## 팀원 소개

참방은 2명의 팀원으로 구성한 작은 팀 입니다.<br>
팀원 수가 적은 만큼 모든 과정은 두 팀원이 함께 진행하였습니다.

|<img src="https://avatars.githubusercontent.com/u/35218494?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/155498348?v=4" width="150" height="150"/>|
|:-:|:-:|
|Dong-Yeop Lee<br/>[@Yeop-Dong](https://github.com/Yeop-Dong)|ChaeMin Lim<br/>[@cmleem](https://github.com/cmleem)|

## 기술 스택

### 백엔드
![spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![spring-boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![spring-security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)

![mysql](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![redis](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![jwt](https://img.shields.io/badge/json%20web%20tokens-323330?style=for-the-badge&logo=json-web-tokens&logoColor=pink)
![oauth2](https://img.shields.io/badge/oauth2-EB5424?style=for-the-badge&logo=auth0&logoColor=white)

### 협업도구
![git](https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white)
![github](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)
![notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)
![zoom](https://img.shields.io/badge/Zoom-2D8CFF?style=for-the-badge&logo=zoom&logoColor=white)

### 개발도구
- Spring Tools Suite
- IntelliJ IDEA

## 개요

수영 커뮤니티 사이트 참방의 백엔드를 설계 및 구현합니다.

CI/CD 파이프라인을 구성하여 Develop 브랜치가 업데이트 될 때 마다 테스트 서버로 지속적으로 배포 및 테스트를 진행하였습니다.

주요 기능은 다음과 같습니다.

- 계정 관련
  - 회원가입 기능을 제공한다.
  - 회원가입시 기입한 ID/PW로 로그인 기능을 제공한다.
  - OAuth2를 이용 네이버, 구글, 카카오의 계정으로 소셜 로그인 기능을 제공한다.
  - 회원은 본인의 정보를 수정 및 탈퇴할 수 있다.
  
- 게시판
  - 관리자는 게시판을 임의로 추가, 수정, 삭제할 수 있다
  - 게시판 별로 메인 노출 여부가 존재하며 메인 노출되는 게시판을 조회하는 API를 제공한다.
  - 게시판 별 글 쓰기 권한이 존재한다.
  - 게시판에는 종류를 지정할 수 있으며 종류에 따라 저장되는 내용이 다르다
    - `LIST` 타입: 게시글이 제목, 내용을 포함하는 일반적인 게시판
    - `GALLERY` 타입: 게시글이 이미지를 반드시 첨부하여야 하는 게시판
    - `MARKET` 타입: 게시글이 중고거래 물품의 가격, 이미지 등을 반드시 첨부하여야 하는 게시판
   
- 게시글
  - 게시글은 게시판에 종속되며 작성한 본인과 관리자만 수정 삭제할 수 있다.
  - 게시글에 댓글을 작성, 수정, 삭제할 수 있다.
  - 댓글에는 대댓글을 작성, 수정, 삭제할 수 있으며, 대댓글의 깊이는 제한하지 않는다.
  - 게시글에는 좋아요/싫어요 중 1개를 계정당 최대 1개 표시할 수 있다.
  - 댓글, 대댓글에는 좋아요/싫어요 중 1개를 계정당 최대 1개 표시할 수 있다.
  
- 광고
  - 관리자는 광고를 임의로 추가, 수정, 삭제할 수 있다.
  - 광고는 게시 기간이 존재하며 현재 시간에 따라 게시 기간 내인 광고를 조회하는 API를 제공한다.

- 구독
  - 회원은 다른 회원을 구독할 수 있으며 구독한 회원들의 글을 조회하는 API를 제공한다.

더 자세한 기능에대한 설명 및 기능 개발을 위한 명세 내용은 다음을 참고해주세요
- [기능 명세](https://charmbang.notion.site/afc5725e62c642518f2f96379a39d9a7?v=d822de9831e14776b2853c81838bab44&pvs=4)
- [API 명세](https://charmbang.notion.site/API-fbe9b72c4f334093b2cb20d34d66b0e2?pvs=4)
- [ERD](https://github.com/CharmRoom/.github/blob/main/profile/ERD.png)

## 백엔드 아키텍처

최종적으로 백엔드 코드는 Linux 환경에서 작동되는 AWS에 배포할 전제로 작성하였습니다.

각 팀원의 개발 환경은 Windows이므로 테스트를 위해 크로스 플랫폼에 대응 가능하도록 신경써서 개발하였습니다(하단 설정 방법 참조).<br/>
배포 이전 테스트를 위해 [@Yeop-Dong](https://github.com/Yeop-Dong) 팀원의 개인 PC에 WSL을 이용하여 테스트 서버를 만들어놓고 CD시 해당 서버로 배포하여 테스트하였습니다.

운영체제 이외의 아키텍처는 다음과 같습니다.

- MySQL 8.0
- Redis 7.2.4
- SpringBoot 3.3.0

## 설정 및 빌드

설정 파일을 다음과 같이 작성하여 gradle로 빌드합니다.

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

![footer](https://capsule-render.vercel.app/api?type=waving&height=300&color=0:BE93C5,50:86A8E7,100:91EAE4&fontAlign=50&fontAlignY=40&animation=fadeIn&rotate=0&textBg=false&fontColor=203A43&reversal=false&desc=Charmroom&section=footer)


