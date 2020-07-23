import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowerSet {

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<T>());
	        return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	        Set<T> newSet = new HashSet<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }       
	    return sets;
	}
	/* retorna o power set com o conjunto original removido */
	public static <T> Set<Set<T>> powerSetr(Set<T> originalSet) {
		Set<Set<T>> pset = PowerSet.powerSet(originalSet);
		Set<Set<T>> psetaux = new HashSet<Set<T>>();;
		psetaux.addAll(pset);
		 for (Set<T> s : pset) {
			 if (s.size()==originalSet.size()) {
				 psetaux.remove(s);
			 }
		 }	 
		return psetaux;
		
	}
	
	public static void main(String args[]) {
		Set<Situa��o> mySet = new HashSet<Situa��o>();
		Caminho c = new Caminho();
		Situa��o s1 = new Situa��o("b", "a", Operador.Dep);
		Situa��o s2 = new Situa��o("b", "c", Operador.Dep);
		Situa��o s3 = new Situa��o("a", "c", Operador.Dep);
		 c.insereSitua��o(s1);
		 c.insereSitua��o(s2);
		 c.insereSitua��o(s3);
		 mySet.addAll(c.getVertices());
		 Set<Set<Situa��o>> pset = PowerSet.powerSetr(mySet);
		 for (Set<Situa��o> s : pset) {
			 System.out.println("*****");
		     for(Situa��o v: s) {
		    	 v.imprime();
			 
		     }
		 }
	}
}
