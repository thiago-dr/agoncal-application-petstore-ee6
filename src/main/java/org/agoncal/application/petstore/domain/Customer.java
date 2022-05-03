package org.agoncal.application.petstore.domain;

import org.agoncal.application.petstore.constraint.Email;
import org.agoncal.application.petstore.constraint.Login;
import org.agoncal.application.petstore.exception.ValidationException;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_BY_LOGIN, query = "SELECT c FROM Customer c WHERE c.login = :login"),
        @NamedQuery(name = Customer.FIND_BY_LOGIN_PASSWORD, query = "SELECT c FROM Customer c WHERE c.login = :login AND c.password = :password"),
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c")
})
@XmlRootElement
@Getter
@Setter
public class Customer implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false, length = 10)
    @Login
    private String login;
    @Column(nullable = false, length = 10)
    @NotNull
    @Size(min = 1, max = 10)
    private String password;
    @Column(nullable = false)
    @NotNull
    @Size(min = 2, max = 50)
    private String firstname;
    @Column(nullable = false)
    @NotNull
    @Size(min = 2, max = 50)
    private String lastname;
    private String telephone;
    @Email
    private String email;
    @Embedded
    @Valid
    private Address homeAddress = new Address();
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Transient
    private Integer age;

    // ======================================
    // =             Constants              =
    // ======================================

    public static final String FIND_BY_LOGIN = "Customer.findByLogin";
    public static final String FIND_BY_LOGIN_PASSWORD = "Customer.findByLoginAndPassword";
    public static final String FIND_ALL = "Customer.findAll";

    // ======================================
    // =            Constructors            =
    // ======================================

    public Customer() {
    }

    public Customer(String firstname, String lastname, String login, String password, String email, Address address) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.login = login;
        this.password = password;
        this.email = email;
        this.homeAddress = address;
        this.dateOfBirth = new Date();
    }

    // ======================================
    // =          Lifecycle Methods         =
    // ======================================

    /**
     * This method calculates the age of the customer
     */
    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateAge() {
        if (dateOfBirth == null) {
            age = null;
            return;
        }

        Calendar birth = new GregorianCalendar();
        birth.setTime(dateOfBirth);
        Calendar now = new GregorianCalendar();
        now.setTime(new Date());
        int adjust = 0;
        if (now.get(Calendar.DAY_OF_YEAR) - birth.get(Calendar.DAY_OF_YEAR) < 0) {
            adjust = -1;
        }
        age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + adjust;
    }

    // ======================================
    // =              Public Methods        =
    // ======================================

    /**
     * Given a password, this method then checks if it matches the user
     *
     * @param pwd Password
     * @throws ValidationException thrown if the password is empty or different than the one
     *                             store in database
     */
    public void matchPassword(String pwd) {
        if (pwd == null || "".equals(pwd))
            throw new ValidationException("Invalid password");

        // The password entered by the customer is not the same stored in database
        if (!pwd.equals(password))
            throw new ValidationException("Passwords don't match");
    }

    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;

        Customer customer = (Customer) o;

        if (!login.equals(customer.login)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Customer");
        sb.append("{id=").append(id);
        sb.append(", login='").append(login).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", telephone='").append(telephone).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", homeAddress=").append(homeAddress);
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}
