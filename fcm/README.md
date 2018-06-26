# FCM Benchmarks
## Scenarios

  * Send fcm push messages in parallel.

## Actions

  1. Authenticate for Google API.
  2. Deserialize json payload.
  3. Request FCM message API and serialize response.

## Environment Variable

  - `FIREBASE_SERVICE_ACCOUNT`
  - `FIREBASE_PROJECT_ID`

## Results

  - Python (4 threads) : 14.896s (500 msgs) (~= 33.6tps)
  - Python (8 threads) : 99.060s (5,000 msgs) (~= **50.47tps**)
  - Python (32 threads) : 23.776s (5,000 msgs) (~= 210.3tps)
  - Go (with 8 threads) : 14.184s (5,000 msgs) (~= **352.5 tps**)
  - Kotlin (8 threads + Coroutine) : 12.092s (500 msg) (~= **41.34tps**)
  - Kotlin (8 threads + Executor) : 12.703s (500 msg) (~= 39.36tps)
