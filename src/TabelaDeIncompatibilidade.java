import org.apache.commons.collections15.map.HashedMap;

public class TabelaDeIncompatibilidade {
private HashedMap<Situa��o,ListadeIncompatibilidade> tab; 
public TabelaDeIncompatibilidade() {
	this.tab= new HashedMap<Situa��o,ListadeIncompatibilidade>();
}
void insere(Situa��o s, ListadeIncompatibilidade l) {
	this.tab.put(s, l);
}

public ListadeIncompatibilidade  recuperaLista(Situa��o s) {
	return this.tab.get(s);
	
}

public static void main(String args[]) {
Situa��o s1,s2,s3;
TabelaDeIncompatibilidade t = new TabelaDeIncompatibilidade();
	s1 = new Situa��o("b", "a", Operador.Dep);
	s2 = new Situa��o("b", "c", Operador.Dep);
	s3 = new Situa��o("a", "c", Operador.Dep);
	BagdeSitua��es b1,b2;
	b1 = new BagdeSitua��es();
	b1.insereSitua��o(s2);
	b1.insereSitua��o(s3);
	b2 = new BagdeSitua��es();
	b2.insereSitua��o(s1);
	ListadeIncompatibilidade l1,l2;
	l1=new ListadeIncompatibilidade(s1);
	l2=new ListadeIncompatibilidade(s2);
	l1.inserebagIncompat�vel(b1);
	l1.inserebagIncompat�vel(b2);
	l2.inserebagIncompat�vel(b2);
	t.insere(s1, l1);
	t.insere(s2, l2);
	
	ListadeIncompatibilidade ret = t.recuperaLista(s2);
		
	for (BagdeSitua��es bag: ret.getBagsIncompat�veis() ) {
	   for (Situa��o s: bag.getBag()) {
		  s.imprime();
	   }
	}

}
}
