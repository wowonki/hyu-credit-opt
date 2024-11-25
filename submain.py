# 안 풀린 문제들 중 IRR 탓에 안풀린 문제가 얼마나 있는지 확인
# Ex) 여기서 푸는 LP가 infeasible 하다면 다른 조건의 문제  
#     문제가 풀렸으나 maximum return이 요구수익률보다 낮은 경우 수익률의 문제로 안 풀린 것.  

import numpy as np
import pandas as pd
from cvxopt import solvers, matrix, sparse

from src.dataloader import DataLoader
from src.util import setup_logger

logger = setup_logger()
solvers.options['glpk'] = {'msg_lev': 'GLP_MSG_OFF'} # glpk solver 로 풀 때 출력 안하게.

def submain():
    # Load data
    dataloader = DataLoader()
    variables = dataloader.get_vars()
    upper_bound_list = dataloader.get_point_2x0().copy()
    cpreturns = dataloader.get_matrix_cp_return()
    
    mean_rtn = pd.Series(cpreturns, index=range(3000))

    # find unsolved problems
    unsolved_idx = []
    with open("result_status.txt") as f:
        for l in f.readlines()[1:]:
            llist = l.split("\t")
            if llist[1] != 'optimal':
                unsolved_idx.append(int(llist[0]))

    L2 = matrix(-np.array(cpreturns))
    A2 = sparse(matrix(np.concatenate([np.ones((1, 3000)), 
                                    -np.ones((1, 3000)), 
                                    -np.eye(3000), 
                                    np.eye(3000)], axis=0)))
    B2 = matrix([1, -1, matrix(np.zeros((3000, 1))), matrix(upper_bound_list)])

    # Check problem can be solved  
    with open("result_infeasible_test.txt", 'w') as f:
        f.write("\t".join(['prob_num', 'max_return']) + "\n")
        
        for idx in unsolved_idx:
            used_vars = variables[idx]
            
            used_const = [0, 1]
            for j in used_vars:
                used_const.append(j+2)
                used_const.append(j+3002)
                
            opt = solvers.lp(L2[used_vars], A2[used_const, used_vars], B2[used_const], solver='glpk')
            
            if opt['status'] == 'optimal':
                maximum_rtn = np.dot(opt['x'].T, mean_rtn[used_vars].values)[0]
            else:
                maximum_rtn = -1
            
            logger.info(f"'solve_lp' RUN COMPLETELY | STATUS: {opt['status']}")
            f.write("\t".join(map(str, [idx, maximum_rtn])) + "\n")

if __name__ == "__main__":
    submain()