import java.util.ArrayList;
import java.util.List;

public class ListadeIncompatibilidade {
private List<BagdeSitua��es> bagsIncompat�veis;

public ListadeIncompatibilidade(Situa��o s) {
	super();
	
	this.bagsIncompat�veis= new ArrayList<BagdeSitua��es>();
}

public List<BagdeSitua��es> getBagsIncompat�veis() {
	return bagsIncompat�veis;
}
public void setBagsIncompat�veis(List<BagdeSitua��es> bagsIncompat�veis) {
	this.bagsIncompat�veis = bagsIncompat�veis;
}
public void inserebagIncompat�vel(BagdeSitua��es b) {
     this.bagsIncompat�veis.add(b);	
     
}
/*
 * dado um bag, verifica se ele est� na lista dos incompat�veis de lista.
 */

public BagdeSitua��es verificaIncompatibilidade(BagdeSitua��es s) {
	for (BagdeSitua��es b: this.bagsIncompat�veis) {
		 if (b.�Equivalente(s)){
			 return b;
		 }
	}
	return null;
}
public void imprimeLista() {
	for (BagdeSitua��es bag: this.getBagsIncompat�veis() ) {
		bag.imprimeBag();
	}
	
}
}
