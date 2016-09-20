

	
	 // Definition for singly-linked list.
	 class ListNode {
	      int val;
	      ListNode next;
	      ListNode(int x) { val = x; }
	  }
	 
	public class No_19_RemoveNthNodeFromEndofList {
	    public ListNode removeNthFromEnd(ListNode head, int n) {
	    	 
	    	ListNode dummyHead=new ListNode(0);
	    	dummyHead.next=head;
	    	ListNode beforeDeletionPointer=dummyHead,gapPointer=dummyHead;
	    	
	    	//creat the gap
	    	for(int count=1;count<=n+1;count ++){
	    		gapPointer=gapPointer.next;
	    	}
	    	
	    	//find the last one 
	    	while(gapPointer!=null){
	    		beforeDeletionPointer=beforeDeletionPointer.next;
	    		gapPointer=gapPointer.next;
	    	}
	    	
	    	beforeDeletionPointer.next=beforeDeletionPointer.next.next;
	    	
	    	return dummyHead.next;
	    }
	}

