import logging

def setup_logger():
    logger = logging.getLogger() 
    logger.setLevel(logging.DEBUG)
    logger.addHandler(get_console_handler())
    logger.addHandler(get_file_handler())
    return logger

def get_console_handler():
    console_handler = logging.StreamHandler()
    console_handler.setLevel(logging.INFO)
    formatter = CustomFormatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s', datefmt="%Y/%m/%d %H:%M:%S")
    console_handler.setFormatter(formatter)
    return console_handler

def get_file_handler():
    file_handler = logging.FileHandler("DS_teamproject.log", encoding='utf-8')
    file_handler.setLevel(logging.DEBUG)
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s', datefmt="%Y/%m/%d %H:%M:%S")
    file_handler.setFormatter(formatter)
    return file_handler


class CustomFormatter(logging.Formatter):
    RESET = "\033[0m"
    RED = "\033[31m"
    GREEN = "\033[32m"
    YELLOW = "\033[33m"
    BLUE = "\033[34m"
    CYAN = "\033[36m"
    
    # 로그 레벨별 색상 설정
    LOG_COLORS = {
        logging.DEBUG: BLUE,
        logging.INFO: GREEN,
        logging.WARNING: YELLOW,
        logging.ERROR: RED,
        logging.CRITICAL: RED,
    }

    def format(self, record):
        log_color = self.LOG_COLORS.get(record.levelno, self.RESET)
        # 메시지 앞에 색상 코드를 추가하고, 뒤에 RESET으로 원래 색상 복구
        message = super().format(record)
        return f"{log_color}{message}{self.RESET}"