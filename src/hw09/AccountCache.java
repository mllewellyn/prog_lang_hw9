package hw09;

/**
 * Created by mllewellyn on 5/5/16.
 */
public class AccountCache {
    Account act;
    int initial_value;

    public AccountCache(Account act) {
        this.act = act;
        initial_value = act.peek();
    }




}
