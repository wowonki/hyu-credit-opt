import time 
import logging 

from cvxopt import solvers
from cvxopt import matrix

logger = logging.getLogger(__name__)

solvers.options['glpk'] = {'msg_lev': 'GLP_MSG_OFF'} # glpk solver 로 풀 때 출력 안하게.

def solve_lp(args: tuple[int, list]|list[int, list],
             L: matrix,
             A: matrix,
             B: matrix,
             solver='glpk'):
    start = time.time()
    
    i, used_vars = args
    used_const = [0, 1, 2] # weights upper, lower bound & return lower bound
    for j in used_vars:
        used_const.append(j+3)
        used_const.append(j+3003)
    
    opt = solvers.lp(L[used_vars], A[used_const, used_vars], B[used_const], solver=solver)
    end = time.time()
    
    opt['elapsed'] = end - start
    opt['prob_num'] = i
    logger.info(f"'solve_lp' RUN COMPLETELY | STATUS: {opt['status']}")
    return i, opt
