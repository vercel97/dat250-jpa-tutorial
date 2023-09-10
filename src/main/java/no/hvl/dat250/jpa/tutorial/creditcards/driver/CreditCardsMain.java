package no.hvl.dat250.jpa.tutorial.creditcards.driver;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import no.hvl.dat250.jpa.tutorial.creditcards.*;

import java.util.HashSet;

public class CreditCardsMain {

  static final String PERSISTENCE_UNIT_NAME = "jpa-tutorial";

  public static void main(String[] args) {
    EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    EntityManager em = factory.createEntityManager();

    try {
      em.getTransaction().begin();
      clearData(em); // Fjern eksisterende data før du legger til nye
      createObjects(em);
      em.getTransaction().commit();
    } finally {
      em.close();
      factory.close();
    }
  }


  private static void createObjects(EntityManager em) {
    Address address = new Address();
    address.setStreet("Inndalsveien");
    address.setNumber(28);

    Customer customer = new Customer();
    customer.setName("Max Mustermann");

    // Assosier kunden med adressen
    customer.addAddress(address);

    // Opprett og assosier pincode med kredittkort
    Pincode pincode = new Pincode();
    pincode.setPincode("123");
    pincode.setCount(1);

    CreditCard creditCard = new CreditCard();
    creditCard.setNumber(12345);
    creditCard.setBalance(-5000);
    creditCard.setCreditLimit(-10000);
    customer.getCreditCards().add(creditCard);
    creditCard.setPincode(pincode);   // Assosier kredittkort med pincode

    CreditCard creditCard2 = new CreditCard();
    creditCard2.setNumber(123);
    creditCard2.setBalance(1);
    creditCard2.setCreditLimit(2000);
    customer.getCreditCards().add(creditCard2); // Assosier kredittkort2 med samme kunden
    creditCard2.setPincode(pincode);   // Assosier kredittkort2 med samme pincode

    Bank bank = new Bank();
    bank.setName("Pengebank");
    // Add the card to the bank's set of ownedCards
    if (bank.getOwnedCards() == null) {
      bank.setOwnedCards(new HashSet<>());
    }
    bank.getOwnedCards().add(creditCard);
    bank.getOwnedCards().add(creditCard2);

    creditCard.setBank(bank);
    creditCard2.setBank(bank);

    // Lagre objektene med riktige assosiasjoner
    em.persist(address);
    em.persist(pincode);
    em.persist(bank);
    em.persist(customer); // Lagre kunden sist for å opprettholde assosiasjoner
    em.persist(creditCard);
    em.persist(creditCard2);
  }

  private static void clearData(EntityManager em) {
    em.createQuery("DELETE FROM CreditCard").executeUpdate();
    em.createQuery("DELETE FROM Pincode").executeUpdate();
    em.createQuery("DELETE FROM Bank").executeUpdate();
    em.createQuery("DELETE FROM Customer").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
  }
}
