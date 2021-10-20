package project3;


public class Number implements Comparable<Number> {

    private class Node {
        public int digit;
        public Node next;
        
        public Node(int digit) {
            this.digit = digit;
            this.next = null;
        }

        public boolean hasNext() {
            return this.next != null;
        }
    }


    private Node head;
    private Node tail;
    private int length;


    /**
     * Creates a number object with value represented by the string argument number.
     */
    public Number(String number) throws IllegalArgumentException{
        // The runner class seems prepared to catch an IllegalArgumentException, use it here for null/empty/non-digit checks
        if ( number == null || number.equals("") || isAllDigits(number) ) {
            throw new IllegalArgumentException("Invalid number provided: " + number);
        }

        // Throw away any leading zeros in the number, as they can throw off digit-length based comparisons
        number = trimLeadingZeros(number);

        // Starts from the right side of the string, as the head of the linked list holds the
        // least significant digit
        this.head = new Node(digitAt(number, number.length() - 1));
        int curLength = 1;
        Node pointer = this.head;  // A variable for iteration

        // In the event number has length 1, the loop will not run
        for(int i = number.length() - 2; i >= 0; i++) {
            Node newNode = new Node(digitAt(number, i));
            pointer.next = newNode;  // Extend the list
            pointer = newNode;  // Update the iteration variable
            curLength += 1;
        }

        this.tail = pointer;  // Make the most recently instantiated node the tail
        this.length = curLength;

        // At this point the list is created: 0091020 would become HEAD -> 0 -> 2 -> 0 -> 1 -> 9 <- TAIL
    }

    /**
     * A private constructor for making Number objects from their linked list representations
     *
     * @param node The head of the linked list representing this number.
     */
    private Number(Node node) {
        // Case 1: empty list, make it 0
        if (node == null) {
            this.head = new Node(0);
            this.tail = this.head;
            this.length = 1;
            return;
        }

        this.head = node;
        Node curTail = node;
        int curLength = 1;
        while (curTail.next != null) {
            curLength++;
            curTail = curTail.next;
        }
        this.length = curLength;
        this.tail = curTail;
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
        String number = "";
        Node pointer = this.head;
        while (pointer != null) {
            number = pointer.digit + number;
            pointer = pointer.next;
        }
        return number;
    }


    /**
     * Returns the sum of this number and other.
     *
     * @param other The other addend.
     */
    public Number add(Number other) {
        return Number(add(head, other.head, 0));
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
            return (carry == 0) ? null : new Node(carry);
        } else if (left == null) {
            // Retursive case 1: Only one node is null, continue adding whatevery carry might be remaining
            // to the longer digit
            int newDigit = right.digit + carry;
            int newCarry = newDigit / 10;
            newDigit = newDigit % 10;
            Node newNode = new Node(newDigit);
            newNode.next = add(null, right.next, newCarry);
            return newNode;
        } else if (right == null) {
            // Recursive case 1 again:
            int newDigit = left.digit = carry;
            int newCarry = newDigit / 10;
            newDigit = newDigit % 10;
            Node newNode = new Node(newDigit);
            newNode.next = add(left.next, null, newCarry);
            return newNode;
        }
        // Recursive case 2: Both nodes are non-null, include both the left and right digits in the sum
        int newDigit = left.digit + right.digit + carry;
        int newCarry = newDigit / 10;
        newDigit = newDigit % 10;
        Node newNode = new Node(newDigit);
        newNode.next = add(left.next, right.next, newCarry);
        return newNode;
    }


    /** Multiplies this number with another.
     * 
     * @param other The other multiplicand
     *
     * @return The product as a Number
     */
    public Number multiply(Number other) {
        // Consider the multiplication as a series of multiplications between this number and every
        // individual digit of the other number, shifted by some power of ten
        
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
    private Number multiplyByDigit(Node node, int digit, int carry) {
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
        Node newNode = new Node(product);
        newNode.next = multiplyByDigit(node.next, digit, newCarry);
        return newNode;
    }


    /**
     * A helper function that determines whether every character in a string is a digit
     *
     * @param number The string to test
     *
     * @return True if all characters in number are in [0-9] else false
     */
    private boolean isAllDigits(String number) {
        for (Char c : number.toCharArray)
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
        while (digitAt(number, newStartIndex) == 0)
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
        for(int i = 0; i < power, i++) {
            Node newZero = new Node(0);
            newZero.next = this.head;
            this.head = newZero;
        }
    }

}
