package project3;


/**
 * Represents a positive decimal integer of arbitrary length as a linked list and provides methods
 * for performing common arithmetic operations on it.
 *
 * @author Aramis Tanelus
 * @version final
 */
public class Number implements Comparable<Number> {

    /**
     * Represents a single digit in the Linked-list representation of the integer.
     */
    private class Node {
        public int digit;
        public Node next;
        public Node prev;
        
        public Node(int digit) {
            this.digit = digit;
            this.next = null;
            this.prev = null;
        }

        /**
         * Returns a string representation of this node.
         */
        public String toString(){
            String result = "" + this.digit;
            return result;
        }
    }


    private Node head;
    private Node tail;
    private int length;


    /**
     * Creates a number object with value represented by the string argument number.
     *
     * @throws IllegalArgumentException when the input is null, empty, or not entirely composed of digit characters
     */
    public Number(String number) throws IllegalArgumentException, NullPointerException{
        // The runner class seems prepared to catch an IllegalArgumentException, use it here for null/empty/non-digit checks
        if ( number == null ) {
            throw new NullPointerException("The input number cannot be null.");
        }
        if ( number.equals("") || !isAllDigits(number) ) {
            throw new IllegalArgumentException("Invalid number provided: " + number);
        }

        // Throw away any leading zeros in the number, as they can throw off digit-length based comparisons
        number = trimLeadingZeros(number);

        // We can start from the left side of the string (most-significant digits) and prepend each digit
        // from left-to-right to end up with the least significant digit at the head
        appendDigit(digitAt(number, 0));

        for(int i = 1; i < number.length(); i++)
            this.prependDigit(digitAt(number, i));

        // The tail variable's assignment was already handled by the appendDigit method
        // Length calculation is also governed by that method

        // At this point the list is created: 0091020 would become HEAD -> 0 <-> 2 <-> 0 <-> 1 <-> 9 <- TAIL
    }

    /**
     * A private constructor for making Number objects from their linked list representations
     *
     * @param node The head of the linked list representing this number.
     */
    private Number(Node node) {
        // All of the functionality of this constructor has already been implemented
        // in appendNode and prependNode
        this.appendNode(node);
    }


    /**
     * Returns the number of digits in the number.
     */
    public int length() {
        return this.length;
    }


    /**
     * Returns a string representation of this Number.
     */
    public String toString() {
        StringBuilder number = new StringBuilder(this.length);
        Node pointer = this.tail;
        while (pointer != null) {
            number.append(pointer.digit);
            pointer = pointer.prev;
        }
        return number.toString();
    }


    /**
     * Compares the this Number with other.
     *
     * @param other Number to compare to
     *
     * @throws NullPointerException when the object of comparison is null
     *
     * @return 1 if this number is greater, 0 if equal, -1 if less
     */
    public int compareTo(Number other) throws NullPointerException{
        if (other == null)
            throw new NullPointerException("Cannot compare to a null reference.");
        // Check length first, longer numbers are guaranteed to be greater since leading zeros 
        // are clipped
        if (length < other.length)
            return -1;
        else if (length > other.length)
            return 1;

        // Starting from the most significant digit (tail) perform a digit-by-digit comparison
        Node thisPointer = this.tail;
        Node otherPointer = other.tail;
        // No need to null check both, they have the same length
        while (thisPointer != null && thisPointer.digit == otherPointer.digit) {
            thisPointer = thisPointer.prev;
            otherPointer = otherPointer.prev;
        }

        if (thisPointer == null)  // They are equal
            return 0;
        else if (thisPointer.digit < otherPointer.digit)  // The other is greater
            return -1;
        else
            return 1;
    }


    /**
     * Determines whether the objects are equal in value.
     *
     * @param obj The object to compare to
     */
    public boolean equals(Object obj) {
        // Alias case: 
        if (this == obj)
            return true;

        // Check other type
        if ( !(obj instanceof Number) )
            return false;

        // Safely cast to number and delegate comparison work to compareTo
        Number other = (Number) obj;
        return compareTo(other) == 0;
    }
            

    /**
     * Returns the sum of this number and other.
     *
     * @param other The other addend.
     * @throws NullPointerEXception if the addend is null
     */
    public Number add(Number other) throws NullPointerException{
        if (other == null)
            throw new NullPointerException("The addend must not be null.");
        return new Number(add(this.head, other.head, 0));
    }

    /**
     * Adds two numbers given by Nodes, taking a carried digit from previous calculations into account.
     *
     * @param left An addend
     * @param right Another addend
     * @param carry A digit carried from a previous calculation
     *
     * @return A linked-list representation of the addends' sum
     */
    private Node add(Node left, Node right, int carry) {
        // Base case 1: both nodes are null and the calculation is done. 
        // Returns the carry digit or null to avoid leading zeros
        if (left == null && right == null) {
            Node output = (carry == 0) ? null : new Node(carry);
            return output;
        } else if (left == null) {
            // Retursive case 1: Only one node is null, continue adding whatevery carry might be remaining
            // to the longer digit
            int newDigit = right.digit + carry;
            int newCarry = newDigit / 10;
            newDigit = newDigit % 10;

            Node currentDigit = new Node(newDigit);
            Node nextDigit = add(null, right.next, newCarry);  // The recursive call
            currentDigit.next = nextDigit;
            if (nextDigit != null)  // The underlying linked list is doubly-linked so this is necessary
                nextDigit.prev = currentDigit;
            return currentDigit;
        } else if (right == null) {
            // Recursive case 1 again:
            int newDigit = left.digit + carry;
            int newCarry = newDigit / 10;
            newDigit = newDigit % 10;

            Node currentDigit = new Node(newDigit);
            Node nextDigit = add(left.next, null, newCarry);
            currentDigit.next = nextDigit;
            if (nextDigit != null)
                nextDigit.prev = currentDigit;
            return currentDigit;
        } else {
            // Recursive case 2: Both nodes are non-null, include both the left and right digits in the sum
            int newDigit = left.digit + right.digit + carry;
            int newCarry = newDigit / 10;
            newDigit = newDigit % 10;

            Node currentDigit = new Node(newDigit);
            Node nextDigit = add(left.next, right.next, newCarry);
            currentDigit.next = nextDigit;
            if (nextDigit != null)
                nextDigit.prev = currentDigit;
            return currentDigit;
        }
    }


    /** Multiplies this number with another.
     * 
     * @param other The other multiplicand
     *
     * @return The product as a Number
     * @throws NullPointerException when the multiplicand is null
     */
    public Number multiply(Number other) throws NullPointerException{
        // Consider the multiplication as a series of multiplications between this number and every
        // individual digit of the other number, shifted by some power of ten
        //
        if (other == null)
            throw new NullPointerException("The multiplicand must not be null.");
        
        Number result = new Number("0");
        int powerOfTen = 0;
        Node otherPointer = other.head;

        while(otherPointer != null) {
            Number intermediateProduct = this.multiplyByDigit(otherPointer.digit);
            intermediateProduct.multiplyByPowTen(powerOfTen);
            result = result.add(intermediateProduct);
            powerOfTen++;
            otherPointer = otherPointer.next;
        }
        
        return result;
    }

    
    /**
     * Computes the product of this number and a digit in the range [0, 9] (inclusive)
     * 
     * @param digit the digit to multiply by
     *
     * @throws IllegalArgumentException when digit is outside the range [0, 9]
     */
    public Number multiplyByDigit(int digit) {
        if (digit == 0)
            return new Number("0");
        if (digit < 0 || digit > 9)
            throw new IllegalArgumentException("Invalid digit passed to multiplyByDigit: " + digit);
        return new Number(multiplyByDigit(this.head, digit, 0));
    }


    /**
     * Multiplies the Number represented by the Node by the given digit, taking the carried number into account.
     *
     * @param node The head of the Number being multiplied.
     * @param digit The digit to multiply by. Assumed to be in [0, 9]
     * @param carry A number carried from previous multiplication that is added to the result.
     */
    private Node multiplyByDigit(Node node, int digit, int carry) {
        // If the node is null and carry is 0, the multiplication is already done, null should be returned
        if (node == null && carry == 0)
            return null;
        // Alternatively, if carry is not zero, return carry as the second base case
        else if (node == null)
            return new Node(carry);

        int product = node.digit * digit + carry;
        int newCarry = 0;
        // If the product is 2 digits, carry the tens digit to the next multiplication
        if (product > 10) {
            newCarry = product / 10;
            product = product % 10;
        }
        // Product now represents the corresponding digit in the new number, carry gets passed on to the
        // next digit's multiplication
        Node currentDigit = new Node(product);
        Node nextDigit = multiplyByDigit(node.next, digit, newCarry);
        currentDigit.next = nextDigit;
        if (nextDigit != null)
            nextDigit.prev = currentDigit;

        return currentDigit;
    }


    /**
     * A helper function that determines whether every character in a string is a digit
     *
     * @param number The string to test
     *
     * @return True if all characters in number are in [0-9] else false
     */
    private boolean isAllDigits(String number) {
        for (char c : number.toCharArray())
            if (c < '0' || c > '9')
                return false;
        return true;
    }

    /**
     * Returns the digit at the specified index of a String as an int.
     *
     * Makes the assumption that the character at that index will be a digit and the
     * index is within a valid range for that string.
     */
    private int digitAt(String str, int index) {
        return Integer.parseInt(str.substring(index, index + 1));
    }

    /**
     * Returns the given string, but with all leading '0' characters removed.
     */
    private String trimLeadingZeros(String number) {
        int newStartIndex = 0;
        while (newStartIndex < number.length() && digitAt(number, newStartIndex) == 0)
            newStartIndex++;

        String newString = number.substring(newStartIndex);

        if (newString.equals(""))
            return "0";  // Prevent silencing a number that was meant to be 0
        return newString;
    }

    
    /**
     * Multiplies this number by some power of ten.
     *
     * @param power The power of ten to multiply by. Assumed to be non-negative.
     */
    private void multiplyByPowTen(int power) {
        for(int i = 0; i < power; i++) {
            this.prependDigit(0);
        }
    }


    /**
     * Helper function for inserting a digit at the left-most (least significant) position of the number.
     * 
     * @param digit The digit to insert
     */
    private void prependDigit(int digit){
        this.prependNode(new Node(digit));
    }

    
    /**
     * Helper function for inserting a node and its prior nodes at the left-most (least significant) position of the number.
     * 
     * @param node The node to insert
     */
    private void prependNode(Node node){
        int additionalLength = 1;
        // Null check just in case
        if (node == null)
            return;

        if (this.head == null) {
            // If the list is empty, the node becomes the new tail
            this.tail = node;
            node.next = null;
        } else {
            this.head.prev = node;
            node.next = this.head;
        }

        Node newHead = node;
        while (newHead.prev != null) {
            additionalLength++;
            newHead = newHead.prev;
        }
        this.head = newHead;
        this.length += additionalLength;
    }


    /**
     * Helper function for inserting a digit at the right-most (most significant) position of the number.
     * 
     * @param digit The digit to insert
     */
    private void appendDigit(int digit) {
        this.appendNode(new Node(digit));
    }


    /**
     * Helper function for inserting a node and its subsequent nodes at the right-most (most significant) position of the number.
     * 
     * @param node The node to insert
     */
    private void appendNode(Node node) {
        int additionalLength = 1;
        // Null check just in case
        if (node == null)
            return;

        if (this.tail == null) {
            // If the list is empty, the node becomes the new head
            this.head = node;
            node.prev = null;
        } else {
            this.tail.next = node;
            node.prev = this.tail;
        }
        
        // Calculate the new tail and how much longer the number is
        Node newTail = node;
        while (newTail.next != null){
            additionalLength++;
            newTail = newTail.next;
        }
        this.tail = newTail;
        this.length += additionalLength;
    }

}
