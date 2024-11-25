# HYU Data Structure Team project 

## Data Download
All data can be download from   
https://www.dropbox.com/scl/fi/xky0ctk9klgbp7as5vqme/ds_data.zip?rlkey=kmyna89ht65j39wcw6v9x223h&dl=0   
and 4 files in that link must be placed in 'data/' directory.

## dataloader.py  

### Features 

- 데이터를 불러와 cvxopt 에 의해 풀리도록 데이터를 가공하는 프로그램  
- data/ 디렉토리에 수업시간에 공유받은 데이터 파일들이 다운받아져 있어야 함.  
- LHS의 constraint coefficient matrix가 0이 많은 sparse matrix이기 때문에 cvxopt가 제공하는 sparse matrix object를 사용(dense matrix를 사용했을 때 보다 약 0.1초정도 빨라짐)  

### TO-DO

- [ ] vars.txt 파일을 불러오는 `get_vars` 메서드의 실행속도가 느려서 개선할 가능성이 있으면 개선  

Example. 
```python
from src.dataloader import DataLoader
dataloader = DataLoader()
variables, A, B, L = dataloader.load()  
```

## solver.py  

### Features  
- solve_lp 함수 하나 정의.
- args 들을 입력받아 linear programming을 품.  
- cvxopt.solvers.lp 의 반환값을 문제 번호와 함께 반환  
-  `glpk` solver를 이용해 해결 

### TO-DO

- [ ] 다운받은 데이터로 linear programming을 풀었을 때 풀지 못하는 문제를 풀도록 개선
	- Ex) glpk solver로 풀리지 않는 것은 다른 알고리즘으로 푼다든지, (다만 이 경우 푼 알고리즘의 이름을 데이터로 따로 저장해야 할 것)  

## main.py  

- Dataloader로 데이터를 불러와 linear programming을 풀어 파일로 저장하는 프로그램  
- result_status.txt: 문제를 푼 결과를 요약한 정보 
	- prob_num: vars.txt 파일 상의 라인 번호 = 문제번호
	- status: 문제를 풀었는지 (optimal / primal infeasible) 
	- ...
	- elased: 문제를 푸는데 걸린 시간(초)  
- result_weight: linear programming으로 찾은 최적 X 벡터 