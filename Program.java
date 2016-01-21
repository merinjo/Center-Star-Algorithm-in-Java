import java.io.*;
import java.util.Scanner;
import java.util.*;

public class p2 {
	public static int direction;
	public static String centerString;
	public static int starIndex;
	public static String globalAlign[]=new String [2];
	public static String multipleAlign[];
	public static void main(String[] args) throws FileNotFoundException {
		
		int n=0;
		int j = 0;
		String sequence[] = new String[1000];       //Input sequences
		Scanner scanner = new Scanner (System.in); 
		String firstLine = scanner.next();
		n = Integer.parseInt(firstLine);   //Number of input strings
		while(j < n)
		{
			sequence[j] = scanner.next();
			j++;
		}
		//String sequence[] = {"AXZ","AXXZ","AYXYZ","AYZ"};
		//n= sequence.length;
		
		int minStarCost = getMinimumStarCost(n,sequence);
		System.out.println("min star cost: "+ minStarCost);
		
		centerString = sequence[starIndex];
		multipleAlign = new String[n];
		multipleAlignment(n,sequence);
		System.out.println("total cost: "+calculateTotalCost(n));
		
		for(int i=0 ; i< n; i++)
			System.out.println(multipleAlign[i]);

	}
	
	//Function to calculate total cost
	public static int calculateTotalCost(int n){
		int score = 0;
		int length = multipleAlign[0].length();
		for(int i = 0; i< n; i++){
			for( int j =0; j< n; j++){
				 if(j > i){
					 for(int k = 0; k < length; k++){
						 if(multipleAlign[i].charAt(k) == multipleAlign[j].charAt(k))
							 continue;
						 else
							 score += 1;
					 }
				 }
			}
		}
		return score;
	}
	
	//Function to do multiple alignment
	public static void multipleAlignment(int n, String sequence[]){
		int score;
		String centerStringCopy = centerString;
		for(int i=0 ; i< n; i++){
			if(i == starIndex){
				multipleAlign[i] = centerStringCopy;
				continue;
			}
		
			score = calculateEditDistance(centerString, sequence[i]);
			multipleAlign[i] = globalAlign[1];
			
			//Add space to all sequence above current multipleAlign[i]***** start
			if(globalAlign[0].length() > centerStringCopy.length()){
				
				for(int j1 = 0,j2 = 0; j1 < globalAlign[0].length(); j1++){
					
					if(centerStringCopy.charAt(j2) != globalAlign[0].charAt(j1)){
						StringBuilder a;
						for(int k = 0; k < i; k++){
							a = new StringBuilder(multipleAlign[k]);
							a.insert(j1, '-');
							multipleAlign[k] = a.toString();
						}
					}
					else
						j2++;
				}
				centerStringCopy = globalAlign[0];
			}                                    //*******************end   
		
			//Add space to the current globalAlign[1]
			if(globalAlign[0].length() < centerStringCopy.length()){   //************start
				
				for(int j1 = 0,j2 = 0; j1 < centerStringCopy.length(); j1++){
					
					if(centerStringCopy.charAt(j1) != globalAlign[0].charAt(j2)){
						StringBuilder a;
						a = new StringBuilder(multipleAlign[i]);
						a.insert(j1, '-');
						multipleAlign[i] = a.toString();
					}
					else
						j2++;
				}
			}                                         //***********************end
			
		}
		
	}
	
	//Function to calculate minimum star cost
	public static int getMinimumStarCost(int n, String s[]){
		
		int editDist = 0;
		int minEditDist = Integer.MAX_VALUE;
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				editDist = editDist + calculateEditDistance(s[i],s[j]);
				//System.out.println("Btw:"+s[i]+" and "+s[j]+" = "+ editDist);
			}
			if(editDist < minEditDist){
				minEditDist = editDist;
				starIndex = i;
			}
			editDist = 0;
		}
		return minEditDist;
	}
	
	//Function to calculate the edit distances
	public static int calculateEditDistance(String seq1, String seq2){
		
		if(seq1.equals(seq2))
			return 0;
		int l1 = seq1.length();
		int l2 = seq2.length();
		int match = 0;
		int i,j,k;
		int score[][] = new int [l1+1][l2+1];
		int trace[][] = new int [l1+1][l2+1];
		score[0][0] = 0;
		trace[0][0] = 0;
		/*
		 *   0   diagonal
		 *   1   left
		 *   2   up
		 */
		//Initialization
		for(i=1; i<=l2; i++){
			score[0][i] = i;
			trace[0][i] = 1;
		}
		for(j=1; j<=l1; j++){
			score[j][0] = j;
			trace[j][0] = 2;
		}
		//sequence 1 - i - l1
		//sequence 2 - j - l2
		//Filling the remaining cells in the matrix
		for(i=1; i<=l1; i++){
			for(j=1; j<=l2; j++){
				if(seq1.charAt(i-1)==seq2.charAt(j-1))
					match = 0;
				else
					match = 1;
		        //     (i-1,j-1)[0]     (i-1,j) [2]
	            //
	            //     (i,j-1)  [1]     (i , j) 
				score[i][j] = calculateMinimum(score[i-1][j-1]+match,score[i][j-1]+1,score[i-1][j]+1);
				trace[i][j] = direction;
			}
		}
		
		//To form the global alignment using traceback
		i = l1;
		j = l2;
		k=0;
	    char [][] pairAlignment = new char [2][l1+l2];
				
		do{
		if(trace[i][j]==0){
			pairAlignment[0][k] = seq1.charAt(i-1);
			pairAlignment[1][k] = seq2.charAt(j-1);
			k++;
			i--;
			j--;
		}
		else if(trace[i][j]==1){
			pairAlignment[0][k] = '-';
			pairAlignment[1][k] = seq2.charAt(j-1);
			k++;
			j--;
		}
		else{
			pairAlignment[0][k] = seq1.charAt(i-1);
			pairAlignment[1][k] = '-';
			k++;
			i--;
		}
		}while(i!=0 || j!=0);
	
		String input;
		char [][] stringReverse = new char [2][k];
		i=0;
		while(k > 0){
			stringReverse[0][i] = pairAlignment[0][k-1];
			stringReverse[1][i] = pairAlignment[1][k-1];
			k--;
			i++;
		}
		input = String.valueOf(stringReverse[0]);
		globalAlign[0] = input;
		input = String.valueOf(stringReverse[1]);
		globalAlign[1] = input;
		
		return score[l1][l2];
	}
	
	//Calculates minimum of three choices for edit distance
	public static int calculateMinimum(int diagonal, int left, int up){
		int temp = diagonal;
		direction = 0;
		if(temp > left){
			temp = left;
			direction = 1;
		}
		if (temp > up){
			temp = up;
			direction = 2;
		}
		return temp;
	}

}
