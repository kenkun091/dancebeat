import uvicorn
from fastapi import File, UploadFile, FastAPI
import base64
from BeatNet.BeatNet import BeatNet
import struct
from utils import *

estimator = BeatNet(1, mode='online', inference_model='DBN', plot=[], thread=False)

app = FastAPI()

@app.post("/hello")
async def hello():
    return {"msg": "hello!"}
    
@app.post("/files/")
async def create_file(file: bytes = File(...)):
    return {"file_size": len(file)}

@app.post("/upload")
async def upload(file: UploadFile = File(...)):
    try:
        contents = await file.read()
        with open(file.filename, 'wb') as f:
            f.write(contents)
        packedData = map(lambda v: struct.pack('h', v), contents)
        frames = b''.join(packedData)
        output_wave('./assets/example.wav', frames)


    except Exception:
        return {"message": "There was an error uploading the file"}
    finally:
        await file.close()

    # return dbn_pred
@app.get("/predict")
async def predict():
    try:
        dbn_pred = await estimator.process("./assets/example.wav")
        return dbn_pred
    except Exception:
        return {"message":"Check input file"}


if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)