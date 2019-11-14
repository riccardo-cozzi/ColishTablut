package it.unibo.ai.didattica.competition.tablut.client;

import java.util.Iterator;
import java.util.LinkedList;

public class MyTree<T> {

	/**
	 * Node of the MinMaxTree
	 * @author Administrator
	 *
	 */
	public class MyNode<E> {
		private E value;
		private MyNode<E> father;
		private LinkedList< MyNode<E> > sons;
		
		public MyNode() {
			super();
			this.sons = new LinkedList<>();
		}
		
		public MyNode(E value) {
			super();
			this.value = value;
			this.sons = new LinkedList<>();
		}
		
		public MyNode(E value, MyNode<E> father) {
			super();
			this.value = value;
			this.father = father;
			this.sons = new LinkedList<>();
		}
		
		
		
		public String toString() {
	        StringBuilder buffer = new StringBuilder(50);
	        printSubTree(buffer, "", "");
	        return buffer.toString();
	    }

	    private void printSubTree(StringBuilder buffer, String prefix, String childrenPrefix) {
	    
	        buffer.append(prefix);
	        buffer.append(this.value);
	        buffer.append('\n');
	        for (Iterator<MyNode<E>> it = sons.iterator(); it.hasNext();) {
	        	MyNode<E> next = (MyNode<E>) it.next();
	            if (it.hasNext()) {
	                next.printSubTree(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
	            } else {
	                next.printSubTree(buffer, childrenPrefix + "└── ", childrenPrefix + "   ");
	            }
	        }
	    }
	}
	
	private MyNode<T> root;

	public MyTree () {
		this.root = null;
	}

	public MyTree(T rootValue) {
		this.root = new MyNode<T>(rootValue);
	}

	public MyNode<T> getRoot() {
		return this.root;
	}

	public LinkedList<MyNode<T>> getSons(MyNode<T> node) {
		return node.sons;
	}
	
	public MyNode<T> getSon(MyNode<T> node, int index) {
		return node.sons.get(index);
	}

	public MyNode<T> getFather(MyNode<T> node) {
		return node.father;
	}

	public boolean isEmpty() {
		return this.root == null;
	}

	/* TODO utile? */
	public boolean isRoot(MyNode<T> node) {
		return node.father == null;
	}

	/**
	 * Expands a state node computing all the possible next reachable states
	 * @param node
	 * @param listOfSons
	 */
	public void setSons(MyNode<T> node, LinkedList<T> listOfSons) {
		for (T value:listOfSons) {
			this.addSon(node, value);
		}
	}

	public void setRoot(T value) {
		this.root = new MyNode<T>(value);
	}

	public void addSon(MyNode<T> node, T value) {
		node.sons.add(new MyNode<T>(value, node));
	}
	
	public void removeSubTree(MyNode<T> node) {
		//node.father.sons.remove(node); // remove all the sons from the father
		if (this.isRoot(node)) {
			this.root = null;
		} else {
			node.father.sons.remove(node);
		}
	}
	
	public boolean isLeaf(MyNode<T> node) {
		return node.sons.isEmpty();
	}
	
	public String toString() {
		if (isEmpty()) return "[ empty tree ]";
		else return this.root.toString();
	}

	/* subclasses can see it and use it for return the parameter*/
	public MyNode<T> getNode(MyNode<T> node) {
		return node;
	}
	
	public T getValue(MyNode<T> node) {
		return node.value;
	}
	
	/**
	 * Test the tree
	 * @param args
	 */
	public static void main (String args[]) {
		/* creating a tree */
		MyTree<String> t = new MyTree<String>();
		System.out.println("empty tree:\n" + t);
	
		/* setting the root */
		t.setRoot("A");
		System.out.println("one-level tree:\n" + t);

		/* setting the sons of the root */
		LinkedList<String> sons = new LinkedList<String>();
		sons.add("B");
		sons.add("C");
		t.setSons( t.root, sons);
		System.out.println("two-level tree, the root has two sons:\n" + t);
		
		/* adding a son to the list of the son of the root*/ 
		t.addSon(t.root, "D");
		System.out.println("two-level tree, now the root has three sons:\n" + t);
		
		/* adding a son to the first son of the root */ 
		t.addSon(t.getSons(t.getRoot()).get(0), "E");
		System.out.println("three-level tree, now the root's first son has two sons:\n" + t);

		/* adding three sons to the second son of the root */ 
		LinkedList<String> sons2 = new LinkedList<String>();
		sons2.add("F"); 
		sons2.add("G");
		sons2.add("H");
		t.setSons(t.getSon(t.getRoot(), 1), sons2);
		System.out.println("three-level tree, now the root's first son has two sons and the second one has three sons:\n" + t);

		/* removing the first child and the subtree rooted in it */		
		t.removeSubTree(t.getSon(t.getRoot(), 0));
		System.out.println("Now:\n" + t);

	}

}
