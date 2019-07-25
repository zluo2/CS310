package bigcalc;

/**
 *
 * @author djn
 */
public class Number
{
   private Node low, high;
   private int decimalPlaces = 0;  // must always be >= 0
   private int digitCount = 0;
   private boolean negative = false;

   public Number()
   {
   }

   public Number(String str)
   {
      if (!validate(str))
         throw new BadNumberException("Invalid number: " + str);
      else
         accept(str);
   }

   /**
    * Tests a string to see if it represents a valid number
    * @param str String to test for valid number
    * @return whether valid or not
    */
   
   private boolean validate(String str)
   {

	 int pointNum = 0; //has point or not
	 char firstSingle = str.charAt(0); 
	 
	 if (str.length()>=2){

		 for (int i=0; i<str.length(); i++){ 
			 
			 char single = str.charAt(i);
			 
			 if (i == 0){	
				 
				 if (!(single == '.' || single == '-' || Character.isDigit(single))) //only three situations
					 return false;
			 }
			 
			 if (single == '.'){ 
				 pointNum++; 
				 if (pointNum>1) //more than one '.'
					 return false;
			 }
			 else {
				 if (i>0&&(!Character.isDigit(single)))
					 return false;
			 }
		 }
	 }
	 else 
		 if (!(Character.isDigit(firstSingle)))
			 return false;
	 
     return true;
   }

   /**
    * Constructs a Number from input string.  This method assumes that
    * str represents a valid number
    * @param str String representing a valid number
    */
   public void accept(String str)
   {
      //TODO: implement this
	  
	  if (this.validate(str)){
		  
		  this.high = null;
		  this.low = null;
		  
		  boolean hasPoint = false;//check '.'
		  
		  this.digitCount = 0;
		  this.decimalPlaces = 0;
		  this.negative = false;
		  
		  Node transfer = null; //using it to connect each node.
		  
		  for (int i = 0; i<str.length(); i++){
			  
			  char single = str.charAt(i);
			  
			  if (single == '-') {
				  negative = true;
			  }
			  
			  if (single == '.') {
				  hasPoint = true;
			  }
			  
			  if (Character.isDigit(single)){
				  
				  this.digitCount++;
				  
				  if (hasPoint) this.decimalPlaces++;
				  
				  int num = Character.getNumericValue(single);
				  Node tempNode = new Node(num);
		
				  if (digitCount == 1) {
					  high = tempNode;
					  high.prev = null;
					  
					  if (str.length() == 1)
						  high.next = null;
				  }
					   
				  if (digitCount!=1 && i != str.length()-1){
					  
					  tempNode.prev = transfer;
					  transfer.next = tempNode;  
				  }
				  
				  if (i == str.length()-1){
					
					  low = tempNode;
					  low.next = null;
					  low.prev = transfer;
					  if (high != low) //in case only have one number
						  transfer.next = low;
				  }
				  
				  transfer = tempNode;  
			  }
		  }
		  
		  this.trim(); 

	  }
	  else throw new BadNumberException("Invalid number: " + str);
   }

   public Number add(Number n)
   {
      Number sum = new Number();
      //TODO: implement this
      
      if ((n.negative&&this.negative)||(!n.negative&&!this.negative)){  //pos + pos and neg + neg
    	  
    	  sum = this.addAbsolute(n);
      }
      
      if ((!this.negative&&n.negative)||(this.negative&&!n.negative)){ //pos + neg and neg + pos
    	  
    	  if (this.compareToAbsolute(n)>=0)
    		  sum = this.subtractAbsolute(n);
    	  else 
    		  sum = n.subtractAbsolute(this);
      }
      
      return sum;
   }

   public Number subtract(Number n)
   {
      Number difference=new Number();
      //TODO: implement this
      
      if ((!this.negative&&n.negative)||(this.negative&&!n.negative)){ // pos - neg and neg - pos
    	  
    	  difference = this.addAbsolute(n);
      }
      
      if ((this.negative&&n.negative)||(!this.negative&&!n.negative)){ // pos - pos and neg - neg
    	  
    	  if (this.compareToAbsolute(n)>=0)
    		  difference = this.subtractAbsolute(n);
    	  else {
    		  difference = n.subtractAbsolute(this);
    	      difference.reverseSign(); //change the sign
    	  }
      }

      return difference;
   }

   public Number multiply(Number n)
   {
	   
      // emulate "paper and pencil" multiplication
      Number product = new Number();

      //TODO: this is an optional part
      //see details in assignment webpage.
      
      int numOfNumber = this.digitCount < n.digitCount? this.digitCount : n.digitCount;
      Number [] tempArray = new Number[numOfNumber];
      
      for (int i = 0; i<numOfNumber; i++)
    	  tempArray[i] = new Number("0");
      
      int count = 0;
      Node temp1 = this.digitCount < n.digitCount? this.low : n.low;
      
      while (numOfNumber > 0){
    	    
    	 Node temp2 = this.digitCount < n.digitCount? n.low : this.low;
    	 
    	 while (temp2 != null){    	
    		 
    		int tempNum = temp1.value * temp2.value;
    		tempArray[count].high.value += tempNum % 10;
    		
    		if (tempArray[count].high.prev == null)
    				tempArray[count].insertHigh(tempNum / 10);
    		else tempArray[count].high.prev.value += tempNum / 10;
    		
    		temp2 = temp2.prev;
    	 }
    	
    	for(int i = 0; i<count; i++)
    		tempArray[count].insertLow(0);
    	
    	tempArray[count].decimalPlaces = 0;
    	
    	count++;
    	numOfNumber --;
    	temp1 = temp1.prev;
    	
      }
      
     
      for (int i = 1; i<count; i++)
    	  tempArray[0].addAbsolute(tempArray[i]);
      
      int adjustPoint = (this.decimalPlaces+n.decimalPlaces)-tempArray[0].digitCount;
      if(adjustPoint > 0)
    	  for (int i=0; i<adjustPoint; i++)
    		  tempArray[0].insertHigh(0);
    	  
      tempArray[0].decimalPlaces = this.decimalPlaces + n.decimalPlaces;
      if ((this.negative&&!n.negative)||(!this.negative&&n.negative))
    	  tempArray[0].negative = true;
      
      product = tempArray[0];
      product.trim();
     
      return product; 
   } 

   public void reverseSign()
   {
      this.negative = !this.negative;
   }

   //compare |this| and |n|
   private int compareToAbsolute(Number n)
   {
	   
     //perform comparison disregarding signs
     //return  0 if |this| == |n|
     //return  1 if |this| > |n|
     //return -1 if |this| < |n|
	 
	 this.sameLength(n);
	 
	 Node tempNode1 = this.high;
	 Node tempNode2 = n.high;
	 
	 int countToPoint1 = 0;
	 int countToPoint2 = 0;
	 
     countToPoint1 = this.digitCount - this.decimalPlaces;
     countToPoint2 = n.digitCount - n.decimalPlaces;
     
     if (countToPoint1 > countToPoint2) return 1; //compare the decimal
     else if (countToPoint1 < countToPoint2) return -1; 
     else if (countToPoint1 == countToPoint2 )
    	 while(true){   // compare each number
		     if (tempNode1.value > tempNode2.value)
		    	 return 1;
		     else if (tempNode1.value < tempNode2.value)
		    	 return -1;
		     else if (tempNode1.value == tempNode2.value){
		    	 
		    	 if (tempNode1.next == null || tempNode2.next == null) 
		    		 break;
		    	 else{
		    		 
		    		 tempNode1 = tempNode1.next;
		    		 tempNode2 = tempNode2.next;
		    	 }
		     }
		 	
    	 }
	 
     return 0;
   }

   //compute |this|+|n|
   private Number addAbsolute(Number n)
   {
      Number sum = new Number();
      //TODO: implement this
      this.sameLength(n);
      
      Node tempNode1 = this.low;
	  Node tempNode2 = n.low;
	  int count = 0;
	  
	  do{
		  int tempNum = 0;  
		  tempNum = tempNode1.value + tempNode2.value;
		  tempNode1.value = tempNum % 10;
	    	  
		  if (tempNum / 10 > 0){
	    	if (tempNode1.prev == null)
	    		this.insertHigh(0);
	    			  
	    	tempNode1.prev.value += tempNum / 10;
		  }
		  
		  tempNode1 = tempNode1.prev;
		  tempNode2 = tempNode2.prev;
		  count++;
	    	  
	  }while(count<this.digitCount);
	      
	  sum = this;
	  sum.trim();
      return sum;
   }

   //compute |this|-|n|
   private Number subtractAbsolute(Number n)
   {
      // assumes that abs(this) >= abs(n)
      Number difference = new Number();
      //TODO: implement this
      this.sameLength(n);
      
      Node tempNode1 = this.low;
	  Node tempNode2 = n.low;
	  int count = 0;

	  do{

		  int tempNum = 0;
		  tempNum = tempNode1.value - tempNode2.value;
		  if (tempNum < 0){
			  
			  tempNode1.prev.value -=1;
			  tempNum+=10;
			  tempNode1.value = tempNum;
		  }  
		  else 
			  tempNode1.value = tempNum;
		  
		  tempNode1 = tempNode1.prev;
		  tempNode2 = tempNode2.prev;
		  count++;
		  
	  }while(count<this.digitCount);
	  
	  difference = this;
	  difference.trim();
      return difference;
   }

   /**
    * Places new digit at high order position of Numbe
    * @param digit digit to append
    */
   private void insertHigh(int digit)
   {
      Node n = new Node(digit);
      // TODO: insert at head of list
      n.next = this.high;
      n.prev = null;
      this.high.prev = n;
      this.high = n;
 
      this.digitCount++;
   }

   /**
    * Places new digit at low order position of Number
    * @param digit digit to append
    */
   private void insertLow(int digit)
   {
      Node n = new Node(digit);
      // TODO: insert at end of list
      n.prev = this.low;
      n.next = null;
      this.low.next = n;
      this.low = n;
      
      if (this.decimalPlaces >=0 && this.digitCount >= 1) //check low before point or not
    	  this.decimalPlaces++;
      
      this.digitCount++;
   }
   
   private void sameLength(Number n) // use this function to control the length for arithmetic 
   {
	   if (this.digitCount!=n.digitCount || this.decimalPlaces!=n.decimalPlaces){
		   
		   int larger = n.decimalPlaces-this.decimalPlaces;
		   
		   int realNum1 = this.digitCount - this.decimalPlaces;
		   int realNum2 = n.digitCount - n.decimalPlaces;
		   
		   if (larger < 0)
			   for (int i = 0; i<Math.abs(larger);i++)
		    		  n.insertLow(0);
		   else
		       for (int i = 0; i<larger;i++)
		    		  this.insertLow(0);
		   
		   if (realNum1>realNum2)
			   for (int i =0; i<realNum1-realNum2; i++)
		    		  n.insertHigh(0);
		   else
			   for (int i=0; i<realNum2-realNum1; i++)
		    		  this.insertHigh(0);
		   
	   }
   }

   /**
    * removes leading 0s
    */
   private void trim()	
   {
      Node p;
      // remove leading 0s preceding decimal point
      while (true){
    	  
    	  p = this.high;
	      if (p.value == 0&&this.digitCount>this.decimalPlaces&&this.digitCount>1){
	    	  
	    	  this.high = this.high.next;
	    	  this.digitCount--;
	    	  
	      }
	      else break;
	  }
      
      Node n;
	   while (true){
		   
		   n = this.low;
		   if (n.value == 0&&this.digitCount>1&&this.decimalPlaces>0){
			   this.low = this.low.prev;
			   this.low.next = null;
			   if (this.decimalPlaces>0){
				   this.decimalPlaces--;
			   }
			   this.digitCount--;
				   
		   }
		   else break;
	   }
      
      if (this.decimalPlaces == this.digitCount && this.high == this.low && this.high.value == 0)
    	  this.decimalPlaces = 0;
   }
   
   private void trim1(){
	   
	   Node p;
	   while (true){
		   
		   p = this.low;
		   if (p.value == 0&&this.digitCount>1&&this.decimalPlaces>0){
			   this.low = this.low.prev;
			   this.low.next = null;
			   if (this.decimalPlaces>0){
				   this.decimalPlaces--;
			   }
			   this.digitCount--;
				   
		   }
		   else break;
	   }
	   
	   if (this.decimalPlaces == this.digitCount && this.high == this.low && this.high.value == 0)
	    	  this.decimalPlaces = 0;
   }

   /**
    * For displaying Number in human readable form
    * @return Stringf representation of Number
    */
   
   @Override
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      int localDigitCount = 0;
      int countToPoint = 0;
      // how far to decimal point?
      countToPoint = digitCount - decimalPlaces;
      Node ptr = high;
      if (negative)
         sb.append('-');
      while (ptr != null)
      {
         if (localDigitCount == countToPoint)
            sb.append('.');
         sb.append(ptr.value);
         ptr = ptr.next;
         localDigitCount++;
      }
      return sb.toString();
   }

   private class Node
   {
      public int value; //0~9
      public Node prev, next;

      public Node(int value)
      {
         this.value = value;
      }
   }
   
   

}
