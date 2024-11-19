from time import time
import multiprocessing as mp

from cvxopt import solvers



def solve_lp(args):
    i, var = args
    start = time()
    cons=[0,1,2]
    s_result=[]
    
    for j in var:
        cons.append(j+3)
        cons.append(j+3003)
        
    s_left_c = left_coefficient[cons,var]
    s_right_c = right_constraint[cons]
    s_cost = Cost[var]
    sol=solvers.lp(s_cost,s_left_c,s_right_c, solver = 'glpk')
    end=time()
    solve_time=end-start

    s_result.append(solve_time)
    s_result.append(sol['status'])
    s_result.append(sol['x'])
    s_result.append(sol['primal objective'])
    
    return i, s_result

if __name__ == '__main__':
    # CPU 코어 수 조절
    num_cores = mp.cpu_count()
    used_cores = max(1, num_cores - 1)  # 1개의 코어는 여유롭게 남김
    pool = mp.Pool(processes=used_cores)
    
    # 작업을 더 작은 배치로 나누기
    batch_size = 1000  # 한 번에 처리할 작업 수 조절
    args = list(enumerate(vars))
    
    # 결과 파일을 주기적으로 저장
    with open('result.txt', mode='w', encoding='utf-8') as f_result:
        for i in range(0, len(args), batch_size):
            batch_args = args[i:i+batch_size]
            
            for i, s_result in pool.imap_unordered(solve_lp, batch_args):
                result1 = str(s_result) + '\n'
                f_result.write(result1)
                f_result.flush()  # 주기적으로 디스크에 저장
                
            print(f'Batch {i//batch_size + 1} completed')
    
    pool.close()
    pool.join()

#-----------------------------------------------------LP 풀고 데이터 출력 완료-----------------------------------------------------