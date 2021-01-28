# Knowledge Graph Generator(Neo4j)
지식그래프 생성<br>
파일명 : "ks_generator_neo4j.ipynb"
#### *Made by soyoung cho - Updated(2021.01.28)* ####

----------
### 1. 목적
* 로컬DB에 지식구조가 생성되었음을 바탕으로 진행하며, DB내 지식구조를 지식그래프화 하는 작업 진행


----------
### 2. 특징
* Neo4j는 그래프 구성에 적합한 NoSQL 데이터베이스
* Cypher라는 언어 사용


----------
### 3. 설치할 것
* Neo4j : https://neo4j.com/download/

* pip install py2neo : 파이썬에서 neo4j를 사용하기 위함


----------
### 4. 실행방법

(1) DB 연결
> user, passwd, host, db를 로컬DB설정에 맞게 입력

(2) 데이터 선택
> knowledge_structure[인덱스] : 그래프화 하고 싶은 지식구조의 인덱스 번호를 기재<br>
> 띄어쓰기로 구분된 문장을 edge리스트 형태로 변형

(3) Neo4J 연결
> 지식구조 리스트를 neo4j형태로 변형하여 transaction commit을 진행함.

----------
### 5.생성된 Neo4j 그래프

![neo4j그래프](https://user-images.githubusercontent.com/28869864/106124175-247f9480-619e-11eb-9a17-61af335015b8.png)

