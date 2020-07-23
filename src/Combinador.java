

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Combinador {
	private static  void helper2(List<int[]> combinations, int data[], int start, int end, int index) {
	    if (index == data.length) {
	        int[] combination = data.clone();
	        combinations.add(combination);
	    } else if (start <= end) {
	        data[index] = start;
	        helper(combinations, data, start + 1, end, index + 1);
	        helper(combinations, data, start + 1, end, index);
	    }
	}
	private static  void helper( List<int[]> combinations, int data[], int start, int end, int index) {
	    if (index == data.length) {
	        int[] combination = data.clone();
	        combinations.add(combination);
	        int aux[] = combination.clone();
	        int swap;
	        swap = aux[0];
	        aux[0]= aux[1];
	        aux[1]=swap;
	        combinations.add(aux); 
	    } else if (start <= end) {
	        data[index] = start;
	        helper(combinations, data, start + 1, end, index + 1);
	        helper(combinations, data, start + 1, end, index);
	    }
	}
	public static List<int[]> generate(int n, int r) {
	    List<int[]> combinations = new ArrayList<>();
	    helper(combinations, new int[r], 0, n-1, 0);
	    return combinations;
	}
public  List<Situação> meuCombinador(List<String> dominio, Operador op) {
	List<Situação> espaco = new ArrayList<>();
	Situação s;
	for (String c: dominio) {
		//Iterator i = dominio.iterator();
		List<String> sd = dominio.subList(dominio.indexOf(c)+1, dominio.size());
		for (String next: sd) { 
			//System.out.println(c + "--"+next);
			s = new Situação(c,next,op);
			espaco.add(s);
			if(op == Operador.Dep) {
				s = new Situação(next,c,op);
				espaco.add(s);
				//System.out.println(next + "--"+c);
			}
		}
	}
	return espaco;
}

public static void main(String[] args) {
 Combinador c = new Combinador();
 List<String> d = new ArrayList<>();
 List<Situação> espaco;
 d.add("a");
 d.add("b");
 d.add("c");
 d.add("d");
 espaco= c.meuCombinador(d,Operador.Dep);
 espaco.addAll(c.meuCombinador(d,Operador.Xor));
 espaco.addAll(c.meuCombinador(d,Operador.Uni));
  for (Situação s : espaco) {
	    System.out.println("["+s.getEsq()+"-"+s.getOp()+"-"+s.getDir()+"]");
	  }
}

/*	
	List<int[]> combinations = generate(5, 2);
char a='a';

for (int[] combination : combinations) {
    System.out.println(Arrays.toString(combination));
    System.out.println(a);
    a++;
}
System.out.printf("generated %d combinations of %d items from %d ", combinations.size(), 2, 3);
}
*/
}