/***************************************************************
* file: Stack.java
* author: D. Tapit
* class: CS 445 – Computer Graphics
*
* assignment: part of Program #2
* date last modified: 4/23/2016
*
* purpose: Stack data structure to hold the transformations.
* Holds String values.
****************************************************************/
/**
 * @author DTapit
 */
public class Stack {
	String[] stack;  // char stack
	int top;  // top of the stack, -1 empty stack
	
	Stack(int size) {
		stack = new String[size];
		top = -1;
	}
	
	public void push(String e) {
		if (top+1 == stack.length) { throw new RuntimeException("Stack full"); }
		stack[++top] = e;
	}
	
	public String pop() {
		if (top == -1) { throw new RuntimeException("Stack empty");	}
		return stack[top--];
	}
	
	public int size() { return top+1; }
	public boolean empty() { return top == -1; } // returns a boolean value
	
	public String peek() {
		if (top == -1) { throw new RuntimeException("Stack empty"); }
		return stack[top];
	}
	
	@Override
	public String toString() {
		String s = "BOTTOM[ ";
		for (int i = 0; i <= top; i++) {
			s += stack[i] + " ";
		}
		return s + "]TOP";
	}
	
} // end class Stack
