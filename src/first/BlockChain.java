package first;

import java.io.FileWriter;
import java.io.IOException;
import java.security.Security;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.gson.GsonBuilder;

public class BlockChain extends Thread {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	public static ArrayList<Wallet> wallets = new ArrayList<Wallet>();
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletAdmin;
	public static Transaction genesisTransaction;
	//Block genesis = new Block("0");

	public static void main(String[] args) throws InterruptedException, IOException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup Bouncey castle as a
																						// Security Provider
		walletAdmin = new Wallet();
		// Create wallets:
		createWallets();
		
		// do transactions
        transactions();	
        
        //isChainValid();
        
        // retreive lost bictoins
       RetreiveLostCoin();      
       System.out.println(walletAdmin.getBalance());
       isChainValid();
  
	}

	
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); // a temporary working
																									// list of unspent
						                                  																		// transactions at a
																									// given block
																									// state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		// loop through blockchain to check hashes:
		for (int i = 1; i < blockchain.size(); i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if (!currentTransaction.verifiySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).receiver != currentTransaction.receiver) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).receiver != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}

	public static void createWallets() throws IOException {
		FileWriter myWriter = new FileWriter("result1.txt");
		FileWriter myWriter2 = new FileWriter("result2.txt");
		FileWriter myWriter3 = new FileWriter("result3.txt");
		FileWriter myWriter4 = new FileWriter("result4.txt");
		FileWriter myWriter5 = new FileWriter("result5.txt");
		FileWriter myWriter6 = new FileWriter("result6.txt");
		FileWriter myWriter7 = new FileWriter("result7.txt");
		FileWriter myWriter8 = new FileWriter("result8.txt");
		Wallet coinbase = new Wallet();
		

		// generate wallets with balance between 10000 and 100000
		for (int i = 0; i < 81; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (100000 - 10000) + 10000;
			float balance = (float) Dbalance;
			// float balance = (float) 10.1;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey);
				genesisTransaction.transactionId = "0"; 
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter.close();

		// generate wallets with balance between 1000 and 10000
		for (int i = 81; i < 281; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (10000 - 1000) + 1000;
			float balance = (float) Dbalance;
			// float balance = (float) 10.1;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey); 
				genesisTransaction.transactionId = "0"; 
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId));
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter2.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter2.close();

		// generate wallets with balance between 100 and 1000
		for (int i = 281; i < 581; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (1000 - 100) + 100;
			float balance = (float) Dbalance;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey);
				genesisTransaction.transactionId = "0"; 
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter3.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter3.close();

		// generate 2000 wallets with balance between 10 and 100
		for (int i = 581; i < 781; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (100 - 10) + 10;
			float balance = (float) Dbalance;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey); 
				genesisTransaction.transactionId = "0";
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter4.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter4.close();

		// generate wallets with balance between 1 and 10
		for (int i = 781; i < 981; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (10 - 1) + 1;
			float balance = (float) Dbalance;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey);
				genesisTransaction.transactionId = "0"; 
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter5.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter5.close();

		// generate wallets with balance between 0.1 and 1
		for (int i = 981; i < 1081; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (1 - 0.1) + 0.1;
			float balance = (float) Dbalance;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey);
				genesisTransaction.transactionId = "0"; 
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter6.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter6.close();

		// generate wallets with balance between 0.1 and 0.01
		for (int i = 1081; i < 1281; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (0.1 - 0.01) + 0.01;
			float balance = (float) Dbalance;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey);
				genesisTransaction.transactionId = "0";
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter7.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter7.close();
		
		// generate wallets with balance between 0.01 and 0.001
		for (int i = 1281; i < 1481; i++) {
			wallets.add(i, new Wallet());
			Wallet w = wallets.get(i);
			Random rd = new Random();
			double Dbalance = rd.nextDouble() * (0.1 - 0.01) + 0.01;
			float balance = (float) Dbalance;
			try {
				genesisTransaction = new Transaction(coinbase.pbkey, w.pbkey, balance, null);
				genesisTransaction.generateSignature(coinbase.pvkey); 
				genesisTransaction.transactionId = "0"; 
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver,
						genesisTransaction.value, genesisTransaction.transactionId)); 
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
				myWriter8.write(
						"balance of wallet number " + i + " " + wallets.get(i).getBalance() + System.lineSeparator());
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		myWriter8.close();
	}
	
   public static void transactions() {
	   Block genesis = new Block("0");
	   addBlock(genesis);
	   Block block1 = new Block(genesis.hash);
	   addBlock(block1);
	   blockchain.add(0, genesis);
	   blockchain.add(1, block1); 
	   for (int i = 0; i < 1000; i++) {
			Random rand = new Random();
			double Dbalance = rand.nextDouble() * (100 - 10) + 10;
			float balance = (float) Dbalance;
			int randomIndex1 = rand.nextInt(wallets.size());
			int randomIndex2 = rand.nextInt(wallets.size());
			Wallet randomElement1 = wallets.get(randomIndex1);
			Wallet randomElement2 = wallets.get(randomIndex2);
			System.out.println("first wallet's balance is " + randomElement1.getBalance());
			System.out.println("first wallet's date is " + randomElement1.Updatedteddate);
			System.out.println("seconde wallet's balance is " + randomElement2.getBalance());
			System.out.println("seconde wallet's date is " + randomElement2.Updatedteddate);
			System.out.println("Wallet with adress" + " " + randomElement1 + " " + "is Attempting to send funds" + " "
					+ balance + " " + "to " + randomElement2 + "...");
			// genesis.(randomElement1.sendFunds(randomElement2.pbkey, balance));
			//Block block1 = new Block(genesis.hash);
			block1.addTransaction(randomElement1.sendFunds(randomElement2.pbkey, balance));
			randomElement1.setUpdateddate(new Date());
			randomElement2.setUpdateddate(new Date());
			System.out.println("first wallet's date is " + randomElement1.Updatedteddate);
			System.out.println("seconde wallet's date is " + randomElement2.Updatedteddate);
			System.out.println(
					"balance's of walett with address" + randomElement1 + " is " + randomElement1.getBalance());
			System.out.println(
					"balance's of walett with address" + randomElement2 + " is " + randomElement2.getBalance());
			// thread.sleep(1000);
			System.out.println("******************************");
		}
   }
   
   public static void  RetreiveLostCoin() {
	   
	   Block block2 = new Block(blockchain.get(1).hash);
	   addBlock(block2);
	   for (int i = 0; i < wallets.size(); i++) {
			Wallet w = wallets.get(i);
			if(isWalletLost(w.getBalance(), w.Updatedteddate))
				block2.addTransaction(w.sendFunds(walletAdmin.pbkey, w.getBalance()));
		}
   }

	public static Boolean isWalletLost(float balance, Date LastUsedDate) {
		double p2 = 1 - (2 / 3.14) * (Math.atan(0.1 * balance));
		Date date = new Date();
		LocalDate d1 = Instant.ofEpochMilli(LastUsedDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate d2 = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		long daysBetween = ChronoUnit.DAYS.between(d1, d2);
		double p1 = 1 / (1 + Math.exp(-(daysBetween - 1500) / 2));
		System.out.println(LastUsedDate);
		System.out.println(daysBetween);
		System.out.println(p1);
		System.out.println(balance);
		System.out.println(p2);
		double p = p1 * p2;
		System.out.println(p);
		if (p > 0.7) {
			return true;
		} else
			return false;
	}
}
