# Knowledge Graph Generator
지식그래프 생성

#### *Made by cheonsol lee - Updated(2020.11.03)* ####

----------
### 1. 목적
* 로컬DB에 지식구조가 생성되었음을 바탕으로 진행하며, DB내 지식구조를 지식그래프화 하는 작업 진행


----------
### 2. 특징
* 지식그래프는 2가지 파일로 구성됨.
1. 데이터.js   : node, edge, weight에 대한 세부정보 기술
2. 데이터.html : 데이터.js, vis.js(시각화 라이브러리)를 로드하여 지식그래프 추출


----------
### 3. 설치할 것

* import pymysql : mysql사용
* import networkx : edge, weight입력시 node를 자동으로 추출함.


----------
### 4. 실행방법

(1) DB 연결
> user, passwd, host, db를 로컬DB설정에 맞게 입력

(2) 데이터 선택
> knowledge_structure[인덱스] : 그래프화 하고 싶은 지식구조의 인덱스 번호를 기재<br>
> 띄어쓰기로 구분된 문장을 edge리스트 형태로 변형

(3) Data JavaScript File 생성
> 키워드.js, 키워드.html 2가지 파일이 생성됨<br>
> file_name변수에 출력할 파일명만 입력하면 지식그래프 추출됨
