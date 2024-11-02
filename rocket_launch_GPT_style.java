import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class RocketLaunch {

    public static void main(String[] args) {
        // Initialize a rocket with an engine and fuel
        Rocket rocket = new Rocket(new Engine(), 100);

        // Initialize launch control with the rocket
        LaunchControl launchControl = new LaunchControl(rocket);

        // Start the launch sequence
        launchControl.startCountdown();
    }
}

// Rocket class to represent the rocket's properties
class Rocket {
    private Engine engine;
    private int fuelLevel;

    public Rocket(Engine engine, int fuelLevel) {
        this.engine = engine;
        this.fuelLevel = fuelLevel;
    }

    // Check if the rocket is ready for launch
    public boolean isReadyForLaunch() {
        return engine.isIgnitionReady() && fuelLevel > 50; // Requires ignition readiness and at least 50% fuel
    }

    public void ignite() {
        engine.ignite();
    }

    public Engine getEngine() {
        return engine;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }
}

// Engine class to represent engine readiness and ignition status
class Engine {
    private boolean ignitionReady = false;

    public boolean isIgnitionReady() {
        return ignitionReady;
    }

    public void prepareForIgnition() {
        ignitionReady = true; // Set to true to simulate preparation for ignition
        DatabaseLogger.logMessage("Engine is prepared for ignition.");
    }

    public void ignite() {
        if (ignitionReady) {
            DatabaseLogger.logMessage("Engine ignited!");
        } else {
            DatabaseLogger.logMessage("Engine is not ready for ignition.");
        }
    }
}

// LaunchControl class to manage the launch sequence
class LaunchControl {
    private Rocket rocket;

    public LaunchControl(Rocket rocket) {
        this.rocket = rocket;
    }

    public void startCountdown() {
        DatabaseLogger.logMessage("Initiating rocket launch sequence...");

        // Check if the rocket is ready for launch
        if (!rocket.isReadyForLaunch()) {
            DatabaseLogger.logMessage("Rocket is not ready for launch. Aborting sequence.");
            return;
        }

        int countdown = 10;

        try {
            // Countdown loop
            for (int i = countdown; i > 0; i--) {
                DatabaseLogger.logMessage("T-minus " + i + " seconds");
                Thread.sleep(1000); // Wait for 1 second
            }

            // Ignite and launch if countdown completes
            DatabaseLogger.logMessage("Countdown complete!");
            rocket.ignite();
            DatabaseLogger.logMessage("Liftoff! The rocket has launched.");

        } catch (InterruptedException e) {
            DatabaseLogger.logMessage("Launch sequence interrupted.");
        }
    }
}

// DatabaseLogger class to handle database operations
class DatabaseLogger {
    private static final String DATABASE_URL = "jdbc:sqlite:launch_log.db";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS launch_log (" +
                                                   "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                   "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                   "message TEXT NOT NULL);";

    static {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {
            // Check and create table if it doesn't exist
            statement.execute(CREATE_TABLE_SQL);
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    // Method to log a message to the database
    public static void logMessage(String message) {
        String insertSql = "INSERT INTO launch_log (message) VALUES (?);";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setString(1, message);
            preparedStatement.executeUpdate();
            System.out.println(message); // Optional: print to console as well
        } catch (Exception e) {
            System.err.println("Failed to log message: " + e.getMessage());
        }
    }
}
