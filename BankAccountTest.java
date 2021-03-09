public class BankAccountTest {
    public static void main(String[] args){
        BankAccount acc1 = new BankAccount(2342423, "Leevisin");
        System.out.println(acc1);
        acc1.deposit(500);
        acc1.withdraw(100);
        System.out.println(acc1);

        BankAccount acc2 = new BankAccount("Tom Will", 7458239);
        System.out.println(acc2);
        acc2.deposit(300);
        acc2.withdraw(40);
        System.out.println(acc2);

    }
}
