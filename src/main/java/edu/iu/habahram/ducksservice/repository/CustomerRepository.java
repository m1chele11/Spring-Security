package edu.iu.habahram.ducksservice.repository;
import edu.iu.habahram.ducksservice.model.Customer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CustomerRepository {
    private static final Logger LOG =
            LoggerFactory.getLogger(CustomerRepository.class);
    public CustomerRepository() {
        File file = new File(DATABASE_NAME);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static final String NEW_LINE = System.lineSeparator();
    private static final String DATABASE_NAME = "ducks/customers.txt";
    private static void appendToFile(Path path, String content)
            throws IOException {
        Files.write(path,
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }
    public void save(Customer customer) throws Exception {
        Customer c = findByUsername(customer.username());
        if(c != null) {
            throw new
                    Exception("This username already exists. " +
                    "Please choose another one.");
        }
        Path path = Paths.get(DATABASE_NAME);

        // encode the password and save to datbase
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordEncoded = encoder.encode(customer.password());
        String data = customer.username() + "," + passwordEncoded
               + "," + customer.email();
        appendToFile(path, data + NEW_LINE);
    }

    public List<Customer> findAll() throws IOException {
        List<Customer> result = new ArrayList<>();
        Path path = Paths.get(DATABASE_NAME);
        List<String> data = Files.readAllLines(path);
        for (String line : data) {
            if(!line.trim().isEmpty()) {
                String[] tokens = line.split(",");
                Customer c = new Customer(tokens[0], tokens[1], tokens[2]);
                result.add(c);
            }
        }
        return result;
    }

    public Customer findByUsername(String username) throws IOException {
        List<Customer> customers = findAll();
        for(Customer customer : customers) {
            if (customer.username().trim().equalsIgnoreCase(username.trim())) {
                return customer;
            }
        }
        return null;
    }
}
