import uvicorn
from fastapi import File, UploadFile, FastAPI
import base64
from BeatNet.BeatNet import BeatNet
import struct
from utils import *
import subprocess

estimator = BeatNet(1, mode='offline', inference_model='DBN', plot=[], thread=False)

app = FastAPI()

@app.get("/ping")
async def ping():
    return {"message": "pong!"}
    
@app.post("/upload_test/")
async def file_test(file: bytes = File(...)):
    return {"file_size": len(file)}

@app.post("/predict")
async def predict(file: bytes = File(...)):
    with open("./assets/recording.wav", "wb") as f:
        f.write(file)
    p = subprocess.Popen("spleeter separate -p spleeter:2stems -o assets ./assets/recording.wav", shell=True)
    p.wait()
    dbn_pred = estimator.process("./assets/recording/accompaniment.wav")
    beats = [{"t": t, "b": b} for [t, b] in dbn_pred]
    print(beats)
    return {"beats": beats}

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)