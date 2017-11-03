
// Change this to your group number
package cs6301.g40;

import java.util.Iterator;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
 


// Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>> {
   	int size;
	int maxlevel;
	public static final int INFINITY = Integer.MAX_VALUE;
	public static final int MINUSINFINITY = Integer.MIN_VALUE;
	Entry<T> head;
	Entry<T> tail;
	
	public class Entry <T extends Comparable<? super T>>{
		ArrayList <Entry<T>> next;
		ArrayList span;
		
		T element;
		Entry(T x){
			this.element = x;
             this.next = new ArrayList<>();
             this.span = new ArrayList();
		}
		public T getElement(){
			return this.element;
		}
	}
	// Constructor
	public SkipList() {
		maxlevel = 0;
		this.head = new Entry(MINUSINFINITY);
		this.tail = new Entry(INFINITY);
		this.head.next.add(tail);
		this.head.span.add(0);
		this.tail.span.add(0);
	}
	
	
	
	// Add x to list. If x already exists, replace it. Returns true if new node is added to list
	
	/**
	 *
	 * @param x : The number which has to be added to the list.
	 * @return : true if a new node is added to the list else false
	 *
	 * The function calls find(x) function to find its appropriate place in the skip list and
	 * chooseLevel() function to randomly generate level of new Node. Its add the node to the list updating
	 * ArrayLists, next and span of all the nodes.
	 */
	public boolean add(T x) {
		Entry<T> [] prev = find(x);
		if((prev[0].next.get(0)).element.compareTo(x)==0){
			(prev[0].next.get(0)).element =x;
		}
		else{
			int lev = chooseLevel();
			Entry<T> newEntry = new SkipList.Entry(x);
			int level;
			if(lev<=maxlevel)
				level = lev;
			else level = maxlevel;
			for(int i=0;i<=level;i++){
				Entry<T> temp = prev[i].next.get(i);
				newEntry.next.add(temp);
				int c1 = countNext(newEntry,temp,i);
				newEntry.span.add(c1);
				int c2 = countNext(prev[i],newEntry,i);
				prev[i].next.set(i,newEntry);
				prev[i].span.set(i,c2);
			}
			
			if(lev>this.maxlevel){
				this.head.next.add(newEntry);
				newEntry.next.add(this.tail);
				int c1 = countNext(newEntry,tail,lev);
				int c2 = countNext(head,newEntry,lev);
				newEntry.span.add(c1);
				head.span.add(c2);
				tail.span.add(0);
				
				this.maxlevel++;
			}
			else{
				lev++;
				while(lev<=this.maxlevel){
					Entry<T> temp = head;
				 while(!temp.equals(tail)){
				 	Entry<T> nextTemp = temp.next.get(lev);
				 	int c1 = countNext(temp,nextTemp,lev);
				 	
				 	temp.span.set(lev,c1);
				 	
				 	
				 	temp = nextTemp;
				 }
					lev++;
				}
			}
			this.size++;
			return true;
		}
		
		return false;
	}
	
	/**
	 *
	 * @param a : First node from which span needs to be calculated
	 * @param b : Second node up to which span needs to be calculated
	 * @param i : The level at which span is getting calculated
	 * @return : span between node a and node b at level i.
	 *
	 * The function calculates span between two nodes a and b at level i based upon the span of the level i-1
	 */
	public int countNext(Entry<T> a,Entry<T> b, int i){
		if(i==0) return 0;
		else{
			i--;
			
			Entry<T> temp = a;
			int count= (int) temp.span.get(i);
			while(!temp.next.get(i).equals(b)){
				temp = temp.next.get(i);
				count= count + (Integer) temp.span.get(i) + 1;
				}
		return count;
		}
	}
	
	public Entry<T>[] find(T x){
		Entry [] prev = new Entry [maxlevel+1];
		Entry<T> temp = head;
		List<Entry<T>> list = new LinkedList<>();
		for(int i =this.maxlevel; i>=0;i--){
			while(((temp.next.get(i)).element).compareTo(x)<0){
				temp = temp.next.get(i);
			}
			prev[i]=temp;
		}
		return prev;
	}
	
	/**
	 *
	 * @return : Height of newly added node in the skip list
	 * The function randomly generate height for a new node in the skip list.
	 */
	public int chooseLevel(){
		Random rand = new Random();
		int n = rand.nextInt();
		
		int mask = 1 << this.maxlevel;
		mask = mask -1;
		int lev = Integer.numberOfTrailingZeros(n & mask);
		if(lev > this.maxlevel){
			
			return this.maxlevel+1;
		}
		else return lev;
		
	}
	
	/**
	 *
	 * @param x : Inputted value
	 * @return : returns the smallest element that is greater or equal to x
	 */
	public T ceiling(T x) {
		Entry<T>[] prev = find(x);
		 if((prev[0].next.get(0)).getElement().compareTo(x)==0)
		 	return x;
		 else {
			 return (prev[0].next.get(0)).getElement();
		 }
	}
	
	
	
	
	
	/**
	 *
	 * @param x : Inputted value to check
	 * @return : A boolean signifying whether list contains x or not
	 */
	public boolean contains(T x) {
		Entry<T>[] prev = find(x);
		if( (prev[0].next.get(0)).getElement().compareTo(x)==0)
		  return true;
		else
			return false;
	}
	
	
	
	/**
	 *
	 * @return : Return first element of list
	 */
	public T first() {
		return head.next.get(0).getElement();
	}
	
	
	
	/**
	 *
	 * @param x : The inputted value
	 * @return : The largest element that is less than or equal to x
	 */
	public T floor(T x) {
		Entry<T>[] prev = find(x);
		if((prev[0].next.get(0)).getElement().compareTo(x)==0)
			return x;
		else
		return (prev[0]).getElement();
	}
	
	// Return element at index n of list.  First element is at index 0.
	
	/**
	 *
	 * @param n : The index value
	 * @return : The element corresponding to the index n
	 * The function utilizes span ArrayList to find the element at index n in log(n) time
	 */
	public T get(int n) {
		
	   Entry<T> temp = head;
	   int count =-1;
		for (int i = this.maxlevel; i >= 0; i--) {
		
			while (count<n) {
				int jump = (int)temp.span.get(i)+1;
				
			
				if(count+jump==n){
					temp = temp.next.get(i);
					return temp.getElement();
				}
				
				else if(count+jump<n){
					count = count + jump;
					temp = temp.next.get(i);
				}
				else break;
			}
		}
		return temp.getElement();
	}
	
	// Is the list empty?
	
	/**
	 *
	 * @return : A boolean to show if skip-list is empty or not
	 * The function checks the size member attribute of SkipList class
	 */
	public boolean isEmpty() {
		if(this.size()==0)
			return true;
		else return false;
	}
	
	// Iterate through the elements of list in sorted order
	public Iterator<T> iterator() {
	  return (java.util.Iterator<T>) new cs6301.g40.SkipList.SkipListIterator();
	}
	public class SkipListIterator<T> implements java.util.Iterator<T>{
		
		Entry cursor;
		public SkipListIterator(){
			cursor = head;
		}
		
		public boolean hasNext(){
			boolean result = !cursor.next.get(0).equals(tail);
			return result;
		}
		
		public T next(){
			cursor = (cs6301.g40.SkipList.Entry) cursor.next.get(0);
			return (T) cursor.getElement();
		}
	}
	
	
	
	/**
	 *
	 * @return : Returns last element of list
	 * The function returns last element of the skip list
	 */
	public T last() {
		
		return (get(this.size()-1));
	}
	
	
	
	/**
	 * The function reorganize the elements of the list into a perfect skip list.
	 * The function first empties the ArrayLists, span and head and then call build() function.
	 */
	public void rebuild() {
		Entry<T> temp = head.next.get(0);
		for(int i = maxlevel;i>0;i--){
			head.span.set(i,this.size());
			head.next.set(i,tail);
		}
	//	List<Entry<T>> list = new LinkedList<>();
		while(((temp.next.get(0)).element).compareTo(tail.getElement())!=0) {
			//	for(int i =1; i<=this.maxlevel;i--){
			while (temp.next.size() > 1) {
				temp.next.remove(temp.next.size() - 1);
				temp.span.remove(temp.span.size()-1);
			}
			temp = temp.next.get(0);
		}
		build(0,this.size()-1,this.maxlevel,tail,head);
	}
	
	/**
	 *
	 * @param start : Start index of skip list for rebuild
	 * @param end : End index of skip list for rebuild
	 * @param height : Height at which & below entries are updated
	 * @param tailNew : Last Entry up to which to update
	 * @param headNew : First Entry from which to update
	 */
	public void build(int start,int end,int height,Entry<T> tailNew,Entry<T> headNew){
		if(start>=end)
			return;
	
		int mid = (end+start)/2;
		
		Entry<T> [] prev =  find(get(mid));
		Entry<T> temp = prev[0].next.get(0);
	
		int j=1;
		while(j<=height){
			headNew.next.set(j,temp);
			temp.next.add(tailNew);
			int c1 = countNext(headNew,temp,j);
			int c2 = countNext(temp,tailNew,j);
			headNew.span.set(j,c1);
			temp.span.add(c2);
			j++;
		}
		build(start,mid-1,height-1,temp,headNew);
		build(mid+1,end,height-1,tailNew,temp);
	}
	// Remove x from list.  Removed element is returned. Return null if x not in list
	
	/**
	 *
	 * @param x : The number which has to be removed from the list.
	 * @return : null if number not found else number itself
	 * It calls find() to find the location of number and then updates ArrayLists (span and next) of previous number
	 * in the skip list.
	 */
	public T remove(T x) {
		Entry<T> [] prev = find(x);
		Entry<T> toRemove = prev[0].next.get(0);
		if(toRemove.getElement().compareTo(x)!=0)
		return null;
		else{
			for(int i=0;i<=maxlevel;i++){
				if((prev[i].next.get(i))==toRemove){
					int spanNew =(Integer) prev[i].span.get(i) + (Integer) toRemove.span.get(i);
					prev[i].next.set(i,toRemove.next.get(i)) ;
				     prev[i].span.set(i,spanNew);
				}
				
				else
				{
					int spanNew = (int) prev[i].span.get(i);
					prev[i].span.set(i,--spanNew);
				}
				
			}
		}
		this.size--;
		return toRemove.getElement();
	}
	
	
	
	/**
	 *
	 * @return : Return the number of elements in the list
	 * This helper function returns the size of skip list
	 */
	public int size() {
		return this.size;
	}
	public static void main(String [] args){
		SkipList<Integer> skip  = new SkipList<>();
		skip.add(8);
		skip.add(17);
		skip.add(3);
		skip.add(7);
		skip.add(20);
		skip.add(15);
		skip.add(23);
		skip.add(45);
		skip.add(50);
		skip.add(86);
		skip.add(90);
	  //  skip.remove(17);
	  //  int a = skip.get(2);
		// Integer a = skip.get(3);
		skip.rebuild();
    /*Iterator it = skip.iterator();
    while(it.hasNext()){
    System.out.println(it.next());
	 }*/
    
	}
	
}