package first;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Wallet {
	
	public PrivateKey pvkey;
	public PublicKey pbkey;
	//public Date Createddate; 
	public Date Updatedteddate; 
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //only UTXOs owned by this wallet.
		
	
	public Wallet(){
		
		Date now = new Date();
	    long sixMonthsAgo = (now.getTime() - 186624000000l);
	    long today = now.getTime();
	    long ms = ThreadLocalRandom.current().nextLong(sixMonthsAgo, today);
        Updatedteddate = new Date(ms);
		
		generateKeyPair();
		
		
	}
		
	public void setUpdateddate(Date date) {
		this.Updatedteddate = date;
	}
	
	public void generateKeyPair() {
		try {
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
	        	KeyPair keyPair = keyGen.generateKeyPair();
	        	// Set the public and private keys from the keyPair
	        	pvkey = keyPair.getPrivate();
	        	pbkey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//returns balance and stores the UTXO's owned by this wallet in this.UTXOs
		public float getBalance() {
			float total = 0;	
	        for (Map.Entry<String, TransactionOutput> item: BlockChain.UTXOs.entrySet()){
	        	TransactionOutput UTXO = item.getValue();
	            if(UTXO.isMine(pbkey)) { //if output belongs to me ( if coins belong to me )
	            	UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
	            	total += UTXO.value ; 
	            }
	        }  
			return total;
		}
		
		
		//Generates and returns a new transaction from this wallet.
		public Transaction sendFunds(PublicKey _recipient,float value ) {
			if(getBalance() < value) { //gather balance and check funds.
				System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
				return null;
			}
	
			
			//create array list of inputs
			ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	    
			float total = 0;
			for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
				TransactionOutput UTXO = item.getValue();
				total += UTXO.value;
				inputs.add(new TransactionInput(UTXO.id));
				if(total > value) break;
			}
			
			Transaction newTransaction = new Transaction(pbkey, _recipient , value, inputs);
			newTransaction.generateSignature(pvkey);
			
			for(TransactionInput input: inputs){
				UTXOs.remove(input.transactionOutputId);
			}
			return newTransaction;
		}

}
