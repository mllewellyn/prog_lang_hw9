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

    // get the current cache account value
    public int peek() {
        return this.current_value;
    }

    // verify the account with the current value
    public void verify() throws TransactionAbortException {
        if (isRead) {
            this.act.verify(this.current_value);
        }
    }

    // open account to read or write as necessary
    public boolean open_if_needed() throws TransactionAbortException {
        try {
            if (isRead) {
                this.act.open(false);
            }
            if (isWritten) {
                this.act.open(true);
            }
        } catch (TransactionUsageError e) { return false; }
        return true;
    }

    // close account if open
    public boolean close_if_open() {
        try {
            this.act.close();
        } catch (TransactionUsageError e) { return false; }
        return true;
    }

    // update account to new value if writing is required
    public void update() {
        if (isWritten) {
            this.act.update(this.current_value);
        }
    }

    public void update_cache(int updated_value) {
        isWritten = true;
        this.current_value = updated_value;
    }
}
