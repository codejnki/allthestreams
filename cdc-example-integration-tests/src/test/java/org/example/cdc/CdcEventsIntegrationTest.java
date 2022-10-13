package org.example.cdc;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.map.IMap;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class CdcEventsIntegrationTest {

    @Test
    public void shouldCaptureCdcEvents() throws SQLException, InterruptedException {
        Connection databaseConnection = connectToDatabase();

        var customer = new CustomerRecord(
                randomString(8),
                randomString(8),
                randomString(10) + "@example.com"
        );

        saveCustomerToDatabase(databaseConnection, customer);

        var id = getIdFromDatabase(customer, databaseConnection);

        databaseConnection.close();

        waitForCdcEventToBeHandled();

        var hazelcastClient = HazelcastClient.newHazelcastClient();
        IMap<Integer, Customer> map = hazelcastClient.getMap("customers");
        var customerFromHazelcast = map.get(id);

        assertThat(customerFromHazelcast).isNotNull();

        assertThat(customerFromHazelcast.firstName).isEqualTo(customer.getFirstName());
        assertThat(customerFromHazelcast.lastName).isEqualTo(customer.getLastName());
        assertThat(customerFromHazelcast.email).isEqualTo(customer.getEmailAddress());
    }

    private static void waitForCdcEventToBeHandled() throws InterruptedException {
        Thread.sleep(1000);
    }

    private static void saveCustomerToDatabase(Connection conn, CustomerRecord customer) throws SQLException {
        var insertStatement = "INSERT INTO inventory.customers (first_name, last_name, email) VALUES (?, ?, ?)";

        PreparedStatement statement = conn.prepareStatement(insertStatement);
        statement.setString(1, customer.getFirstName());
        statement.setString(2, customer.getLastName());
        statement.setString(3, customer.getEmailAddress());

        int rowsUpdated = statement.executeUpdate();
        System.out.println("Rows updated " + rowsUpdated);

        statement.close();
    }

    private static Integer getIdFromDatabase(CustomerRecord customer, Connection conn) throws SQLException {
        Statement queryStatement = conn.createStatement();

        ResultSet resultSet = queryStatement.executeQuery("SELECT id FROM inventory.customers WHERE email = '" + customer.getEmailAddress() + "'");
        resultSet.next();
        var id = resultSet.getInt(1);
        System.out.println(id);

        resultSet.close();
        queryStatement.close();

        return id;
    }

    private static Connection connectToDatabase() throws SQLException {
        String url = "jdbc:postgresql://localhost/postgres";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        return DriverManager.getConnection(url, props);
    }

    private String randomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
