# 🔷 Dodal
<img width="400" alt="image" src="https://github.com/team-dodal/dodal_backend/assets/81945553/9d755a80-3092-485b-8e6f-886848e35e17">

- 같은 목표를 두고, 서로 인증하며 커뮤니케이션하는 앱 어플리케이션
- 성장을 나누는 공간, 도달 (도전의 달인)

## 📖 서비스 소개
- 목표를 지속 가능하게 해주는건 개인의 성취 기록보다 사람들과의 소통과 공감에서 더 큰 힘이 있다 생각합니다.
- 우리는 목표에 맞는 모임에서 사람들과 노력을 공유함으로써 하루를 알차게, 인생을 멋지게 꾸미고자 하는 사용자들을 필요로 합니다.
- 개인적인 하나의 목표를 위해 모인 구성원들을 관리하는 것은 힘든것을 알기에, 구성원의 관리가 쉬운 어플이 되도록 노력할게요

## 🖥 주요 기능

- 도전방 둘러보기 (관심사별, 인기별, 최근별 조회)  
- 피드 둘러보기 (전체 도전방 내 인증 완료된 이미지 피드 조회)
- 도전방 관리하기 (참가자로 참여중인 도전방, 방장으로 운영중인 도전방 관리) 
- 알림 이력 둘러보기 (인증 요청 / 완료 / 도전방 참여 등의 이벤트에 따른 Push 알림)
- 도전 기록 관리하기 (기간 / 도전 별 도전 달성한 기록 관리)

## 📌 사용 기술
- Language : Java 11
- FrameWork : Spring Boot 2.7 / JPA / QueryDSL
- Database : MySQL, Redis
- DevOps : Git, Github Actions, Docker, AWS(EC2, CodeDeploy, S3, Rds), Kafka

## 📚 느낀점 및 배운점

- 애자일 방법론 경험
    - Github Project 내 칸반보드를 활용한 이슈 사항 및 진척률 관리
    - Github 이슈번호를 활용한 브랜치 분기 관리
- CI & CD 배포 경험
    - Docker / DockerCompose 를 활용한 컨테이너 기반 인프라 구축
    - 개발 시기 비용 절감을 위한 Mac Mini 홈서버 기반 CI & CD 구축
    - 클라우드 서버 비용 절감을 위한 AWS 프리티어 / GCP 무료 크레딧 등 서버 구축 경험
- 비동기 메시지 시스템 구축 경험
    - Kafka를 활용한 비동기 메시지 기능 구현 (피드 좋아요 / 댓글, 도전방 가입 / 강퇴, 피드 요청 / 승인)
- 토큰 인증 기반 로그인 구현
    - 토큰 탈취 문제에 대응을 위한 수명 짧은 액세스 토큰 사용
    - 매 요청마다 토큰이 변조되지 않았는지 검증
- JPA 벌크 연산 경험
    - 다 건 데이터 jdbcTemplate 활용 벌크 연산 처리 
  
## 🙋🏻‍♂️ 팀원
- 백엔드 & 인프라 1인 개발  (2023.05 ~ 2024.01)

## ⚙️ 애플리케이션 구성도
<img width="1000" alt="image" src="https://github.com/team-dodal/dodal_backend/assets/81945553/44d5dcfc-c570-4b75-8931-539dda987f8b">



## ✌🏻 클라이언트 화면 
### 로그인 화면 
<img width="677" alt="image" src="https://github.com/user-attachments/assets/067dcb04-22e7-4a04-a4a7-470e6f8df82f">


### 메인 화면 
<img width="674" alt="image" src="https://github.com/user-attachments/assets/69227193-742f-4605-abcf-8c89a70d4ef9">

### 도전방 관리 화면
<img width="675" alt="image" src="https://github.com/user-attachments/assets/7e0bf07e-af78-4200-b3f2-0ba5d841125f">


### 마이페이지 화면 
<img width="675" alt="image" src="https://github.com/user-attachments/assets/38cac98d-a062-4cd0-b14f-1869d05baac4">



