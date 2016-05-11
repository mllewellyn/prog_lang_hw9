package hw09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;

// TO DO: Task is currently an ordinary class.
// You will need to modify it to make it a task,
// so it can be given to an Executor thread pool.
//
class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private String transaction;

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        transaction = trans;
    }
    
    // TO DO: parseAccount currently returns a reference to an account.
    // You probably want to change it to return a reference to an
    // account *cache* instead.
    //
    private Account parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';

        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();

        Account a = accounts[accountNum];

        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*')
                throw new InvalidTransactionError();

            accountNum = (accounts[accountNum].peek() % numLetters);
            a = accounts[accountNum];
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

    public void run() {
        // tokenize transaction
        String[] commands = transaction.split(";");

        for (String command : commands) {
            String[] words = command.trim().split("\\s");

            if (words.length < 3)
                throw new InvalidTransactionError();

            Account lhs = parseAccount(words[0]);

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

            try {
                lhs.open(true);
            } catch (TransactionAbortException e) {
                // won't happen in sequential version
            }

            lhs.update(rhs);
            lhs.close();
        }
        System.out.println("commit: " + transaction);
    }
}

public class MultithreadedServer {

	// requires: accounts != null && accounts[i] != null (i.e., accounts are properly initialized)
	// modifies: accounts
	// effects: accounts change according to transactions in inputFile
    public static void runServer(String inputFile, Account accounts[])
        throws IOException {

        // read transactions from input file
        String line;
        BufferedReader input =
            new BufferedReader(new FileReader(inputFile));

        int numThreads = 4;
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);

        while ((line = input.readLine()) != null) {
            Task t = new Task(accounts, line);

            exec.execute(t);

            //t.run();
        }

        exec.shutdown();
        try {
            exec.awaitTermination(60,TimeUnit.SECONDS);
        } catch(InterruptedException e) {}
        
        input.close();
    }
}
