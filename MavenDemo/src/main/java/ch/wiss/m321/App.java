package ch.wiss.m321;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;

public class App {
    public static void main(String[] args) {
        String TOPIC = args[0];
        System.out.println("Hello Bledion!");

        try {
          MqttClient client = new MqttClient("tcp://mosquitto:1883", UUID.randomUUID().toString());
          double inkrement = 0;

          while (true){
            client.connect();
            Double value = Math.sin(inkrement);
            MqttMessage message = new MqttMessage(String.valueOf(value).getBytes());
            inkrement = (inkrement + 0.1) % (2 * Math.PI);
            client.publish(TOPIC, message);
            Thread.sleep(500);
            client.disconnect();          

          }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
