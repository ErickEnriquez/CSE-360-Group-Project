import java.util.*;

import javax.swing.JOptionPane;

public class PertNode
{
	ArrayList<PertNode> Dependencies;
	String Node;
	int Duration;
	int endTime = -1;
	String[] stringDependencies;
	boolean isCritical = false;
	
	PertNode() // default constructor
	{
		this.Node = "empty";
		this.Duration = 0;
		this.isCritical = false;
	}
	PertNode(String Node, int Duration, String[] stringDependencies)
	{
		this.Node = Node;
		this.Duration = Duration;
		this.stringDependencies = stringDependencies;
		this.isCritical = false;
	}
	int getDuration()
	{
		return Duration;
	}
	String getNode()
	{
		return Node;
	}
	ArrayList<PertNode> getDependencies()
	{
		return Dependencies;
	}
	boolean getCritical()
	{
		return this.isCritical;
	}
	void setDuration(int Duration)
	{
		this.Duration = Duration;
	}
	void setNode(String Node)
	{
		this.Node = Node;
	}
	void setStringDependencies(String[] Dependencies)
	{
		this.stringDependencies = Dependencies;
	}
	void setCritical()
	{
		this.isCritical = true;
	}
	//assuming beyond this point that the data entry has concluded and all nodes have been inserted into masterList;
	//function that determines dependency nodes;
	boolean setDependency(PertList masterList)
	{
		/*
		 * The setDependency method checks the string form of the dependency against
		 * the nodes that are stored in masterList. If the stringDependency
		 * is one of the nodes that the user explicitly entered, then the dependency can exist;
		 * however, if the dependency was not explicitly entered as a node by the user, the chart
		 * cannot be completed, and the function will return false. If this function returns 
		 * false, the entire entry should be aborted and the user should be forced to restart
		 * entry.
		 */
		boolean flag = false; //this flag determines whether or not the given dependency is in the master list or not.
		this.Dependencies = new ArrayList<PertNode>();
		for(int i = 0; i < this.stringDependencies.length; i++)
		{
			 for(int j = 0; j < masterList.masterList.size(); j++)
			 {
				 if(Objects.equals(stringDependencies[i], masterList.masterList.get(j).getNode()))
				 {
					this.Dependencies.add(masterList.masterList.get(j));
					flag = true;
				 }
			 }
			 if (flag == false)
			 {
				 return false;
			 }
		}
		return true;
	}
	int determineEndTime(PertNode Node)
	{
		if(Node.Dependencies.size() == 0)
		{
			return Node.Duration;
		}
		else
		{
			int largest = 0;

			for(int i = 0; i < Node.Dependencies.size(); i++)
			{
				int dependencyEndTime = determineEndTime(Node.Dependencies.get(i));
				if(largest < dependencyEndTime)
				{
					largest = dependencyEndTime;
				}
			}
			return largest + Node.Duration;
		}
	}
	void setEndTime()
	{
		this.endTime = determineEndTime(this);
	}
	/*void setCriticalPath(ArrayList<PertNode> CriticalPath)
	{
		determineCriticalPath(this, CriticalPath);
	}*/
	void markCriticalPath(PertNode Node)
	{
		if(Node.Dependencies.size() == 0)
		{
			//if we reach the start of the path
			return;
		}
		else
		{
			//there are still nodes along the path in need of checking;
			//Determine the largest path(s), starting with the first path
			PertNode Largest = Node.Dependencies.get(0);
			int LargestEndTime = Largest.endTime;
			//loop to see if the node has the longest endTime
			for (int i = 0; i < Node.Dependencies.size(); i++)
			{
				if(Node.Dependencies.get(i).endTime > LargestEndTime)
				{
					Largest = Node.Dependencies.get(i);
					LargestEndTime = Largest.endTime;
				}
			}
			for(int i = 0; i < Node.Dependencies.size(); i++)
			{
				if(Node.Dependencies.get(i).endTime == LargestEndTime)
			 	{
					Node.Dependencies.get(i).setCritical();
					if(Node.Dependencies.size() == 1)
					{
						Node.Dependencies.get(0).isCritical = true;
					}
					markCriticalPath(Node.Dependencies.get(i));
				}
			}
			
		}
	}
	void determineCriticalPaths(PertNode Node, ArrayList<ArrayList<PertNode>> CriticalPaths, ArrayList<PertNode> currentList)
	{
		if(Node.Dependencies.size() == 0)
		{
			if(Node.getCritical())
			{
				currentList.add(Node);
			}
			return;
		}
		else if(Node.Dependencies.size() == 1)
		{
			if(Node.getCritical())
			{
				currentList.add(Node);
			}
			determineCriticalPaths(Node.Dependencies.get(0), CriticalPaths, currentList);
		}
		else
		{
			if(Node.getCritical())
			{
				currentList.add(Node);
			}
			int count = 0;
			for(int i = 0; i < Node.Dependencies.size(); i++)
			{
				if(Node.Dependencies.get(i).getCritical())
				{
					count++;
				}
			}
			for(int i = 0; i < count; i++)
			{
				//We need to make sure the criticalPaths array has a spot for the new branches, this is handled in the following if statement
				if(CriticalPaths.size() < count)
				{
					//we're taking the difference between the number of branches and the size of our list of critical paths in order to
					//add the correct amount of paths to the list so we don't step out of bounds of the array when a new branch is discovered.
					int difference = count - CriticalPaths.size();
					for (int j = 0; j < difference; j++)
					{
						//here we copy the nodes from the reference currentList into newBranch, so we can maintain the path up to this branching
						//for each new list in the CriticalPaths array.
						ArrayList<PertNode> newBranch = new ArrayList<PertNode>();
						newBranch.addAll(currentList);
						CriticalPaths.add(newBranch);
					}
				}
				for(int j = 0; j < Node.Dependencies.size(); j++)
				{
					if(Node.Dependencies.get(j).isCritical)
					{
						determineCriticalPaths(Node.Dependencies.get(j), CriticalPaths, CriticalPaths.get(i));
						i++;
					}
				}
			}
		}
	}
	void determinePaths(PertNode Node, ArrayList<ArrayList<PertNode>> masterList, ArrayList<PertNode> path)
	{
		path.add(Node);
		
		if(Node.Dependencies.size() == 0)
		{
			//traversal done.
			//path.add(Node);
			//ArrayList<PertNode> newerPath = new ArrayList<PertNode>();
			masterList.add(path);
			return;
		}
		else
		{
			for (int i = Node.Dependencies.size() - 1; i >= 0; i--)
			{
				ArrayList<PertNode> newPath = new ArrayList<PertNode>();
				newPath.addAll(path);
				//newPath.add(Node.Dependencies.get(i));
				determinePaths(Node.Dependencies.get(i), masterList, newPath);
			}
		}
	}
	String printDependencies() // this function prints the dependencies to std output.
	{
		String Output = "Node: " + this.Node + " End Time: " + this.endTime;
		return Output;
	}
	boolean compareCriticalPath(PertNode Node, ArrayList<PertNode> CriticalPath)
	{
		for(int i = 0; i < CriticalPath.size(); i++)
		{
			if(Objects.equals(CriticalPath.get(i).Node, Node.Node))
			{
				return false;
			}
		}
		return true;
	}
	String printCriticalPath(ArrayList<PertNode> CriticalPath)
	{
		String Output = "Critical Path:";
		for(int i = 0; i < CriticalPath.size(); i++)
		{
			Output += " " + CriticalPath.get(i).Node + " ";
		}
		return Output;
	}
}
/*void determineCriticalPath(PertNode Node, ArrayList<PertNode> CriticalPath)
{
if(Node.Dependencies.size() == 0)
{
	return;
}
else
{
	PertNode Largest = Node.Dependencies.get(0);
	int largestTime = 0;
	for (int i = 0; i < Node.Dependencies.size(); i++)
	{
		/*if(largestTime < Node.Dependencies.get(i).endTime)
		{
			
		}
		if (Node.Dependencies.get(i).endTime == largestTime)
		{
			
		}
		else if (Node.Dependencies.get(i).endTime > largestTime)
		{
			//from top if statement
			largestTime = Node.Dependencies.get(i).endTime;
			Largest = Node.Dependencies.get(i);
		}
	}
	if(compareCriticalPath(Largest, CriticalPath))
	{
		Critical Path.add(Largest);
	}
	determineCriticalPath(Largest, CriticalPath);
}
*/