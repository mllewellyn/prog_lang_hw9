package hw09;

public class AccountCache {
    Account act;
    int initial_value;
    int current_value;
    boolean isRead = false;
    boolean isWritten = false;

    public AccountCache(Account act) {
        this.act = act;
        this.initial_value = act.peek();
        this.current_value = this.initial_value;
    }

    public int getVal() {
        return this.current_value;
    }

    public void verify() {
        //this.act.verify(this.current_value);
    }

    public boolean open_if_needed() {
        try {
            if (isRead) {
                //open for reading
            }
            if (isWritten) {
                //open for writing
            }
        } catch (TransactionUsageError e) {
            return false;
        }
        return true;
    }

    public void close() {

    }

    public void update(int new_value) {
        isWritten = true;
        this.current_value = new_value;
    }

    public void updateAccount() {
        this.act.update(this.current_value);
    }


}
