package ovh.angrysoft.homedbackend.configurations;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MqttPropertiesTest {

    @Test
    void checkMqttProps(@Autowired MqttProperties props) {
        System.out.println(props);
        assertNotNull(props.uri());
        assertNotNull(props.user());
        assertNotNull(props.password());
    }
}
