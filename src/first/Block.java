package first;

public class Block {
 
	String hash;
	String previousHash;
	String data;
	long timeStamp;
	int nonce;
	
	
	public Block(String previousHash, String data, long timeStamp) {
		super();
		this.previousHash = previousHash;
		this.data = data;
		this.timeStamp = timeStamp;
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		String calculatedhash = StringUtil.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				data 
				);
		return calculatedhash;
	}
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}

}
