package first;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class BlockChain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 

	public static int difficulty = 5;
	
	
	public static void main(String[] args) {
		
		
		
		Block first = new Block("aaa","bbbb",5);
		Block second = new Block(first.hash,"bbbb",6);
		Block third = new Block(second.hash,"ccc",7);
		System.out.println("first hash : " + first.calculateHash());
		System.out.println(second.hash);
		System.out.println(third.hash);
		blockchain.add(first);
		blockchain.add(second);
		blockchain.add(third);
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
		System.out.println(blockchainJson);
		blockchain.get(0).mineBlock(difficulty);
	
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(1).mineBlock(difficulty);
		
	//	blockchain.add(new Block("Hey im the third block",blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 3... ");
		blockchain.get(2).mineBlock(difficulty);	
		/*blockchain.add( new Block("bbbb","cccc",66)) ;
		//blockchain.get(0).mineBlock(difficulty);
		blockchain.add(new Block("ffff",blockchain.get(0).hash,55)) ;
		//blockchain.get(1).mineBlock(difficulty);
		blockchain.add(new Block("iiii",blockchain.get(1).hash,65)) ;
		//blockchain.get(2).mineBlock(difficulty);
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
		System.out.println(blockchainJson);
	    
	    //String firsthash = firstBlock.calculateHash(firstBlock.previousHash + firstBlock.data firstBlock.timeStamp );
        //System.out.println( firstBlock.hash);
        //System.out.println( secondBlock.hash);
        //System.out.println( thirdBlock.hash);*/
		
		
		
		
	}

	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}
}
