import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class No_349_IntersectionofTwoArrays {
	public int[] intersection11ms(int[] nums1, int[] nums2) {
		if(nums1.length==0|nums2.length==0){
			int[] results={};
			return results;
		}


		Set<Integer> set1 = new HashSet<Integer>(); 
		for(int i=0;i<nums1.length;i++){
			set1.add(nums1[i]);
		}

		List<Integer> common = new ArrayList<Integer>();
		for(int j=0;j<nums2.length;j++){
			if( set1.contains(nums2[j])
					&&(!common.contains(nums2[j])) )   common.add(nums2[j]);
		}

		int[] results=new int[common.size()];
		for(int k=0;k<common.size();k++){
			results[k]=common.get(k);
		}

		return results;
	}
	public int[] intersectionTwoHashSet6ms(int[] nums1, int[] nums2) {

		if(nums1.length==0|nums2.length==0){
			int[] results={};
			return results;
		}


		Set<Integer> set1 = new HashSet<Integer>(); 
		for(int i=0;i<nums1.length;i++){
			set1.add(nums1[i]);
		}

		Set<Integer> common = new HashSet<Integer>(); 
		for(int j=0;j<nums2.length;j++){
			if( set1.contains(nums2[j])) common.add(nums2[j]);
		}

		int[] results=new int[common.size()];
		int i=0;
		for(Integer inte: common){
			results[i]=inte;
			i++;
		}

		return results;

	}
	public int[] intersectionTwoPointersSort(int[] nums1, int[] nums2) {
		Arrays.sort(nums1);
		Arrays.sort(nums2);
		int i=0;
		int j=0;
		Set<Integer> common=new HashSet<Integer>();
		while(i<nums1.length&&j<nums2.length){
			if(nums1[i]>nums2[j]){
				j++;
			}else if(nums1[i]<nums2[j]){
				i++;
			}
			else{
				common.add(nums1[i]);
				i++;
				j++;
			}
		}
		
		int[] result=new int[common.size()];
		int k=0;
		for(Integer integ: common){
			result[k]=integ;
			k++;
		}
		return result;
	}
	
	public int[] intersectionBinarySearchSort(int[] nums1, int[] nums2) {
		Arrays.sort(nums1);
		Set<Integer> intersection=new HashSet<Integer>();
		for(Integer integ: nums2){
			if(binarySearch(nums1,integ)) intersection.add(integ);
		}
		int[] result=new int[intersection.size()];
		int k=0;
		for(Integer common: intersection){
			result[k++]=common;
		}
		return result;
	
	}
	public boolean binarySearch(int[] range, int target){
		int low=0;
		int high=range.length-1;
		while(low<=high){
			int mid=low+(high-low)/2;  // in case of over flow by using (high+low)/2
			
			if(range[mid]<target){
				low=mid+1;
			}else if(range[mid]>target){
				high=mid-1;
			}else{
				return true;
			}
				
		}
		return false;
	}
}
