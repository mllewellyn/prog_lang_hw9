package hw09.test;

import hw09.*;

import java.io.*;
import java.lang.Thread.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

public class MultithreadedServerTests extends TestCase {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;
    private static Account[] accounts;
            
    protected static void dumpAccounts() {
	    // output values:
	    for (int i = A; i <= Z; i++) {
	       System.out.print("    ");
	       if (i < 10) System.out.print("0");
	       System.out.print(i + " ");
	       System.out.print(new Character((char) (i + 'A')) + ": ");
	       accounts[i].print();
	       System.out.print(" (");
	       accounts[i].printMod();
	       System.out.print(")\n");
	    }
	 }

	Account[] initialize_accounts() {
		accounts = new Account[numLetters];
		for (int i = A; i <= Z; i++) {
			accounts[i] = new Account(Z-i);
		}
		return accounts;
	}

	private void verify_results_with_single_threaded_server(String test_file) throws IOException {
		Account[] multi_accounts = initialize_accounts();
		Account[] single_accounts = initialize_accounts();
		MultithreadedServer.runServer(test_file, multi_accounts);
		SinglethreadedServer.runServer(test_file, single_accounts);

		for (int i = A; i <= Z; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs", single_accounts[i].getValue(), multi_accounts[i].getValue());
		}
	}

     @Test
	 public void testIncrement() throws IOException {
		accounts = initialize_accounts();
		MultithreadedServer.runServer("src/hw09/data/increment", accounts);
	
		// assert correct account values
		for (int i = A; i <= Z; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs", Z-i+1, accounts[i].getValue());
		}		

	 }

	@Test
	public void testRotate() throws IOException {
		verify_results_with_single_threaded_server("src/hw09/data/rotate");
	}
	 	  	 
	
}