package acme.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTest {

    static Logger logger = LoggerFactory.getLogger("test");

    public static void main(String[] args) {
        try {
            throw new RuntimeException("ahá");
        } catch (RuntimeException e) {
            logger.error("Something bad happened while fetching record from CB. Error [{}]", e.fillInStackTrace());
        }
    }
}
