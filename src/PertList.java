/* 
 * This class may be unnecessary. The idea is to have all of the pertNodes share
 * a single common list between themselves. This way, they all access the
 * same list when determining whether the given dependency is already a
 * declared/initialized node;
 */
import java.util.*;

public class PertList
{
	ArrayList<PertNode> masterList;
	PertList() //default constructor
	{
		this.masterList = new ArrayList<PertNode>();
	}
	void updateList(PertNode Node)
	{
		this.masterList.add(Node);
	}
	void insertionSort(ArrayList<PertNode> array)
	{
		for (int j = 1; j < array.size(); j++)
		{
			PertNode Key = array.get(j);
			int i = j - 1;
			while(i > -1 && array.get(i).endTime > Key.endTime)
			{
				array.set(i + 1, array.get(i));
				i = i - 1;
			}
			array.set(i + 1, Key);
		}
	}
}
