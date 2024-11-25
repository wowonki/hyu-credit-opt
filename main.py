from src.dataloader import DataLoader
from src.solver import solve_lp
from src.util import setup_logger

logger = setup_logger()

def main():
    dataloader = DataLoader()
    variables, A, B, L = dataloader.load()
    
    # Version 1.
    with open("result_status.txt", 'w') as file_status, open("result_weights.txt", 'w') as file_weight:
        
        # columns of result_status.txt file
        status_cols = ["prob_num", "status", "primal_objective", 'dual_objective', 'gap', 'relative_gap',
                       'primal_infeasibility', 'dual_infeasibility', 'primal_slack', 'dual_slack', 'res_primal', 'res_dual', 'elapsed']
        file_status.write("\t".join(status_cols) + "\n")   # write column
        
        exclude_cols = ['x', 's', 'y', 'z']
        for args in enumerate(variables):
            result = solve_lp(args, L, A, B)
            
            if result[1]['status'] == 'optimal':
                weight_str = '\t'.join(map(str, list(result[1]['x'])))
                file_weight.write(str(result[0]) + '\t' + weight_str + "\n")
                
            else:
                file_weight.write(str(result[0]) + '\n') 
                
            status_str = "\t".join(map(str, [v for k, v in result[1].items() if k not in exclude_cols]))
            file_status.write(str(result[0]) + '\t' + status_str + "\n")
    
if __name__ == "__main__":
    main()