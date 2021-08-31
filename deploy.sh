#!/bin/bash
# Deploy script based on https://github.com/castorini/covidex/blob/master/scripts/deploy-prod.sh

ps aux | grep "gradlew" | awk '{print $2}' | xargs kill

DATE=$(date +"%Y_%m_%d")

echo "Compiling app..."
./gradlew build

echo "Starting server..."
nohup ./gradlew run >> $DATE.log &

echo "Waiting for server availability..."
status_code=$(curl --write-out %{http_code} --silent --output /dev/null http://localhost:$PORT/api/status)
while [ "$status_code" -ne 200 ]; do
  echo "Server not available, trying again in 10 seconds..."
  sleep 10
  status_code=$(curl --write-out %{http_code} --silent --output /dev/null http://localhost:$PORT/api/status)
done

echo "Server started successfully! Logs are available at logs/"
