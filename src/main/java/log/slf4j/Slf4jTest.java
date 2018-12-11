package log.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jTest {

    public static void main(String[] args) {


        Logger logger = LoggerFactory.getLogger(Slf4jTest.class);

        logger.info("#Hello");

    }
}
