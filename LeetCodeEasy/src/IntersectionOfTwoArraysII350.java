import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntersectionOfTwoArraysII350 {
	
	public int[] intersectionTwoList(int[] nums1, int[] nums2) {
		
		List<Integer> list1=new ArrayList<Integer>();
		for(int i=0;i<nums1.length;i++){
			list1.add(nums1[i]);
		}
		
		List<Integer> intersection=new ArrayList<Integer>();
		for(int j=0;j<nums2.length;j++){
			if(list1.contains(nums2[j])){
				list1.remove((Object)nums2[j]);
				intersection.add(nums2[j]);
			}
		}
		
		int[] result=new int[intersection.size()];
		int k=0;
		for(Integer element: intersection){
			result[k++]=element;
		}
		return result;
		
	}
	
	
	public int[] intersectionTwoPointers(int[] nums1, int[] nums2) {
		Arrays.sort(nums1);
		Arrays.sort(nums2);
		int i=0,j=0;
		
		List<Integer> intersection=new ArrayList<Integer>();
		while(i<nums1.length && j<nums2.length){
			
			if(nums1[i]>nums2[j]){
				j++;
			}else if(nums1[i]<nums2[j]){
				i++;
			}else{
				intersection.add(nums1[i]);
				i++;
				j++;
			}
		}
		
		int[] result=new int[intersection.size()];
		int k=0;
		for(Integer element: intersection){
			result[k++]=element;
		}
		return result;
	}
}

