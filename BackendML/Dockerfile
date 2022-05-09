FROM pytorch/pytorch:latest

WORKDIR /app

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y locales && locale-gen en_US.UTF-8
RUN apt-get install -y git
RUN apt-get install -y -qq libfftw3-dev
RUN git clone --recursive https://github.com/CPJKU/madmom.git
RUN cd madmom && pip install -r requirements.txt && python setup.py develop --user
COPY ./app /code/app
COPY ./requirements.txt /code/app/requirements.txt
RUN pip install -r /code/app/requirements.txt

EXPOSE 8000

CMD ["python", "/code/app/main.py"]

