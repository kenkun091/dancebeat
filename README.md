# dancebeat

Notes on resolving Numpy and numba dependency issues with Madmom
* Install dependencies first 
  * Numpy
  * scipy
  * cython
  * mido
* Follow instructions **Install from source**
```
git clone --recursive https://github.com/CPJKU/madmom.git
```
Then
```commandline
cd madmom
git submodule update --init --remote
```
Finally
```commandline
conda install pyaudio
```

