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

    public int peek() {
        return this.current_value;
    }

    public void verify() {
        try {
            this.act.verify(this.current_value);
        } catch (TransactionAbortException e) {}
    }

    public boolean open_if_needed() {
        try {
            if (isRead) {
                this.act.open(false);
            }
            if (isWritten) {
                this.act.open(true);
            }
        } catch (TransactionAbortException e1) {
            return true;
        } catch (TransactionUsageError e2) { return false; }
        return true;
    }

    public boolean close_if_open() {
        try {
            this.act.close();
        } catch (TransactionUsageError e) { return false; }
        return true;
    }

    public void update() {
        if (isWritten) {
            this.act.update(this.current_value);
        }
    }


}
