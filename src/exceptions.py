class FileNotExistError(FileNotFoundError):
    def __init__(self, filename): self.filename = filename
    def __str__(self): 
        return f"""{self.filename} is not found. directory must be following tree
                data/
                | - Ldata.txt
                | - matrix_cp_mean.txt
                | - matrix_cp_return.txt
                | - point_2x0.txt
                | - vars.txt
                """
