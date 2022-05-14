FROM python:3.9-bullseye

WORKDIR /usr/src/app

RUN apt update && apt upgrade -y
RUN apt install -y ffmpeg
RUN apt install -y libsndfile1
RUN apt install -y portaudio19-dev

COPY prerequirements.txt ./
RUN pip install --no-cache-dir -r prerequirements.txt

COPY MLrequirements.txt ./
RUN pip install --no-cache-dir -r MLrequirements.txt
#RUN python3 -m pipx ensurepath
ENV PATH=/root/.local/bin:$PATH
RUN pipx install spleeter==2.3.0

COPY APIrequirements.txt ./
RUN pip install --no-cache-dir -r APIrequirements.txt

COPY ./app ./
COPY ./assets ./assets/
RUN spleeter separate -p spleeter:2stems -o assets ./assets/recording.wav 

EXPOSE 8000

CMD ["python", "/usr/src/app/main.py"]