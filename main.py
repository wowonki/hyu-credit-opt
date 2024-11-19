import logging 

from src.dataloader import DataLoader
from src.solver import solve_lp
from src.util import setup_logger

logger = setup_logger()

def main():
    dataloader = DataLoader()
    variables, A, B, L = dataloader.load()
    
    with open("result.txt", 'w') as f:
        for args in enumerate(variables[:10]):
            result = solve_lp(args, L, A, B)
            data = result[1]
            f.write(f"{[data['elapsed'], data['status']]}\n")

if __name__ == "__main__":
    main()