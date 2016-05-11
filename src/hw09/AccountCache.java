package hw09;

public class AccountCache {
    Account act;
    int initial_value;
    int current_value;
    boolean isRead = false;
    boolean isWritten = false;
    boolean isOpen = false;

    public AccountCache(Account act) {
        this.act = act;
        this.initial_value = act.peek();
        this.current_value = this.initial_value;
    }

    // get the current cache account value
    public int peek() {
        isRead = true;
        return this.current_value;
    }

    // verify the account with the current value
    public void verify() throws TransactionAbortException {
        if (isRead) {
            act.verify(initial_value);
        }
    }

    // open account to read or write as necessary
    public void open_if_needed() throws TransactionAbortException {
        try {
            if (isRead) {
                act.open(false);
                isOpen = true;
            }
            if (isWritten) {
                act.open(true);
                isOpen = true;
            }
        } catch (TransactionUsageError e) {
            System.out.println("Got a TransactionUsageError at open_if_needed");
        }
    }

    // close account if open
    public void close_if_open() {
        if(isOpen)
            try {
                act.close();
            } catch (TransactionUsageError e) {
                System.out.println("Got a TransactionUsageError at close_if_open");
            }
    }

    // update cache value
    public void update_cache(int updated_value) {
        isWritten = true;
        this.current_value = updated_value;
    }

    // update account to new value if writing is required
    public void update() {
        if (isWritten) {
            this.act.update(this.current_value);
        }
    }

}
