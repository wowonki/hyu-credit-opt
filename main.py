import numpy as np

from src.dataloader import DataLoader
from src.solver import solve_lp
from src.util import setup_logger

logger = setup_logger()

def main():
    dataloader = DataLoader()
    variables, A, B, L = dataloader.load()
    cp_return = dataloader.get_matrix_cp_return()
    
    # Version 1.
    with open("result_status.csv", 'w') as file_status, open("result_weights.csv", 'w') as file_weight, open("result_value.csv", "w") as file_value:
        
        # columns of result_status.txt file
        status_cols = ["prob_num", "status", 'elapsed']
        file_status.write(",".join(status_cols) + "\n")   # write column
        
        value_cols = ["prob_num", "risk", 'return']
        file_value.write(",".join(value_cols) + "\n")   # write column

        for args in enumerate(variables):
            result = solve_lp(args, L, A, B)
            
            # write weights & values
            if result[1]['status'] == 'optimal':
                weight = np.array(result[1]['x'])
                risk = result[1]['y']
                rtn = np.dot(cp_return, weight)
                
                weight_str = ','.join(map(str, weight))
                file_weight.write(str(result[0]) + ',' + weight_str + "\n")
                
                value_str = ",".join(map(str, [risk, rtn]))
                file_value.write(str(result[0]) + ',' + value_str + "\n")
                
                
            else:
                file_weight.write(str(result[0]) + '\n') 
                
            # write status
            status_str = ",".join(map(str, [v for k, v in result[1].items() if k in status_cols]))
            file_status.write(str(result[0]) + ',' + status_str + "\n")

    
if __name__ == "__main__":
    main()