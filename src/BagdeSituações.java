import java.util.ArrayList;
import java.util.List;

public class BagdeSituações {
	private List<Situação> bag;
 
	public BagdeSituações() {
		bag = new ArrayList<Situação>();
	}
	
	public List<Situação> getBag() {
		return bag;
	}

	public void setBag(List<Situação> bag) {
		this.bag = bag;
	}

public boolean localizaSituação(Situação s) {
		// TODO Auto-generated method stub
		   for (Situação v: this.getBag()) {
			 if (v.éEquivalente(s))    
		        return true;
		   }
		   return  false;

	
	}
public  boolean éEquivalente(BagdeSituações bags) {
	boolean equivalente=false;
	for(Situação v: bags.getBag()) {
		equivalente = true;
		if (this.localizaSituação(v)==false) {
			equivalente = false;
		}
	}
	return equivalente;
}

public boolean insereSituação(Situação s) {
	
	if (this.localizaSituação(s)==false) {
		this.getBag().add(s);
	    return true;
	} 
	return false;
}
public void imprimeBag() {
	
	for (Situação s: this.getBag()) {
		  s.imprime();
	   }
}
}