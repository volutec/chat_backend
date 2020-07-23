import java.util.ArrayList;
import java.util.List;

public class ListadeIncompatibilidade {
private List<BagdeSituações> bagsIncompatíveis;

public ListadeIncompatibilidade(Situação s) {
	super();
	
	this.bagsIncompatíveis= new ArrayList<BagdeSituações>();
}

public List<BagdeSituações> getBagsIncompatíveis() {
	return bagsIncompatíveis;
}
public void setBagsIncompatíveis(List<BagdeSituações> bagsIncompatíveis) {
	this.bagsIncompatíveis = bagsIncompatíveis;
}
public void inserebagIncompatível(BagdeSituações b) {
     this.bagsIncompatíveis.add(b);	
     
}
/*
 * dado um bag, verifica se ele está na lista dos incompatíveis de lista.
 */

public BagdeSituações verificaIncompatibilidade(BagdeSituações s) {
	for (BagdeSituações b: this.bagsIncompatíveis) {
		 if (b.éEquivalente(s)){
			 return b;
		 }
	}
	return null;
}
public void imprimeLista() {
	for (BagdeSituações bag: this.getBagsIncompatíveis() ) {
		bag.imprimeBag();
	}
	
}
}
