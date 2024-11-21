import logging 
import multiprocessing

from src.dataloader import DataLoader
from src.solver import solve_lp
from src.util import setup_logger

logger = setup_logger()


def task_lp_v2(variables_batch, batch_idx, L, A, B):
    results = []
    for i, row_variable in zip(batch_idx, variables_batch):
        results.append(solve_lp((i, row_variable), L, A, B))
    
    return results


def task_lp_v3(queue, i, row_variable, L, A, B):
    queue.put(solve_lp((i, row_variable), L, A, B))


def main():
    dataloader = DataLoader()
    variables, A, B, L = dataloader.load()
    
    # # Version 1.
    # with open("result.txt", 'w') as f:
    #     for args in enumerate(variables[:10]):
    #         result = solve_lp(args, L, A, B)
    #         data = result[1]
    #         f.write(f"{[data['elapsed'], data['status']]}\n")
            
    
    # # Version 2.
    # # !Error: AttributeError: Can't pickle local object 'main.<locals>.task_lp'
    # with multiprocessing.Pool(processes=2) as pool:
    #     async_results = [pool.apply_async(task_lp_v2, args=(variables[i:i*10], range(i, i*10), L, A, B)) for i in range(2)]
    #     results = [res.get() for res in async_results]
    # print("Results:", results)
    
    
    # Version 3.
    queue = multiprocessing.Queue()
    for i in range(0, 8, 4):
        processes = [multiprocessing.Process(target= task_lp_v3, args= (queue, i+j, variables[i+j], L, A, B)) for j in range(4)]
        
        for process in processes:
            process.start()

        for process in processes:
            process.join()
        
        results = [queue.get() for _ in range(4)]
        print("Results:", results)
    
if __name__ == "__main__":
    main()