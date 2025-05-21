import os
import requests
from dotenv import load_dotenv
from datetime import datetime

from database import SessionLocal, engine
from models import Movie, Base
from sqlalchemy.exc import SQLAlchemyError

import chromedriver_autoinstaller
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from urllib.parse import quote_plus
import pandas as pd

# 개발 단계: 테이블 초기화
Base.metadata.drop_all(bind=engine)
Base.metadata.create_all(bind=engine)

# 환경변수 로드
load_dotenv()
API_KEY = os.getenv("KOBIS_KEY")

# KOBIS API 엔드포인트
LIST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json"
INFO_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json"
AUDI_URL = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json"

# Watcha Pedia 검색 URL
WATCHA_SEARCH_URL = "https://pedia.watcha.com/ko-KR/search?query="

#포스터 추출 selenium
def fetch_watcha_poster_selenium(title: str, year: int | None = None) -> str | None:
    """
    Selenium으로 Watcha Pedia 검색 → JS 렌더링 후 포스터 URL 추출
    """

    # # 1) 크롬드라이버.exe가 있는 절대 경로
    # driver_path = r"C:\tools\chromedriver\chromedriver.exe"
    #
    # # 2) Service 객체에 executable_path 지정
    # service = Service(executable_path=driver_path)
    #
    # # 3) ChromeOptions (필요시)
    # # options = webdriver.ChromeOptions()
    # options = Options()
    # options.add_argument("--headless")
    #
    # # 4) webdriver 실행 시 Service 사용
    # driver = webdriver.Chrome(service=service, options=options)

    # ///////////////////////////////////////////////////////////
    # 1) driver 변수를 미리 선언
    driver = None

    # 2) chromedriver 자동 설치
    chromedriver_autoinstaller.install()

    options = Options()
    options.add_argument("--headless=new")

    # 2) 반드시 실제 Chrome.exe 경로를 지정
    #    일반적으로 Windows 기본 설치 경로:
    options.binary_location = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
    #    만약 여기에 없다면, 실제 설치된 chrome.exe 경로를 찾아 넣어 주세요.
    # ///////////////////////////////////////////////////////////

    # # 1) ChromeOptions 세팅
    # options = Options()
    # options.add_argument("--headless")                # headless
    # # ** Chrome 설치 경로가 기본이 아니면, 명시적으로 지정 **
    # options.binary_location = r"C:\tools\chromedriver\chromedriver.exe"
    # # (위 경로가 다르다면 실제 설치 경로로 바꿔 주세요)
    #
    # # 2) Service 객체에 webdriver-manager 로 설치된 chromedriver 사용
    # service = Service(ChromeDriverManager().install())
    #
    # # 3) 드라이버 실행
    # driver = webdriver.Chrome(service=service, options=options)

    try:
        # 드라이버 생성
        driver = webdriver.Chrome(options=options)

        # 2) 검색 페이지 열기
        url = f"https://pedia.watcha.com/ko-KR/search?query={quote_plus(title)}"
        driver.get(url)

        # 3) 검색 결과가 로드될 때까지 대기
        wait = WebDriverWait(driver, 10)
        # ul 항목이 나타날 때까지
        wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, "ul.RoIpdTuo.j79CCmtE > li")))

        # 4) 모든 <li> 요소 가져오기
        li_elements = driver.find_elements(By.CSS_SELECTOR, "ul.RoIpdTuo.j79CCmtE > li")
        selected_li = None
        matched_year = None

        # 1️⃣ 먼저 원래 연도, 없으면 +1년으로 재시도
        for target_year in (year, year-1 if year is not None else None):
            if target_year is None:
                continue
            for li in li_elements:
                # 제목 매칭
                a = li.find_element(By.CSS_SELECTOR, "a[title]")
                if a.get_attribute("title") != title:
                    continue
                # 연도 매칭
                if str(target_year) in li.text:
                    selected_li = li
                    matched_year = target_year
                    break
            if selected_li:
                print(f"[DEBUG] matched year: {matched_year}")
                break

        # 5) 매칭된 항목이 없으면 첫 번째 요소를 fallback
        if not selected_li and li_elements:
            selected_li = li_elements[0]
            print("[DEBUG] no year match, using first li")

        # 6) 최종 selected_li 에서 포스터 추출
        img = selected_li.find_element(By.TAG_NAME, "img")
        src = img.get_attribute("src")
        return src

    except Exception as e:
        print("Selenium error:", e)
        return None
    finally:
        if driver:
            driver.quit()

# KOFIC 에서 2023~2025년 누적 관객수 xlsx 다운받아서 이용
def load_yearly_audience_from_xlsx(xlsx_path: str) -> dict[str,int]:
    """
    1) header=None 으로 전부 읽은 뒤
    2) '영화명' 컬럼명이 포함된 행을 찾아 헤더로 재설정
    3) 영화명→관객수 맵 생성
    """
    # 1) 모든 행을 데이터로 읽기
    df0 = pd.read_excel(xlsx_path, header=None, engine="openpyxl")

    # 2) '영화명' 텍스트가 포함된 행 index 찾기
    header_row = None
    for idx, row in df0.iterrows():
        # any cell contains '영화명'?
        if row.astype(str).str.contains("영화명").any():
            header_row = idx
            break
    if header_row is None:
        raise ValueError("엑셀에서 '영화명'이 포함된 헤더 행을 찾을 수 없습니다.")

    # 3) 이제 그 행을 header로 삼아 다시 읽기
    df = pd.read_excel(
        xlsx_path,
        header=header_row,
        engine="openpyxl"
    )

    # 4) 컬럼명 정리 (공백·줄바꿈 제거)
    df.columns = [str(c).strip().replace("\n","") for c in df.columns]

    # 5) '영화명' 및 '관객' 이 포함된 실제 컬럼 찾기
    name_col = next(c for c in df.columns if "영화명" in c)
    cnt_col  = next(c for c in df.columns if "관객" in c)

    # 6) 필요한 두 컬럼만 추리고, 빈 영화명 행 제거
    df = df[[name_col, cnt_col]].dropna(subset=[name_col])

    # 7) '관객수' 문자열 → 숫자만 추출 → int
    df[cnt_col] = (
        df[cnt_col].astype(str)
        .str.replace(",", "", regex=False)
        .str.extract(r"(\d+)")
        .astype(int)
    )

    # 8) 영화명.strip() 을 key, 관객수를 value 로 맵 생성
    return { str(n).strip(): v for n, v in zip(df[name_col], df[cnt_col]) }


def fetch_and_store(item_per_page: int = 100):
    session = SessionLocal()

    # ── 엑셀 경로
    project_dir = os.path.dirname(__file__)
    xlsx_path   = os.path.join(project_dir, "KOBIS_2023-2025.xlsx")
    audience_map = load_yearly_audience_from_xlsx(xlsx_path)
    print(f"[INFO] 엑셀에서 읽어온 영화 수: {len(audience_map)}")

    page = 1
    today = datetime.today().date()

    while page < 21:
        params = {
            'key':         API_KEY,
            'curPage':     page,
            'itemPerPage': item_per_page,
            'openStartDt': '2023',  # YYYY 형식의 연도만 사용
            'openEndDt':   '2025',  # YYYY 형식의 연도만 사용
        }
        resp = requests.get(LIST_URL, params=params)
        resp.raise_for_status()
        result = resp.json()

        if 'faultInfo' in result:
            print('API error:', result['faultInfo']['message'])
            break
        if 'movieListResult' not in result:
            print('Unexpected response:', result)
            break

        movies = result['movieListResult']['movieList']
        if not movies:
            break

        for item in movies:
            movie_id = item.get('movieCd')
            movie_nm = item.get('movieNm')

            movie_nm1 = item["movieNm"].strip()
            audi_acc = audience_map.get(movie_nm1)
            if audi_acc is None:
                print(f"[DEBUG] '{movie_nm1}' 엑셀 매칭 실패")
            else:
                print(f"[DEBUG] '{movie_nm1}' 매칭 관객수: {audi_acc}")

            # 개봉연도 추출
            open_dt_str = item.get('openDt') or ''
            open_year = None
            if len(open_dt_str) >= 4 and open_dt_str[:4].isdigit():
                open_year = int(open_dt_str[:4])
            try:
                fmt = '%Y-%m-%d' if '-' in open_dt_str else '%Y%m%d'
                open_dt = datetime.strptime(open_dt_str, fmt).date()
            except ValueError:
                open_dt = None
            # 개봉 후 영화만
            if not open_dt or open_dt > today:
                continue

            # 상세정보 조회
            detail = requests.get(INFO_URL, params={'key': API_KEY, 'movieCd': movie_id})
            detail.raise_for_status()
            info = detail.json().get('movieInfoResult', {}).get('movieInfo', {})

            # 장르 필터링
            genres = [g.get("genreNm", "").strip() for g in info.get("genres", [])]
            # 성인물(에로) 장르가 있으면 건너뛰기
            if any("성인물" in g for g in genres):
                print(f"성인물 제외: {movie_id} - {movie_nm} ({genres})")
                continue

            if any("멜로" in g for g in genres):
                print(f"멜로 제외: {movie_id} - {movie_nm} ({genres})")
                continue

            if any("다큐멘터리" in g for g in genres):
                print(f"다큐멘터리 제외: {movie_id} - {movie_nm} ({genres})")
                continue

            # if any("기타" in g for g in genres):
            #     print(f"기타 제외: {movie_id} - {movie_nm} ({genres})")
            #     continue

            # 장르 자체가 없으면 건너뛰기
            if not genres:
                print(f"장르 없음 제외: {movie_id} - {movie_nm}")
                continue
            # CSV 형태 문자열로 변환
            genre_str = ", ".join(genres)

            # Watcha Pedia에서 포스터 조회
            poster_url = fetch_watcha_poster_selenium(movie_nm, open_year)
            if not poster_url:
                print(f"포스터 없음 제외: {movie_nm}")
                continue
            if not poster_url and open_year is not None:
                # 년도 불일치 시 +1년으로 재검색
                retry_year = open_year - 1
                print(f"[DEBUG] '{movie_nm}' 포스터 검색 실패({open_year}), {retry_year}년도로 재시도")
                poster_url = fetch_watcha_poster_selenium(movie_nm, retry_year)
            print(f"Poster for '{movie_nm}' ({open_year} → {retry_year if not poster_url else open_year}): {poster_url or 'None'}")


            # 상영시간 파싱
            raw_tm = info.get('showTm')
            show_tm = int(raw_tm) if isinstance(raw_tm, str) and raw_tm.isdigit() else None

            # 감독 파싱
            director = ', '.join(d.get('peopleNm', '') for d in info.get('directors', []))

            # 개봉일(date)
            open_dt = None
            if open_dt_str:
                fmt = '%Y-%m-%d' if '-' in open_dt_str else '%Y%m%d'
                try:
                    open_dt = datetime.strptime(open_dt_str, fmt).date()
                except ValueError:
                    open_dt = None

            # DB 저장
            movie_obj = Movie(
                movie_id   = movie_id,
                movie_nm   = movie_nm,
                genre      = genre_str,
                audi_acc   = audi_acc,
                show_tm    = show_tm,
                director   = director,
                open_dt    = open_dt,
                poster_url = poster_url
            )
            session.merge(movie_obj)

        try:
            session.commit()
        except SQLAlchemyError as e:
            session.rollback()
            print('DB error:', e)

        print(f"페이지 {page} 저장 완료 ({len(movies)}건)")
        page += 1

    session.close()
    print("모든 영화 데이터 저장 완료!")

if __name__ == '__main__':
    fetch_and_store()
