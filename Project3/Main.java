
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import java.io.File;

import javafx.util.Pair;

public class Main{
	
	public static void main(String[] args) throws IOException{
		
		Scanner input = new Scanner(System.in);
		
		int count = 0; 
		count = input.nextInt();
		
		List<List<Double>> points;
		List<Pair<Double, Double>> range;
		
		boolean go = true;
		
		points = new ArrayList<>(count); //read the x,y,z coordination 
		int i = 0;
		while (i < count){
			List <Double> point = new ArrayList<Double>();
			double x, y, z;
			x =  input.nextDouble();
			point.add(x);
			y =  input.nextDouble();
			point.add(y);
			z =  input.nextDouble();
			point.add(z);
			i++;
			points.add(point);
		}
		System.out.println("Read in "+count+" points");
		
		while (go){
			range = new ArrayList<>(3); 
			System.out.print("Query: ");
			
			if (input.hasNextDouble()||input.hasNextInt()){  	
				double temp1, temp2, min = 0, max =0;
				//get the query - put in range
				for (int j = 0; j <3; j++){
					temp1 = input.nextDouble();
					temp2 = input.nextDouble();
					
					min = Math.min(temp1, temp2);
					max = Math.max(temp1, temp2);
					range.add(new Pair<>(min, max));
				}
			}
			
			else {  //check the query 	
				String temp = input.next();
				if (temp.compareTo("exit")==0){
					System.out.println("bye");
					break;
				}
				else continue;
				
			}
			
			List<List<Double>> result;
			RangeTree<Double> searchTree = new RangeTree<Double>(3, points);
			result = searchTree.search(range);
			
			if (result.size()>1)
				System.out.println("Found "+result.size()+" points");
			else 
				System.out.println("Found "+result.size()+" point");
			
			//sorted the point array
			List<List<Double>> sortedPoint = result
	                .stream()
	                .sorted((x, y) -> x.get(2).compareTo(y.get(2)))
	                .sorted((x, y) -> x.get(1).compareTo(y.get(1)))
	                .sorted((x, y) -> x.get(0).compareTo(y.get(0)))
	                .collect(Collectors.toList());
			for (i =0; i<sortedPoint.size(); i++)
			System.out.println(sortedPoint.get(i).get(0)+" "+sortedPoint.get(i).get(1)+" "+sortedPoint.get(i).get(2));
			System.out.println();
		}
		
		input.close();
	}
	
}