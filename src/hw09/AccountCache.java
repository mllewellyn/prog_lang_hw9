package hw09;

public class AccountCache {
    Account act;
    int initial_value;
    int current_value;
    boolean isRead = false;
    boolean isWritten = false;

    public AccountCache(Account act) {
        this.act = act;
        initial_value = act.peek();
    }

    public void verify() {

    }

    public void openAsNeeded() {

    }

    public void closeAccount() {

    }

    public void update(int new_value) {
        this.current_value = new_value;
    }

    public void updateAccount() {
        this.act.update(this.current_value);
    }


}
