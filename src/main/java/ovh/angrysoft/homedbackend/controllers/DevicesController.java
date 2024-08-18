package ovh.angrysoft.homedbackend.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ovh.angrysoft.homedbackend.configurations.MqttProperties;
import ovh.angrysoft.homedbackend.models.Device;
import ovh.angrysoft.homedbackend.services.MqttV5Connection;

@RestController
@RequestMapping("/api/v1/devices")
public class DevicesController {
    private final MqttV5Connection mqttConn;
    private SseEmitter sseEmitter;

    Logger logger = Logger.getLogger(getClass().getName());

    DevicesController(MqttV5Connection mqttV5Connection, MqttProperties props) {
        this.mqttConn = mqttV5Connection;
        this.mqttConn.setUri(props.uri());
        this.mqttConn.setUser(props.user());
        this.mqttConn.setPassword(props.password());
        this.mqttConn.setAutomaticReconnect(true);
        this.mqttConn.start();
    }

    @GetMapping()
    public Device getDevices() {
        try {
            sseEmitter.send("Registered");
        } catch (IOException e) {
            logger.log(Level.WARNING, "ERROR: from getDevices {0}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        logger.info(String.format("%s - %s", mqttConn.getTopics(), mqttConn.getSseEmitter()));
        return new Device("Halina", "Roboroc 300");
    }

    @CrossOrigin
    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        logger.info("init events");
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.sseEmitter = emitter;
        try {
            sseEmitter.send("start emitter");
        } catch (IOException e) {
            logger.warning("ERROR: from emiter " + e.getLocalizedMessage());

            // e.printStackTrace();
        }
        String topic = String.format("homed/%s/get", "e935ce0b-5c5f-47e1-9c7e-7b52afbfa96a");
        mqttConn.addTopic(topic);
        mqttConn.addSseEmiter(sseEmitter);
        return emitter;
    }

    @GetMapping("/refresh")
    public String refreshDevicesList() {
        logger.info("refreshing");
        String topic = String.format("homed/%s/set", "e935ce0b-5c5f-47e1-9c7e-7b52afbfa96a");
        try {
            mqttConn.publishMessage(
                    "{\"event\": \"request\", \"sid\": \"deviceManager\", \"payload\": {\"name\": \"devices\" \"value\": \"list\"}}"
                            .getBytes(),
                    0, false, topic);
        } catch (MqttException e) {
           logger.warning("ERROR: from refresh " + e.getLocalizedMessage());
            // e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping()
    public String sendAction(@RequestBody String action) {
        String topic = String.format("homed/%s/set", "e935ce0b-5c5f-47e1-9c7e-7b52afbfa96a");
        try {
            mqttConn.publishMessage(action.getBytes(), 0, false, topic);
        } catch (MqttException e) {
            logger.warning("ERROR: from send action " +  e.getLocalizedMessage());

            e.printStackTrace();
        }
        return "ok";
    }

    @GetMapping("/{id}")
    public Device getMethodName(@PathVariable String id) {
        return new Device("Halina", id);
    }

}
