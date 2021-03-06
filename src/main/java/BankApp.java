import dao.impl.BankCardDaoImpl;
import domain.BankCard;
import util.DataBaseUtil;
import util.LuhnAlgorithm;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BankApp {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static BankCardDaoImpl bankCardDao;
    private BankCard card;

    public BankApp(String[] args) {
        DataBaseUtil.setURL(args);
    }

    public void run() {
        bankCardDao = new BankCardDaoImpl();
        while (true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            try {
                switch (SCANNER.nextInt()) {
                    case 1:
                        createAnAccount();
                        break;
                    case 2:
                        if (logIntoAccount()) {
                            return;
                        }
                        break;
                    case 0:
                        System.out.println("\nBye!");
                        return;
                    default:
                        System.out.println("Please enter number 0-2!");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter number 0-2!");
            }
        }
    }

    private boolean logIntoAccount() {
        System.out.println("\nEnter your card number:");
        String loginCard = SCANNER.next();
        System.out.println("Enter your PIN:");
        String loginPin = SCANNER.next();
        card = bankCardDao.get(loginCard);
        if (card == null || !card.getCardPin().equals(loginPin)) {
            System.out.println("\nWrong card number or PIN!");
            return false;
        }
        System.out.println("\nYou have successfully logged in!");
        while (true) {
            System.out.println("\n1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            try {
                switch (SCANNER.nextInt()) {
                    case 1:
                        System.out.println("\nBalance: " + card.getBalance());
                        break;
                    case 2:
                        addIncome();
                        break;
                    case 3:
                        transfer();
                        break;
                    case 4:
                        return closeAccount();
                    case 5:
                        System.out.println("\nYou have successfully logged out!");
                        return false;
                    case 0:
                        System.out.println("\nBye");
                        return true;
                    default:
                        System.out.println("\nPlease enter number 0-5");
                        return false;
                }
            } catch (InputMismatchException e) {
                System.out.println("\nPlease enter number 0-5");
            }
        }
    }

    private boolean closeAccount() {
        System.out.println("\nThe account has been closed!");
        bankCardDao.delete(card);
        return false;
    }

    private void addIncome() {
        System.out.println("\nEnter income:");
        int income = SCANNER.nextInt();
        bankCardDao.update(card, income);
        card.setBalance(card.getBalance() + income);
    }

    private void transfer() {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String transferCard = SCANNER.next();
        if (card.getCardId().equals(transferCard)) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }
        if (!LuhnAlgorithm.checkNumberValid(transferCard)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        if (bankCardDao.get(transferCard) == null) {
            System.out.println("Such a card does not exist.");
            return;
        }


        int transferMoney = SCANNER.nextInt();
        if (card.getBalance() - transferMoney < 0) {
            System.out.println("Not enough money!");
            return;
        }
        if (bankCardDao.doTransfer(card, transferCard, transferMoney)) {
            card.setBalance(card.getBalance() - transferMoney);
            System.out.println("Success!");
        } else {
            System.out.println("Something goes wrong");
        }
    }

    private void createAnAccount() {
        BankCard bankCard = new BankCard();
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(bankCard.getCardId());
        System.out.println("Your card PIN:");
        System.out.println(bankCard.getCardPin() + "\n");
        bankCardDao.save(bankCard);
    }
}


