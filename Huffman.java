package huffman_assignment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;


public class Huffman {

	/**
	 * 
	 * Inner class Huffman Node to Store a node of Huffman Tree
	 *
	 */
	private int cont = 0;

	private class HuffmanTreeNode<E> implements Comparable<HuffmanTreeNode<E>> {
		private int character; // character being represented by this node (applicable to leaves)
		private int count; // frequency for the subtree rooted at node
		private HuffmanTreeNode left; // left/0 subtree (NULL if empty)
		private HuffmanTreeNode right; // right/1 subtree subtree (NULL if empty)

		public HuffmanTreeNode(int c, int ct, HuffmanTreeNode leftNode, HuffmanTreeNode rightNode) {
			character = c;
			count = ct;
			left = leftNode;
			right = rightNode;
		}

		public int getChar() {
			return character;
		}

		public Integer getCount() {
			return count;
		}

		public HuffmanTreeNode getLeft() {
			return left;
		}

		public HuffmanTreeNode getRight() {
			return right;
		}

		public boolean isLeaf() {
			return left == null;
		} // since huffman tree is full; if leaf=null so must be right

		@Override
		public int compareTo(HuffmanTreeNode o) {
			// TODO Auto-generated method stub

			int freqcom = Integer.compare(getCount(), o.getCount());
			
		return freqcom;
	}
	}
	/**
	 * 
	 * Auxiliary class to write bits to an OutputStream Since files output one byte
	 * at a time, a buffer is used to group each output of 8-bits Method close
	 * should be invoked to flush half filed buckets by padding extra 0's
	 */
	private class OutBitStream {
		OutputStream out;

		int buffer;
		int buffCount;

		public OutBitStream(OutputStream output) { // associates this to an OutputStream
			out = output;
			buffer = 0;
			buffCount = 0;

		}

		public void writeBit(int i) throws IOException {
			// write one bit to Output Stream (using byte buffer)
			buffer = buffer << 1;
			buffer = buffer + i;
			buffCount++;
			cont++;
			if (buffCount == 8) {

				out.write(buffer);
				// System.out.println("buffer="+buffer);
				buffCount = 0;
				buffer = 0;
			}
		}

		public void close() throws IOException { // close output file, flushing half filled byte
			if (buffCount > 0) { // flush the remaining bits by padding 0's
				buffer = buffer << (8 - buffCount);
				out.write(buffer);

			}
			out.close();
		}

	}

	/**
	 * 
	 * Auxiliary class to read bits from a file Since we must read one byte at a
	 * time, a buffer is used to group each input of 8-bits
	 * 
	 */
	private class InBitStream {
		InputStream in;
		int buffer; // stores a byte read from input stream
		int buffCount; // number of bits already read from buffer

		public InBitStream(InputStream input) { // associates this to an input stream
			in = input;
			buffer = 0;
			buffCount = 8;
		}

		public int readBit() throws IOException { // read one bit to Output Stream (using byte buffer)
			if (buffCount == 8) { // current buffer has already been read must bring next byte
				buffCount = 0;
				buffer = in.read(); // read next byte
				if (buffer == -1) {
					return -1;
				} // indicates stream ended
			}
			int aux = 128 >> buffCount; // shifts 1000000 buffcount times so aux has a 1 is in position of bit to read
			// System.out.println("aux="+aux+"buffer="+buffer);
			buffCount++;
			if ((aux & buffer) > 0) {
				return 1;
			} // this checks whether bit buffcount of buffer is 1
			else {
				return 0;
			}

		}

	}

	/**
	 * Builds a frequency table indicating the frequency of each character/byte in
	 * the input stream
	 * 
	 * @param input is a file where to get the frequency of each character/byte
	 * @return freqTable a frequency table must be an ArrayList<Integer? such that
	 *         freqTable.get(i) = number of times character i appears in file and
	 *         such that freqTable.get(256) = 1 (adding special character
	 *         representing"end-of-file")
	 * @throws IOException indicating errors reading input stream
	 */

	private ArrayList<Integer> buildFrequencyTable(InputStream input) throws IOException {
		ArrayList<Integer> freqTable = new ArrayList<Integer>(257); // declare frequency table
		HashSet<Integer> lists = new HashSet<Integer>(257);
		for (int i = 0; i < 257; i++)
			freqTable.add(i, 0); // initialize frequency values with 0

		/************ your code comes here ************/
		//
		int j = 0;
		int c = input.read();
		do {

			if (c == -1) {
				break;
			}

			if (lists.contains(c)) {
				freqTable.set((int) c, (freqTable.get(c) + 1));
//			System.out.println(c);
			} else {
				lists.add(c);
				freqTable.set((int) c, (freqTable.get(c) + 1));

			}
			c = input.read();
			j++;

		} while (true);

		freqTable.set(256, (freqTable.get(256) + 1));
		return freqTable; // return computer frequency table
	}

	/**
	 * Create Huffman tree using the given frequency table; the method requires a
	 * heap priority queue to run in O(nlogn) where n is the characters with nonzero
	 * frequency
	 * 
	 * @param freqTable the frequency table for characters 0..255 plus 256 =
	 *                  "end-of-file" with same specs are return value of
	 *                  buildFrequencyTable
	 * @return root of the Huffman tree build by this method
	 */
	private HuffmanTreeNode buildEncodingTree(ArrayList<Integer> freqTable) {

		// creates new huffman tree using a priority queue based on the frequency at the
		// root

		/************ your code comes here ************/
		PriorityQueue<HuffmanTreeNode> trees = new PriorityQueue<HuffmanTreeNode>();
		for (int i = 0; i < 256; i++) {

			if (freqTable.get(i) > 0) {
				HuffmanTreeNode p = new HuffmanTreeNode(i, freqTable.get(i), null, null);
				trees.add(p);

			}
			
		
		}
		
		HuffmanTreeNode eof = new HuffmanTreeNode(256, 1, null, null);
		trees.add(eof);
//		assert trees.size() > 0;

		while (trees.size() > 1) {
			// two trees with least frequency

			HuffmanTreeNode a = trees.remove();
			HuffmanTreeNode b = trees.remove();


			HuffmanTreeNode parent = new HuffmanTreeNode('\0', (a.getCount() + b.getCount()), a, b);
			trees.add(parent);
		}

		return trees.remove(); // dummy return value so code compiles
	}

	/**
	 * 
	 * @param encodingTreeRoot - input parameter storing the root of the HUffman
	 *                         tree
	 * @return an ArrayList<String> of length 257 where code.get(i) returns a String
	 *         of 0-1 correspoding to each character in a Huffman tree code.get(i)
	 *         returns null if i is not a leaf of the Huffman tree
	 */
	private ArrayList<String> buildEncodingTable(HuffmanTreeNode encodingTreeRoot) {
		ArrayList<String> code = new ArrayList<String>(257);

//		code.addAll(null);
		for (int i = 0; i < 257; i++) {
			code.add(i, null);
		}
		encodTable(encodingTreeRoot, "", code);
		return code;
	}

	private void encodTable(HuffmanTreeNode root, String s, ArrayList<String> code) {
		// TODO Auto-generated method stub
//System.out.println(root.character);
		if (root.left != null && root.right!=null) {

			encodTable(root.left, (s + "0"), code);

			encodTable(root.right, (s + "1"), code);

		} else {

//			System.out.println(root.character+s);
			code.set(root.getChar(), s);

		}
	}

	/**
	 * Encodes an input using encoding Table that stores the Huffman code for each
	 * character
	 * 
	 * @param input         - input parameter, a file to be encoded using Huffman
	 *                      encoding
	 * @param encodingTable - input parameter, a table containing the Huffman code
	 *                      for each character
	 * @param output        - output paramter - file where the encoded bits will be
	 *                      written to.
	 * @throws IOException indicates I/O errors for input/output streams
	 */
	private void encodeData(InputStream input, ArrayList<String> encodingTable, OutputStream output)
			throws IOException {

		OutBitStream bitStream = new OutBitStream(output); // uses bitStream to output bit by bit

		/************ your code comes here ************/
		int i = 0;
		double out = 0;// count ouput bytes

		String ff;//encoding string
		int c = input.read();
		while(c!=-1) {
			
			
			

			
			
			ff = encodingTable.get(c);
			if (ff != null ) {
//			
		
				
				for (int j = 0; j <ff.length(); j++) {

					char h = ff.charAt(j);

					int b = -1;
					if (h == '0') {
						b = 0;
						out++;
					} 
					else if (h == '1') {
						b = 1;
						out++;
					}
				
				
//					if(b!=-1) {
//						System.out.println(b);
//						out++;
					bitStream.writeBit(b);
//					}
				}
				
			}

			c = input.read();
			i++;
			
		} 
	
		ff=encodingTable.get(256);
		for (int j = 0; j <ff.length(); j++) {

			char h = ff.charAt(j);

			int b = -1;
			if (h == '0') {
				b = 0;
			} 
			else if (h == '1') {
				b = 1;
			}
		
			
			if(b!=-1) {

				out++;
			bitStream.writeBit(b);
			}
		}

		//pad with zeros
	if(out%8!=0) {
	double pad=out/8;

	int nzero=(int) (Math.ceil(pad)*8-out);

	for(int k=0;k<nzero;k++) {
		bitStream.writeBit(0);
	}
	}
	bitStream.close();
		System.out.println("Number of bytes in input:" + (i));
		System.out.println("Number of bytes in output:" + ((int)Math.ceil(out / 8)));
		// close bit stream; flushing what is in the bit buffer to output file
	
	}

	/**
	 * Decodes an encoded input using encoding tree, writing decoded file to output
	 * 
	 * @param input            input parameter a stream where header has already
	 *                         been read from
	 * @param encodingTreeRoot input parameter contains the root of the Huffman tree
	 * @param output           output parameter where the decoded bytes will be
	 *                         written to
	 * @throws IOException indicates I/O errors for input/output streams
	 */
	private void decodeData(ObjectInputStream input, HuffmanTreeNode encodingTreeRoot, FileOutputStream output)
			throws IOException {

		InBitStream inputBitStream = new InBitStream(input); // associates a bit stream to read bits from file

		
//		System.out.println(c);
//		int c=input.read();
		int j = 0;
		int c = inputBitStream.readBit();
		HuffmanTreeNode node = encodingTreeRoot;
		int d = 0;
		int i = 0;
		String str ="";

		String decoded = "";
str+=c;
		while(true) {
			c = inputBitStream.readBit();

			
			str += c;

			if (c ==-1) {

				break;
			}
			j++;
		}

		System.out.println("\nNumber of bytes in input:" + ((str.length() / 8)));

		for (i = 0; i < str.length(); i++) {
			char b = str.charAt(i);
			
			if (!node.isLeaf()&& node.character!=256) {
				if (b == '1' ) {
					node = node.right;

				} else if (b == '0' ) {
					node = node.left;
				}
			} else if(node.isLeaf() && node.character!=256) {

					decoded += (char) node.getChar();

					node = encodingTreeRoot;
					i--;
				}
							
		}

	
		System.out.println("Number of bytes in output:" + (decoded.length()));
		output.write(decoded.getBytes());

		int out = 0;


	}

	/**
	 * Method that implements Huffman encoding on plain input into encoded output
	 * 
	 * @param input       - this is the file to be encoded (compressed)
	 * @param codedOutput - this is the Huffman encoded file corresponding to input
	 * @throws IOException indicates problems with input/output streams
	 */
	public void encode(String inputFileName, String outputFileName) throws IOException {
		System.out.println("\nEncoding " + inputFileName + " " + outputFileName);

		// prepare input and output files streams
		FileInputStream input = new FileInputStream(inputFileName);
		FileInputStream copyinput = new FileInputStream(inputFileName); // create copy to read input twice
		FileOutputStream out = new FileOutputStream(outputFileName);
		ObjectOutputStream codedOutput = new ObjectOutputStream(out); // use ObjectOutputStream to print objects to file

		ArrayList<Integer> freqTable = buildFrequencyTable(input);

		System.out.println("FrequencyTable is=" + freqTable);
		HuffmanTreeNode root = buildEncodingTree(freqTable); // build tree using frequencies
		ArrayList<String> codes = buildEncodingTable(root); // buildcodes for each character in file
		System.out.println("EncodingTable is=" + codes);
		codedOutput.writeObject(freqTable); // write header with frequency table
		encodeData(copyinput, codes, codedOutput); // write the Huffman encoding of each character in file
	}

	/**
	 * Method that implements Huffman decoding on encoded input into a plain output
	 * 
	 * @param codedInput - this is an file encoded (compressed) via the encode
	 *                   algorithm of this class
	 * @param output     - this is the output where we must write the decoded file
	 *                   (should original encoded file)
	 * @throws IOException            - indicates problems with input/output streams
	 * @throws ClassNotFoundException - handles case where the file does not contain
	 *                                correct object at header
	 */
	public void decode(String inputFileName, String outputFileName) throws IOException, ClassNotFoundException {
		System.out.println("\nDecoding " + inputFileName + " " + outputFileName);
		// prepare input and output file streams
		FileInputStream in = new FileInputStream(inputFileName);
		ObjectInputStream codedInput = new ObjectInputStream(in);
		FileOutputStream output = new FileOutputStream(outputFileName);

		ArrayList<Integer> freqTable = (ArrayList<Integer>) codedInput.readObject(); // read header with frequency table
		System.out.println("FrequencyTable is=" + freqTable);
		HuffmanTreeNode root = buildEncodingTree(freqTable);
//		System.out.println(root.right.right.character);
		decodeData(codedInput, root, output);
	}

}
