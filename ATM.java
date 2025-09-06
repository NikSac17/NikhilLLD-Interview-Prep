/*
Data Flow
- User comes with card
- Upon authentication he can perform operation
- Perform viewBalance, withdraw money, printPassBook
- Eject card
*/

import java.util.*;

enum Operation{
    ViewBalance, Withdraw, PrintPassbook
}

class User{
    String id;
    String name;
    Card card;
    BankAccount account;
}

class Card{
    String id;
    String cvv;
    String expiryDate;
    String pin;

    boolean validatePin(String pin){
        return this.pin.equals(pin);
    }
}

class BankAccount{
    String accountNo;
    double balance;
    List<String> passbook;

    synchronized void withDraw(double balance){
        if(this.balance<balance){
            System.out.println("Insufficient Balance");
            return;
        }
        if(!ATMService.getInstance().canDispenseAmount(balance)){
            System.out.println("ATM Out of cash");
            return;
        }
        this.balance-=balance;
        ATMService.getInstance().dispenseCash(balance);
        passbook.add("Witdraw"+balance);
    }

    synchronized void checkBalance(){

    }

    synchronized void deposit(double balance){

    }

    void printPassBook(){

    }
}

class ATMService{
    double balance;
    State currATMState;
    static ATMService instance;
    Map<Integer,Integer> notesInventory;

    ATMService(){
        this.balance=4500000;
        this.currATMState=new IdleState();
    }

    static ATMService getInstance(){
        if(instance==null) instance=new ATMService();
        return instance;
    }

    void setCurrATMState(State state){
        this.currATMState=state;
    }

    State getCurrATMState(){
        return this.currATMState;
    }

    boolean canDispenseAmount(double amount){

    }

    void dispenseCash(double amount){

    }

    void updateNotesInventory(int noteValue, int freq){

    }
}

interface State{
    void insertCard(User user);
    void authenticateUser(User user, String pin);
    void performOperation(User user, Operation operation, double amount);
    void ejectCard(User user);
}

class IdleState implements State{
    @Override
    public void insertCard(User user){
        ATMService.getInstance().setCurrATMState(new HasCardInsertedState());
    }

    @Override
    public void authenticateUser(User user, String pin){
        System.out.println("Enter Card First");
    }

    @Override
    public void performOperation(User user, Operation operation, double amount){
        System.out.println("Enter Card First");
    }

    @Override
    public void ejectCard(User user){
        System.out.println("Enter Card First");
    }
}

class HasCardInsertedState implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card Already Inserted");
    }

    @Override
    public void authenticateUser(User user, String pin){
        if(user.card.validatePin(pin)){
            ATMService.getInstance().setCurrATMState(new PerformOperationState());
            return;
        }
        System.out.println("Please try again...");
        return;
    }

    @Override
    public void performOperation(User user, Operation operation, double amount){
        System.out.println("Get Authenticated First");
    }

    @Override
    public void ejectCard(User user){
        ATMService.getInstance().setCurrATMState(new IdleState());
    }
}

class PerformOperationState implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card Already Inserted");
    }

    @Override
    public void authenticateUser(User user, String pin){
        System.out.println("Already Authenticated");
    }

    @Override
    public void performOperation(User user, Operation operation, double amount){
        switch (operation){
            case Withdraw:
                user.account.withDraw(amount);
                break;
            case ViewBalance:
                user.account.checkBalance();
                break;
            case PrintPassbook:
                user.account.printPassBook();
        }
    }

    @Override
    public void ejectCard(User user){
        ATMService.getInstance().setCurrATMState(new IdleState());
    }
}

class ATMFacade{
    public void insertCard(User user){
        ATMService.getInstance().getCurrATMState().insertCard(user);
    }

    public void authenticateUser(User user, String pin){
        ATMService.getInstance().getCurrATMState().authenticateUser(user,pin);
    }

    public void performOperation(User user, Operation operation, double amount){
        ATMService.getInstance().getCurrATMState().performOperation(user,operation,amount);
    }

    public void ejectCard(User user){
        ATMService.getInstance().getCurrATMState().ejectCard(user);
    }
}


public class ATM {
    public static void main(String[] args) {

    }
}
