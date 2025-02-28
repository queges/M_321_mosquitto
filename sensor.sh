#!/bin/bash
SENSOR_NAME=$1
while true; do
    VALUE=$RANDOM
    mosquitto_pub -t "$SENSOR_NAME" -m "$VALUE"
    sleep 1
done
