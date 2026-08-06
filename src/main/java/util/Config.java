package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();

    static {

        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            properties.load(input);

        } catch (IOException e) {

            throw new RuntimeException(e);

        }
    }

    public static String get(String chave) {

        return properties.getProperty(chave);

    }
}