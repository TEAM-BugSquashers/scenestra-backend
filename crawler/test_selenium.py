# test_selenium.py
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

# 1) 옵션: 헤드리스 없이 실행
options = webdriver.ChromeOptions()
# options.add_argument("--headless")  # ← 이 줄을 주석 처리하세요
options.add_argument("--window-size=1920,1080")

# 2) 서비스: 자동으로 드라이버 설치
service = Service(
    ChromeDriverManager(driver_version="136.0.7103.94").install()
)
options = webdriver.ChromeOptions()

# 3) 드라이버 실행
driver = webdriver.Chrome(service=service, options=options)

# 4) 구글 접속 & 타이틀 출력
driver.get("https://www.google.com")
print("Page title is:", driver.title)

driver.quit()
