package hw09;

class MultiThreadedServerTask implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private AccountCache[] account_caches;
    private Account[] allAccounts;
    private String transaction;

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    public MultiThreadedServerTask(Account[] allAccounts, String trans) {
        account_caches = new AccountCache[allAccounts.length];
        this.allAccounts = allAccounts;
        transaction = trans;
    }

    // returns reference to AccountCache object
    private AccountCache parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';

        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();

        AccountCache a = account_caches[accountNum];

        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*')
                throw new InvalidTransactionError();

            accountNum = (account_caches[accountNum].peek() % numLetters);
            a = account_caches[accountNum];
        }

        return a;
    }

    private int parseAccountOrNum(String name) {
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            return Integer.parseInt(name);
        } else {
            return parseAccount(name).peek();
        }
    }

    private void load_accounts_into_cache() {
        for(int i=0; i < allAccounts.length; i++) {
            account_caches[i] = new AccountCache(allAccounts[i]);
        }
    }

    private void execute_transaction_on_cache() {
        // tokenize transaction
        String[] commands = transaction.split(";");

        for (String command : commands) {
            String[] words = command.trim().split("\\s");

            if (words.length < 3)
                throw new InvalidTransactionError();

            AccountCache lhs = parseAccount(words[0]);

            if (!words[1].equals("="))
                throw new InvalidTransactionError();

            int rhs = parseAccountOrNum(words[2]);

            for (int j = 3; j < words.length; j += 2) {
                if (words[j].equals("+"))
                    rhs += parseAccountOrNum(words[j + 1]);
                else if (words[j].equals("-"))
                    rhs -= parseAccountOrNum(words[j + 1]);
                else
                    throw new InvalidTransactionError();

            }

            lhs.update_cache(rhs);
        }
    }

    private void close_accounts() {
        for (AccountCache account_cache : account_caches) {
            account_cache.close_if_open();
        }
    }

    public void run() {
        while(true) {
            // First thing we do is load the latest values of the accounts
            // into the caches
            System.out.println("Loading accounts into cache.");
            load_accounts_into_cache();

            // now we execute the transaction on the cache
            execute_transaction_on_cache();

            // now we need to try and open the accounts the transaction needed
            try {
                for (AccountCache account_cache : account_caches) {
                    account_cache.open_if_needed();
                }
            } catch (TransactionAbortException e) {
                System.out.println("Failed to open an account");
                // One of the accounts we wanted to open is currently open
                // fail and go back to the beginning
                close_accounts();
                continue;
            }

            // now that we've opened everythign we need to we want to verify the
            // contents of the accounts are what we expected when we cached them
            try {
                for (AccountCache account_cache : account_caches) {
                    account_cache.verify();
                }
            } catch (TransactionAbortException e) {
                System.out.println("Failed to verify an account");
                // One of the accounts we wanted to open is currently open
                // fail and go back to the beginning
                close_accounts();
                continue;
            }

            // We have all the needed accounts open and they have the correct contents
            // now we update and close them
            for (int i = A; i <= Z; i++) {
//                Character c = new Character((char) (i+'A'));
//                System.out.println("Trying to update account "+c);
                AccountCache account_cache = account_caches[i];

                account_cache.update();
                account_cache.close_if_open();
            }

            // Everything worked! Print a message and break out of the while(true)
            System.out.println("commit: " + transaction);
            break;
        }


    }
}
