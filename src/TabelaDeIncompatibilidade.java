import org.apache.commons.collections15.map.HashedMap;

public class TabelaDeIncompatibilidade {
private HashedMap<Situação,ListadeIncompatibilidade> tab; 
public TabelaDeIncompatibilidade() {
	this.tab= new HashedMap<Situação,ListadeIncompatibilidade>();
}
void insere(Situação s, ListadeIncompatibilidade l) {
	this.tab.put(s, l);
}

public ListadeIncompatibilidade  recuperaLista(Situação s) {
	return this.tab.get(s);
	
}

public static void main(String args[]) {
Situação s1,s2,s3;
TabelaDeIncompatibilidade t = new TabelaDeIncompatibilidade();
	s1 = new Situação("b", "a", Operador.Dep);
	s2 = new Situação("b", "c", Operador.Dep);
	s3 = new Situação("a", "c", Operador.Dep);
	BagdeSituações b1,b2;
	b1 = new BagdeSituações();
	b1.insereSituação(s2);
	b1.insereSituação(s3);
	b2 = new BagdeSituações();
	b2.insereSituação(s1);
	ListadeIncompatibilidade l1,l2;
	l1=new ListadeIncompatibilidade(s1);
	l2=new ListadeIncompatibilidade(s2);
	l1.inserebagIncompatível(b1);
	l1.inserebagIncompatível(b2);
	l2.inserebagIncompatível(b2);
	t.insere(s1, l1);
	t.insere(s2, l2);
	
	ListadeIncompatibilidade ret = t.recuperaLista(s2);
		
	for (BagdeSituações bag: ret.getBagsIncompatíveis() ) {
	   for (Situação s: bag.getBag()) {
		  s.imprime();
	   }
	}

}
}
