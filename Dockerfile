FROM tensorflow/tensorflow:latest-py3
WORKDIR /app

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get install -y locales && \
    locale-gen en_US.UTF-8 && \
    apt-get install -y git && \
    apt-get install -y -qq libfftw3-dev && \
    git clone --recursive https://github.com/CPJKU/madmom.git && \
    cd madmom && pip install -r requirements.txt && \
    python setup.py develop --user

COPY ./app /code/app