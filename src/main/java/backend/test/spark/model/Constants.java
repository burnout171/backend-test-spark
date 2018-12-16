package backend.test.spark.model;

public final class Constants {

    public static final String DB_URL = "jdbc:h2:./accounts";
    public static final String INIT_SCRIPT = "INIT=RUNSCRIPT from 'classpath:db/init.sql'";

    private Constants() {
    }
}
