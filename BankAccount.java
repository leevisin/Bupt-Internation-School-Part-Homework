public class BankAccount{
    private int accNo;
    private String accName;
    private double balance;

    public BankAccount(int accNo, String accName){
        this.accNo = accNo;
        this.accName = accName;
        this.balance = 0.0;
    }

    public BankAccount(String accName, int accNo){
        this.accNo = accNo;
        this.accName = accName;
        this.balance = 0.0;
    }

    public int getAccountNo(){ return accNo; }

    public String getAccountName(){  return accName; }

    public double getBalance(){ return balance; }

    public void setAccountName(String accName){
        this.accName = accName;
    }

    public void deposit(double amount){
        balance = balance + amount;
    }

    public void withdraw(double amount){
        balance = balance - amount;
    }
    public String toString() {
        return  "Account number: " + accNo + "\n" 
                + "Account name: " + accName + "\n"
                + "Balance: " + balance;
    }
}