//Hibernate XML
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/yourdb</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">password</property>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL5Dialect</property>
        <property name="show_sql">true</property>
        <mapping class="com.example.Student"/>
    </session-factory>
</hibernate-configuration>



//Student(Entity)
package com.example;

import jakarta.persistence.*;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int age;

    // Getters and Setters
}



//StudentDAO
package com.example;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public class StudentDAO {
    private SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void addStudent(Student s) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(s);
            tx.commit();
        }
    }

    public Student getStudent(int id) {
        try (Session session = factory.openSession()) {
            return session.get(Student.class, id);
        }
    }

    public void updateStudent(Student s) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(s);
            tx.commit();
        }
    }

    public void deleteStudent(int id) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Student s = session.get(Student.class, id);
            if (s != null) session.delete(s);
            tx.commit();
        }
    }
}



//Test App
package com.example;

public class TestApp {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();

        Student s = new Student();
        s.setName("John");
        s.setAge(22);
        dao.addStudent(s);

        Student fetched = dao.getStudent(1);
        System.out.println("Fetched: " + fetched.getName());

        fetched.setAge(23);
        dao.updateStudent(fetched);

        dao.deleteStudent(1);
    }
}
