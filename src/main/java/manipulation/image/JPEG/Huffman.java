package manipulation.image.JPEG;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Class which handles Huffman Table of a JPEG image
 * 
 * @author Ashley Allott
 */
public class Huffman {
	
	public short tableClass;
	public short tableID;
	public ArrayList<Pair> symbolLengths; 
	public int maxSymbolLength;
	public HuffmanTree huffmanTree;
	
	/**
	 * <p>vClass for storing a pair - a symbol and it's Huffman code
	 * @author Ashley Allott
	 */
	class Pair{
		public short symbol;
		public short length;
		public String huffmanCode;
		
		/**
		 * <p>Constructor, creates a new Pair
		 * 
		 * @param symbol	a 1 byte long symbol
		 * @param length 	the length of the Huffman code the symbol uses
		 */
		public Pair(short symbol, short length){
			this.symbol = symbol;
			this.length = length;
		}
	}
	
	/**
	 * <p>Class for handling Huffman Trees.
	 * @author Ashley Allott
	 */
	class HuffmanTree{
		public boolean set;
		public short symbol;
		public HuffmanTree left;
		public HuffmanTree right;
		public int codeLength;
		public boolean rightmost;
		
		/**
		 * <p>Constructor, creates a new empty tree.
		 */
		public HuffmanTree(){
			this.set = false;
		}
		/**
		 * <p>Constructor, creates a new empty tree with the current symbol.
		 * 
		 * @param symbol	the symbol to set the current tree node to
		 */
		public HuffmanTree(byte symbol){
			this.set = true;
			this.symbol = symbol;
		}
		/**
		 * <p>Set the symbol of the current node.
		 * 
		 * @param symbol	the symbol to be set to
		 */
		public void setSymbol(byte symbol){
			this.set = true;
			this.symbol = symbol;
		}
		/**
		 * <p>Set the left node pointer of the current node.
		 * 
		 * @param left	the HuffmanTree node to add to the left
		 */
		public void addLeft(HuffmanTree left){
			this.left = left;
		}
		/**
		 * <p>Set the right node pointer of the current node.
		 * 
		 * @param right	the HuffmanTree node to add to the right
		 */
		public void addRight(HuffmanTree right){
			this.right = right;
		}
		/**
		 * <p>Gets the depth of the current Huffman tree.
		 * 
		 * @return	integer value of the tree depth
		 */
		public int depth(){
			int depth = 0;
			if(this.left != null && this.right != null){
				depth = depth + Math.max(this.left.depth(), this.right.depth());
			}else if(this.left != null){
				depth = depth + this.left.depth();
			}else if(this.right != null){
				depth = depth + this.right.depth();
			}
			return depth + 1;
		}
	}
	
	/**
	 * <p>Constructor, creates a new Huffman using the specified parameters.
	 * 
	 * @param tableClass	the class of the Huffman table
	 * @param tableID		the id of the Huffman table
	 * @param codeLengths	a 16 long array containing the amount of codes at the index'ed length
	 * @param codeSymbols	the symbols sorted by their Huffman codes
	 */
	public Huffman(short tableClass, short tableID, short[] codeLengths, short[] codeSymbols){
		this.tableClass = tableClass;
		this.tableID = tableID;
		this.symbolLengths = new ArrayList<Pair>();
		this.maxSymbolLength = codeLengths.length+1;
		
		short ctr = 0;
		for(int i=0; i<codeLengths.length; i++){
			for(int j=0; j<codeLengths[i]; j++){
				this.symbolLengths.add(new Pair(codeSymbols[ctr], (short)(i+1)));
				ctr++;
			}
		}
		this.huffmanTree = generateCodes();
	}
	
	/**
	 * <p>Generates the JPEG specified string of the current Huffman structure for writing the JPEG file.
	 * 
	 * @return	string representation of the JPEG file
	 */
	public String genDHTString(){
		generateCodes();
		short[] codeLengths = new short[16];
		short[] codeSymbols = new short[0];
		int codeCount = 0;
		for(int i=0; i<codeLengths.length;i++){
			for(int j=0; j<symbolLengths.size(); j++){
				if(symbolLengths.get(j).length == (i+1)){
					codeLengths[i] = (short)(codeLengths[i] + 1);
					if(codeCount >= codeSymbols.length){
						codeSymbols = Arrays.copyOf(codeSymbols, codeSymbols.length+1);
					}
					codeSymbols[codeCount] = symbolLengths.get(j).symbol;
					codeCount++;
				}
			}
		}
		String huffmanString = "";
		byte tableClass = (byte)this.tableClass;
		byte tableID = (byte)this.tableID;
		byte identifierByte = (byte)((tableClass << 4) | (tableID) & 0xFF);
		huffmanString = huffmanString + Integer.toBinaryString((identifierByte & 0xFF)+ 0x100).substring(1);
		for(int i=0; i<codeLengths.length;i++){
			huffmanString = huffmanString + Integer.toBinaryString((codeLengths[i] & 0xFF)+ 0x100).substring(1);
		}
		for(int i=0; i<codeSymbols.length;i++){
			huffmanString = huffmanString + Integer.toBinaryString((codeSymbols[i] & 0xFF)+ 0x100).substring(1);
		}
		return huffmanString;
	}
	
	/**
	 * <p>Gets the symbol, if present, for the supplied Huffman code.
	 * 
	 * @param code	the Huffman code matching the required symbol
	 * @return		the symbol corresponding to the supplied code
	 * @throws Exception
	 */
	public byte getSymbol(String code) throws Exception{
		for(int i=0; i<symbolLengths.size(); i++){
			if(symbolLengths.get(i).huffmanCode.equals(code)){
				return (byte)symbolLengths.get(i).symbol;
			}
		}
		throw new Exception("Symbol not present for given code");
	}
	
	/**
	 * <p>Gets the Huffman code, if present, for the supplied symbol.
	 * 
	 * @param symbol	the symbol matching the required code
	 * @return			the code corresponding to the supplied symbol
	 * @throws Exception
	 */
	public String getCode(byte symbol) throws Exception{
		String searchSymbol = Integer.toBinaryString((symbol & 0xFF)+ 0x100).substring(1);
		for(int i=0; i<symbolLengths.size(); i++){
			String treeSymbol = Integer.toBinaryString((symbolLengths.get(i).symbol & 0xFF)+ 0x100).substring(1);
			//System.out.println("I.s: " + Integer.toBinaryString((symbolLengths.get(i).symbol & 0xFF)+ 0x100).substring(1));
			if(treeSymbol.equalsIgnoreCase(searchSymbol)){
				return symbolLengths.get(i).huffmanCode;
			}
		}
		throw new Exception("Code not present for given symbol");
	}
	
	/**
	 * <p>Constructs a Huffman tree to generate the huffman codes for the symbols.
	 * 
	 * @return	the completed Huffman tree, populated with codes and matching symbols.
	 */
	private HuffmanTree generateCodes(){			
		HuffmanTree tree = this.createTree(maxSymbolLength);
		
		for(int i=0; i<maxSymbolLength; i++){
			for(int j=0; j<symbolLengths.size(); j++){
				if(symbolLengths.get(j).length == i){
					tree = addSymbol(tree, symbolLengths.get(j).symbol, symbolLengths.get(j).length, tree.depth());
				}
			}
		}
		getCodesFromTree(tree, "");
		return tree;
		
	}
	
	/**
	 * <p>Populates the supplied HuffmanTree with Huffman codes.
	 * 
	 * @param tree			the huffman tree, populated with symbols, 
	 * 						to add huffman codes to
	 * @param huffmanCode	the current huffman code to be added to a matching symbol
	 */
	private void getCodesFromTree(HuffmanTree tree, String huffmanCode){
		if(tree != null){
			if(tree.set == true){
				for(int i=0; i<symbolLengths.size(); i++){
					if(symbolLengths.get(i).symbol == tree.symbol){
						symbolLengths.get(i).huffmanCode = huffmanCode;
					}
				}
			}else{
				getCodesFromTree(tree.left, huffmanCode + "0");
				getCodesFromTree(tree.right, huffmanCode + "1");
			}
		}
	}
	
	/**
	 * <p>Gets the Huffman code for the supplied symbol, and adds the symbol
	 * if it's not already present and there's space.
	 * 
	 * @param symbol	the symbol to get the code of
	 * @return			the Huffman code of the symbol
	 * @throws Exception
	 */
	public String addNewSymbol(byte symbol) throws Exception{
		try{
			return this.getCode(symbol);
		}catch(Exception e){}
		
		if(checkSpace()){
			int insertLength = spaceLength();
			this.symbolLengths.add(new Pair(symbol, (short)(insertLength)));
			this.huffmanTree = generateCodes();
			return this.getCode(symbol);
		}
		throw new Exception("Code not present for given symbol");
	}
	
	/**
	 * <p>Checks if there is space to add a new symbol in the Huffman tree.
	 * 
	 * @return	boolean value indicating if a new symbol can be added
	 */
	public boolean checkSpace(){
		int lengthCheck = spaceLength();
		if(lengthCheck != -1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * <p>Checks if there is space in the current Huffman table for new codes.
	 * 
	 * @return 	integer value indicating the length at which there 
	 * 			is space to add a new symbol
	 */
	public int spaceLength(){
		int maxLength = 0;
		for(int i=0; i<symbolLengths.size(); i++){
			maxLength = Math.max(maxLength, symbolLengths.get(i).length);
		}
		for(int i=maxLength; i<16; i++){
			if(checkSpaceInTree(huffmanTree, (short)i)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * <p>Checks if there is space in the tree for a symbol of a specified length.
	 * 
	 * @param tree		the HuffmanTree to check if there is space in
	 * @param length	the length to check for space for.
	 * @return			boolean value indiate if there is space in the tree
	 */
	private boolean checkSpaceInTree(HuffmanTree tree, short length){	
		if(tree == null){
			return false;
		}
		if(tree.set){
			return false;
		}
		if(tree.codeLength == length){
			if(tree.rightmost == true){
				return checkSpaceInTree(tree.left, length) | checkSpaceInTree(tree.right, length);
			}else{
				return true;
			}
		}else{
			return checkSpaceInTree(tree.left, length) | checkSpaceInTree(tree.right, length);
		}
	}
	
	/**
	 * <p>Adds the specified symbol to the Huffman tree, returning the modified tree.
	 * 
	 * @param tree			the HuffmanTree to add the symbol to
	 * @param symbol		the symbol to be added
	 * @param length		the length fo the symbol
	 * @param totalDepth	the total depth of the tree
	 * @return				the modified HuffmanTree
	 */
	public HuffmanTree addSymbol(HuffmanTree tree, short symbol, short length, int totalDepth){
		if(tree == null)return null;
		if(tree.codeLength == length){
			if(tree.set == false){
				tree.set = true;
				tree.symbol = symbol;
				tree.left = null;
				tree.right = null;
				return tree;
			}else{
				return null;
			}
		}else{
			if(tree.left != null){
				HuffmanTree leftInsert = addSymbol(tree.left, symbol, length, totalDepth);
				if(leftInsert != null){
					tree.left = leftInsert;
					return tree;
				}
			}
			if(tree.right != null){
				HuffmanTree rightInsert = addSymbol(tree.right, symbol, length, totalDepth);
				if(rightInsert != null){
					tree.right = rightInsert;
					return tree;
				}
			}
			return null;
		}
	}
	
	/**
	 * <p>Creates a new tree of the required size (depth).
	 * 
	 * @param maxSize	the depth of the tree
	 * @return			the created HuffmanTree
	 */
	private HuffmanTree createTree(int maxSize){
		HuffmanTree root = new HuffmanTree();
		root.codeLength = 0;
		
		return extendTree(root, maxSize, 0, true);
	}
	
	/**
	 * <p>Extends a given tree to the specified depth.
	 * 
	 * @param tree			the HuffmanTree to be extended
	 * @param depth			the depth required
	 * @param nodeDepth		the depth the current node is located at
	 * @param rightMost		boolean indicating if the current node is the right most node
	 * @return
	 */
	private HuffmanTree extendTree(HuffmanTree tree, int depth, int nodeDepth, boolean rightMost){
		if(tree!=null)tree.codeLength = nodeDepth;
		if(nodeDepth >= depth){
			return tree;
		}else{
			if(tree.set == false){
				if(tree.left == null){
					tree.left = new HuffmanTree();
					tree.left.rightmost = false;
					extendTree(tree.left, depth, (nodeDepth+1), false);
				}else{
					extendTree(tree.left, depth, (nodeDepth+1), false);
				}
				if(tree.right == null){
					tree.right = new HuffmanTree();
					tree.right.rightmost = rightMost;
					extendTree(tree.right, depth, (nodeDepth+1), rightMost);
				}else{
					extendTree(tree.right, depth, (nodeDepth+1), rightMost);
				}
				return tree;
			}else{
				return tree;
			}
		}
	}
	
}
