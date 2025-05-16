package com.metropol.credit.repositories;

import com.metropol.credit.models.entities.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByLastNameContainingIgnoreCase(String lastNameFragment);

    @Query("SELECT c FROM Customer c WHERE c.smeName IS NOT NULL AND c.smeName <> ''")
    List<Customer> findAllSmeCustomers();

    @Query("SELECT DISTINCT la.customer FROM LoanApplication la")
    List<Customer> findCustomersWithLoanApplications(Customer customer);

    @Query(value = "SELECT * FROM customers WHERE phone_number = :phoneNumber", nativeQuery = true)
    Optional<Customer> findByPhoneNumberNative(@Param("phoneNumber") String phoneNumber);

}