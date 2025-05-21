from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Date, Float

Base = declarative_base()

class Movie(Base):
    __tablename__ = "movie7"

    movie_id  = Column(String(50), primary_key=True, nullable=False)
    movie_nm  = Column(String(255), nullable=False)
    # movie_nmEn = Column(String(255), nullable=False)
    genre     = Column(String(255))
    audi_acc = Column(Integer, nullable=True)
    show_tm   = Column(Integer)
    director  = Column(String(255))
    open_dt   = Column(Date)
    poster_url = Column(String(512))

