//Account (Entity)
package com.bank.model;

import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    private int id;
    private String name;
    private double balance;

    // Getters and Setters
}



//Transaction (Entity)
package com.bank.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int fromAccount;
    private int toAccount;
    private double amount;
    private Date date = new Date();

    // Getters and Setters
}



//Bank Service
package com.bank.service;

import com.bank.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;

@Service
public class BankService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void transferMoney(int fromId, int toId, double amount) {
        Account from = em.find(Account.class, fromId);
        Account to = em.find(Account.class, toId);

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds!");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        Transaction tx = new Transaction();
        tx.setFromAccount(fromId);
        tx.setToAccount(toId);
        tx.setAmount(amount);
        em.persist(tx);
    }
}



//Spring Configuration
@Configuration
@EnableTransactionManagement
@ComponentScan("com.bank")
public class AppConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("com.bank.model");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return emf;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/bank");
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}



//Test Transfer
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.bank.service.BankService;

public class MainApp {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        BankService service = context.getBean(BankService.class);

        try {
            service.transferMoney(1, 2, 500);
            System.out.println("Transaction successful.");
        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }
}
