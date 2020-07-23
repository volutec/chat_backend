import java.util.ArrayList;
import java.util.List;

public class BagdeSitua��es {
	private List<Situa��o> bag;
 
	public BagdeSitua��es() {
		bag = new ArrayList<Situa��o>();
	}
	
	public List<Situa��o> getBag() {
		return bag;
	}

	public void setBag(List<Situa��o> bag) {
		this.bag = bag;
	}

public boolean localizaSitua��o(Situa��o s) {
		// TODO Auto-generated method stub
		   for (Situa��o v: this.getBag()) {
			 if (v.�Equivalente(s))    
		        return true;
		   }
		   return  false;

	
	}
public  boolean �Equivalente(BagdeSitua��es bags) {
	boolean equivalente=false;
	for(Situa��o v: bags.getBag()) {
		equivalente = true;
		if (this.localizaSitua��o(v)==false) {
			equivalente = false;
		}
	}
	return equivalente;
}

public boolean insereSitua��o(Situa��o s) {
	
	if (this.localizaSitua��o(s)==false) {
		this.getBag().add(s);
	    return true;
	} 
	return false;
}
public void imprimeBag() {
	
	for (Situa��o s: this.getBag()) {
		  s.imprime();
	   }
}
}