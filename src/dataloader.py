import os
import logging
from pathlib import Path

import numpy as np
from cvxopt import matrix, sparse

from src.exceptions import *

logger = logging.getLogger(__name__)

class DataLoader():
    def __init__(self):
        self._check_paths()
        self._set_paths()
        self.VARNUM = 3000
        self._ldata = None
        self._matrix_cpmean = None
        self._matrix_cpreturn = None
        self._vars = None
        self._point2x0 = None
        
    def load(self) -> tuple[list, matrix, matrix, matrix]:
        """
        Return Matrices representing following constraint and objective function
        minimize   -Lx
        subect to  Ax <= B
        
        L: 1 x N matrix(vector) - estimated mean loss for each asset `i`
        x: N x 1 matrix(vector) - weight of portfolio for each asset `i`
        A: M x N matrix         - Left-hand-side of constraints 
        B: M x 1 matrix(vector) - right-hand-side of constrains
        
        Return:
        cvx_data: tuple[list, matrix, matrix, matrix]
            tuple[0]: list - contents of 'vars.txt'. index of asset used in each sample
            tuple[1]: matrix - matrix of LHS of constraint. (A above docs.)
            tuple[2]: matrix - matrix of RHS of constraint. (B above docs.)  
            tuple[3]: matrix - matrix of Cost coefficient of objective. (L above docs.)
        """
        logger.info("LOAD DATA FOR CVX OPTIMIZATION.")
        
        ldata = np.array(self.get_ldata())                                # 1 x 3000
        cpreturn = np.array(self.get_matrix_cp_return())                  # 1 x 3000
        point2x0 = np.array(self.get_point_2x0())                         # 1 x 3000  
        vars = self.get_vars()                                            # 80000 x 3000
        constant = -0.0963007951203275                                    # required return
        
        if not (len(ldata) == len(cpreturn) == len(point2x0) == self.VARNUM):
            raise ValueError("data shape doesn't match.")
        
        A = np.concatenate((np.ones((1,3000)), 
                            -np.ones((1,3000)), 
                            -cpreturn.reshape(1, -1), 
                            -np.eye(3000, dtype=int), 
                            np.eye(3000, dtype=int)), axis=0)
        A = matrix(A)
        A = sparse(A) # There are many zero entries in constraint matirx A. In order to savve memory, convert matrix object to sparse matrix
        
        B = matrix([1, -1, constant, matrix(np.zeros((3000,1))), matrix(point2x0)])
        L = matrix(ldata.T)
        
        return vars, A, B, L

    def _check_paths(self):
        current_dir = self._get_current_dir()
        file_names = ['point_2x0.txt', 'matrix_cp_return.txt', 'matrix_cp_mean.txt', 'vars.txt']
        
        if not os.path.exists(os.path.join(current_dir, 'data')):
            raise FileNotExistError("data")
        
        for name in file_names:
            file_path = os.path.join(current_dir, 'data', name)
            if not os.path.exists(file_path):
                raise FileNotExistError(file_path)
            
        logger.info("CHECKING PATHS EXISTENCY PASSED.")
    
    def _set_paths(self):
        """
        set path of data files in 'data' folder like "data/point_2x0.txt"  
        """
        current_dir = self._get_current_dir()
        
        self.point_2x0_path = os.path.join(current_dir, 'data', 'point_2x0.txt')
        self.matrix_cp_return_path = os.path.join(current_dir, 'data', 'matrix_cp_return.txt')
        self.matrix_cp_mean_path = os.path.join(current_dir, 'data', 'matrix_cp_mean.txt')
        self.ldata_path = os.path.join(current_dir, 'data', 'Ldata.txt')
        self.vars_path = os.path.join(current_dir, 'data', 'vars.txt')
        
    def _get_current_dir(self):
        try:
            current_dir = os.getcwd()
        except:
            current_dir = ""
        return current_dir
    
    def get_ldata(self) -> list[float]:
        """
        Representation of Ldata is:
        
        [[-1527, -2892, ..., -1234]] 

        Try to load from Ldata.txt file.  
        if file not exists, make file using matrix_cp_mean.txt file.
        (Ldata is sum of rows of matrix_cp_mean)
        
        Return:
        ldata: list
            mean loss of each scenarios. 
            
            length of length: 3000
        """ 
        logger.info("LOADING 'Ldata.txt' ...")
        
        if self._ldata is None:    
            try:
                data = self.read_txtfile(self.ldata_path)
                self._ldata = [float(d) for d in data[0]]
                
            except FileNotFoundError:
                cpmean_data = self.get_matrix_cp_mean()
                self._ldata = np.sum(cpmean_data, axis=0)
                
                with open(self.ldata_path, 'w', encoding='utf8') as f:
                    f.write('\t'.join(map(str, self._ldata)))
        
        logger.info("'Ldata.txt' IS LOADED")
        return self._ldata

    # 좀 느린데 개선 가능성?
    def get_vars(self) -> list[list[int]]:
        """
        Representation of data is:
        
        [[1, 2, 3, 6, ..., 2997],
        [1, 2, 4, 5, ..., 2999],
        ...,
        [2, 3, 5, 6, ..., 2998]]
        
        Return:
        vars: list[list] 
            index 'i' of asset which contained in each scenarios.
        
            length of outside list (number of scenarios): 80000
            length of inside list (which to contains): n(j) (1 <= n(j) <= 3000)
        """  
        logger.info("LOADING 'vars.txt' ...")
        
        if self._vars is None:    
            data = self.read_txtfile(self.vars_path) 
            self._vars = [list(map(int, d)) for d in data]
        
        logger.info("'vars.txt' IS LOADED")
        return self._vars

    def get_matrix_cp_mean(self) -> list[list[float]]:
        """
        Representation of data is:
        
        [[x1, x2,  ..., x2999, x3000],
        [0.1, 0.2, ..., 0.001, 0.003],
        ...,
        [0.2, -0.3, ..., 0.03, 0.006]]
        
        Return:
        cpmean: list[list]
            random loss of each counterparties of contract for each scenarios
            
            length of outside list: 10000
            length of inside list: 3000
        """
        logger.info("LOADING 'matrix_cp_mean.txt' ...")
        
        if self._matrix_cpmean is None:
            data = self.read_txtfile(self.matrix_cp_mean_path)
            data = data[1:]
            
            logger.info("'matrix_cp_mean.txt' IS LOADED")
            self._matrix_cpmean = [list(map(float, d)) for d in data]
        return self._matrix_cpmean

    def get_matrix_cp_return(self) -> list[float]:
        """
        Representation of data is:
        
        [[x1, x2,  ..., x2999, x3000],
        [0.1, 0.2, ..., 0.001, 0.003]]
        
        Return:
        cpreturn: list
            required return of each assets. 
            
            length: 3000
        """
        logger.info("LOADING 'matrix_cp_return.txt' ...")
        
        if self._matrix_cpreturn is None:
            data = self.read_txtfile(self.matrix_cp_return_path)
            data = data[1]
            self._matrix_cpreturn = [float(d) for d in data]
            
        logger.info("'matrix_cp_return.txt' IS LOADED")
        return self._matrix_cpreturn
        
    def get_point_2x0(self) -> list[float]: 
        """
        Representation of data is:
        
        [['component_name', 'value'],
        ['x1', '0.000228579625661'],
        ['x2', '0.000571443369576'],
        ...
        ['x3000', '0.000676867976035']
        
        Return
        : list object containing 2x_i^j. shape: (1, 3000)
        : upper bound array of weight on asset `i`
        : weight of asset `i`: x^i, and must satisfy "0 <= x^i <= 2x_i^0 
        """
        if self._point2x0 is None:
            logger.info("LOADING 'point_2x0.txt' ...")
            data = self.read_txtfile(self.point_2x0_path)
            data = data[1:-1]
            self._point2x0 = [float(d[1]) for d in data]
            
        logger.info("'point_2x0.txt' IS LOADED")
        return self._point2x0

    @staticmethod
    def read_txtfile(path: Path|str) -> list:
        data = []
        with open(path) as f:
            l = f.readline()
            while l:
                line_data = l.strip().replace('\n', '').split("\t")
                data.append(line_data)
                l = f.readline()
                
        return data
