/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileNotFoundException;
import static java.lang.Math.ceil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;
import java.util.Scanner;
/**
 *
 * @author ashu9
 */
public class treesearch {

    static double m; //Degree of Tree
    static BranchNode bHead; //Head node of Tree    
    static Stack s; //Stack to keep track of parent nodes

    static class DataNode { //Doubly linked leaf node that will contain the data

        List<ArrayList<String>> data; //A List of Lists of Strings to store data		
        List<Double> keys;			  //A List of Double to store keys
        ArrayList<String> datatemp;	  //A temporary list which will be added to data
        DataNode left;				  //Pointer to the data node which is at the left of the current node
        DataNode right;				  //Pointer to the data node which is at the right of the current node

        DataNode() { //Constructor
            data = new ArrayList<>();
            keys = new ArrayList<>();
        }

        String search(double key) { //Returns the String value associated with the given key
            if (keys.contains(key)) {
                StringBuilder str = new StringBuilder(); //Stores all the values associated with the key seperated by commas
                for (int i = 0; i < data.get(keys.indexOf(key)).size(); i++) {
                    str.append(data.get(keys.indexOf(key)).get(i));
                    str.append(", ");
                }
                str.delete(str.length() - 2, str.length() - 1);
                return str.toString();
            } else {
                return "Null";
            }
        }

        StringBuilder search(double startKey, double endKey) { //Returns the key and value pairs for every key including and between startKey and endKey
            int i = 0;
            StringBuilder str = new StringBuilder(); //Stores all the key value pairs
			if(keys.get(0) > endKey)
				return str;
            while (i < keys.size() && keys.get(i) < startKey) {
                i++;
            }
            while (i < keys.size() && keys.get(i) < endKey) {
                for (int j = 0; j < data.get(i).size(); j++) {
                    str.append("(" + keys.get(i) + "," + data.get(i).get(j) + "), ");
                }
                i++;
            }
            if (this.right == null) {
                return str;
            } else {
                return str.append(this.right.search(startKey, endKey));
            }
        }

        void insert(double key, String value) { //Inserts a particular key and value pair in the tree
            keys.add(key);
            Collections.sort(keys);
            int i = keys.indexOf(key);
            int t2 = keys.lastIndexOf(key);
            datatemp = new ArrayList<>();
            datatemp.add(value);
            if (i != t2) {					//True if there is a duplicate key present in the node
                data.get(i).add(value);
                keys.remove(t2);
            } else {
                data.add(i, datatemp);
            }
            if (keys.size() == m) {		  //Checks whether the node should be split or not
                DataNode temp = new DataNode(); //Make a new datanode
                temp.keys = new ArrayList<>(keys.subList((int) ceil(m / 2)-1, keys.size())); //Split the original node and add appropriate keys and values
                temp.data = new ArrayList<>(data.subList((int) ceil(m / 2)-1, data.size()));
                keys = keys.subList(0, (int) ceil(m / 2)-1);
                data = data.subList(0, (int) ceil(m / 2)-1);
                if (this.right != null) { //Making a double linked list between the data nodes
                    this.right.left = temp;
                    temp.right = this.right;
                }
                this.right = temp;
                temp.left = this;
                Node btemp;
                if (s.isEmpty()) { //Check whether the data nodes have a parent node
                    BranchNode parent = new BranchNode(temp.keys.get(0)); //Make a new Branch node
                    parent.dchilds.add(this);
                    parent.dchilds.add(temp);
                    bHead = parent;
                } else {
                    btemp = s.pop(); //Get the parent node of the current data node
                    int index = btemp.index;
                    btemp.value.keys.add(index, temp.keys.get(0)); //Add the lowest value of the newly created datanode in the keylist of the parent
                    btemp.value.dchilds.add(index + 1, temp); //Add the new datanode to the parent at its appropriate position
                    btemp.value.split(btemp); //Check whether the parent node must split 
                }
            }
        }
    }

    static class BranchNode { //Branch nodes that will just store the key values

        List<DataNode> dchilds; //List of data node childs
        List<Double> keys;	//List of keys
        List<BranchNode> bchilds; //List of branch node childs

        BranchNode() { //Constructor
            dchilds = new ArrayList<>();
            bchilds = new ArrayList<>();
            keys = new ArrayList<>();
        }
		
		BranchNode(double v) { //Constructor
            dchilds = new ArrayList<>();
            bchilds = new ArrayList<>();
            keys = new ArrayList<>();
            keys.add(v);
        }

        void insert(double key, String data) { //Identifies and calls the proper data node where the new key value pair has to be inserted
            int i;
            for (i = 0; i < keys.size(); i++) {
                if (key < keys.get(i)) {
                    break;
                }
            }
			s.push(this, i);	//Stores the current node in the stack along with the index where the new value will be inserted if required
            if (dchilds.isEmpty()) {                
                bchilds.get(i).insert(key, data);
            } else {                
                dchilds.get(i).insert(key, data);
            }
        }

        void split(Node b) {	//Splits a Branch node if required
            if (b.value.keys.size() == m) { //Checks whether a split is required
                Node temp;	// The parent node along with the index where the new child nodes will be inserted
                int index;	// index where the child nodes will be inserted in the parent
                boolean flag = false; //Indicates whether or not there is going to be a new root node
                if (s.isEmpty()) {
                    temp = new Node();
                    flag = true;
                } else {
                    temp = s.pop();
                }
                index = temp.index;
                temp.value.keys.add(index, b.value.keys.get((int) ceil(m / 2)-1));
                BranchNode bn = new BranchNode(); //Creates a new BranchNode
                bn.keys = new ArrayList<Double>(b.value.keys.subList((int) ceil(m / 2), (int) m));	//Splits the branchnode contained in Node b
                if (b.value.dchilds.size()==0) {
                    bn.bchilds = new ArrayList<BranchNode>(b.value.bchilds.subList((int) ceil(m / 2), b.value.bchilds.size()));
                    b.value.bchilds = b.value.bchilds.subList(0, (int) ceil(m / 2));
                } else {
                    bn.dchilds = new ArrayList<DataNode>(b.value.dchilds.subList((int) ceil(m / 2), b.value.dchilds.size()));
                    b.value.dchilds = b.value.dchilds.subList(0, (int) ceil(m / 2));
                }
                b.value.keys = b.value.keys.subList(0, (int) ceil(m / 2)-1);
                try{
                temp.value.bchilds.add(index+1, bn);	//Adds the newly created branchnode to the parent
                }catch(IndexOutOfBoundsException e){
                    temp.value.bchilds.add(bn);
                }            
                if(flag){	//Adds the branch node in b as the child of the parent node if a new root is created
                    try{
                        temp.value.bchilds.add(index, b.value);
                    }catch(IndexOutOfBoundsException e){
                        temp.value.bchilds.add(temp.value.bchilds.size()-1,b.value);
                    }
                        bHead = temp.value;
                }
                split(temp);
            }
        }

        String search(double key) {	//Search Function to call the appropriate data node and find the value associated with key
            int i;
            for (i = 0; i < keys.size(); i++) {
                if (key < keys.get(i)) {
                    break;
                }
            }
            if (dchilds.isEmpty()) {
                s.push(this, i);
                return bchilds.get(i).search(key);
            } else {
                s.push(this, i);
                return dchilds.get(i).search(key);
            }
        }

        StringBuilder search(double startKey, double endKey) { //Search Function to call the appropriate data node and find the value associated with all the keys between startKey and endKey
            int i;
            for (i = 0; i < keys.size(); i++) {
                if (startKey < keys.get(i)) {
                    break;
                }
            }
            if (dchilds.isEmpty()) {
                s.push(this, i);
                return bchilds.get(i).search(startKey, endKey);
            } else {
                s.push(this, i);
                return dchilds.get(i).search(startKey, endKey);
            }
        }
    }

    public static class Node {	//Stores the Branchnode and index in the Stack

        BranchNode value;
        int index;
        Node next;

        Node() {
            value = new BranchNode();
            next = null;
            index = 0;
        }

        Node(BranchNode b, int i) {
            value = b;
            index = i;
            next = null;
        }
    }

    static class Stack { //Stores the parent nodes

        Node top;

        Stack() { //Constructor
            top = new Node();
            top.value = null;
            top.next = null;
        }

        void push(BranchNode b, int i) { //Pushes a new Node in the stack
            Node current = top;
            top = new Node(b, i);
            top.next = current;
        }

        Node pop() { //Return the Node present at the top of the stack
            Node value = top;
            top = top.next;
            return value;
        }

        boolean isEmpty() { //Returns true if stack is empty
            if (top.value == null) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException { //EntryPoint                
        DataNode head = new DataNode(); //Stores the first data node position
        File file = new File("input.txt"); //Stores the input file
        File outputFile = new File("output_file.txt"); //Creates an output file
        FileWriter fw = new FileWriter(outputFile); //FileWriter for the output file
        PrintWriter pw = new PrintWriter(fw); //PrintWriter for the output file   
        try {
            Scanner sn = new Scanner(file);
            initialize(sn.nextInt()); //Assign the order of the tree
            if (sn.hasNextLine()) {
                sn.nextLine();
            }            
            while (sn.hasNextLine()) {
                String str = sn.nextLine(); //Stores one line of the input file
                String params = str.substring(7, str.length() - 1); //Stores the parameters                
                if (str.charAt(0) == 'I' || str.charAt(0)== 'i') {
                    // runInsert
                    s = new Stack();
                    String p[] = params.split(",");

                    double key = Double.parseDouble(p[0]);
                    String value = p[1];
                    if(bHead == null) //Check whether the root is a branch node
                        head.insert(key, value);
                    else
                        bHead.insert(key, value);
                    
                } else {

                    // runSearch
                    if (params.contains(",")) { //Check whether a range search is to be performed
                        String p[] = params.split(",");
                        double startKey = Double.parseDouble(p[0]);
                        double endKey = Double.parseDouble(p[1]);
						if(startKey > endKey){
							double tempKey = startKey;
							startKey = endKey;
							endKey = tempKey;
						}
                        StringBuilder sb; //Stores the result of the range search
                        if(bHead == null){ //Check whether the root node is a branch node
                            sb = head.search(startKey, endKey);
                            sb.delete(sb.length()-2, sb.length()-1);
                            pw.println(sb.toString()); //Prints the output to the file
                        }
                        else{
                            sb = bHead.search(startKey, endKey);
                            sb.delete(sb.length()-2, sb.length()-1);
                            pw.println(sb.toString());  //Prints the output to the file
                        }
                    } else { //Calls the single key search
                        double key = Double.parseDouble(params);
                        if(bHead == null)
                            pw.println(head.search(key)); //Prints the output to the file
                        else
                            pw.println(bHead.search(key)); //Prints the output to the file
                    }
                }
            }

        } catch (FileNotFoundException e) {            
            e.printStackTrace();
        }
        pw.flush();
        pw.close();        
    }
	static void initialize(double o){
		m = o;
	}
}
