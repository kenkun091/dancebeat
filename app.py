import uvicorn
from fastapi import File, UploadFile, FastAPI
import base64
from BeatNet.BeatNet import BeatNet


estimator = BeatNet(1, mode='online', inference_model='DBN', plot=[], thread=False)

app = FastAPI()


@app.post("/files/")
async def create_file(file: bytes = File(...)):
    return {"file_size": len(file)}

@app.post("/upload")
async def upload(file: UploadFile = File(...)):
    try:
        contents = await file.read()
        with open(file.filename, 'wb') as f:
            f.write(contents)
        dbn_pred = await estimator.process(file.filename)

    except Exception:
        return {"message": "There was an error uploading the file"}
    finally:
        await file.close()

    return dbn_pred


if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)