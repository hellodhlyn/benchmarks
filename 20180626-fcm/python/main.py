import json
import os
import time
from concurrent.futures import ThreadPoolExecutor

import firebase_admin
from firebase_admin import credentials, messaging


cred = credentials.Certificate('./service-account.json')
app = firebase_admin.initialize_app(cred)

def send(idx):
    if idx % 20 == 0:
        print(idx)

    # Deserialize json payload.
    raw = "{\"message\":\"Message Body\",\"token\":\"ExampleToken\"}"
    payload = json.loads(raw)

    message = messaging.Message(
        data={'key': payload['message']},
        token=payload['token']
    )

    # Request FCM message API.
    try:
        res = messaging.send(message, dry_run=True, app=app)
    except Exception as e:
        pass

if __name__ == '__main__':
    start = int(time.time() * 1000)

    with ThreadPoolExecutor(max_workers=8) as executor:
        for idx in range(1, 500):
            executor.submit(send, idx)

    to = int(time.time() * 1000)

    print(f"Elapsed Time : {to - start} ms.")
