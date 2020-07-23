

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.SparseMultigraph;

public class GrafoColorido {
ProcessDomain pd;	
List<Situa��o> vertices;

List<int[]> arestas;  
List<Caminho> caminhos;
public GrafoColorido(ProcessDomain pd) {
	this.pd = pd;
	
	
}

public GrafoColorido(List<Situa��o> vertices) {
	super();
	this.vertices = vertices;
	this.arestas = new  ArrayList<>();
	this.caminhos = new ArrayList<Caminho>();
	/* inicializa arestas */
	int gsize = this.vertices.size();
	int[] a =  new int[gsize];
	
	int i, k ;
	for (k=0;k<gsize;k++) {
	   for (i=0;i<gsize;i++) {
		  a[i] = 0;
		
     	}
	   this.arestas.add(a);
	}
}



public static void iniciaTeste() {
	Combinador c = new Combinador();
	List<String> d = new ArrayList<>();
	 List<Situa��o> espaco;
	 d.add("a");
	 d.add("b");
	 d.add("c");
//	 d.add("d");
//	 d.add("e");
//	 d.add("f");
	/* d.add("g");
	 d.add("h");
	 d.add("i");
	 d.add("j"); */
	 espaco= c.meuCombinador(d,Operador.Dep);
	 espaco.addAll(c.meuCombinador(d,Operador.Xor));
	 espaco.addAll(c.meuCombinador(d,Operador.Uni));
	 GrafoColorido g = new GrafoColorido(espaco);
	 //testaEstrutura(g);
	 g.mostraSitua��es();
	 g.criaCaminhos();
	 g.imprimeCaminhos();
	 System.out.println("Caminhos secund�rios");
	 g.CriaCaminhosSecund�rios();
	 g.imprimeCaminhos();
}
public void imprimeCaminhos() {
	for(Caminho c: this.caminhos) {
		c.imprimeCaminho();
	}
}
boolean localizaSitua��oemtodosOsCaminhos(Situa��o v) {
	for (Caminho c: this.caminhos) {
		if (c.localizaSitua��o(v))
			return true;
	}
	return false;
}
public void criaCaminhos() {
	Caminho c;
	
	/**
	 * Crio caminhos primarios -  inicialmente s�o todos os caminhos contendo elementos 
	 * n�o mutualmente exclusivos e sem repetir estes elementos em nenhum caminho. 
	 * Nesta etapa o n�mero de caminhos � sempre 3. Porque?
	 */
	for (Situa��o v :this.vertices) {
	  if(this.localizaSitua��oemtodosOsCaminhos(v)==false) {	
	    c = new Caminho();
	    c.insereSitua��o(v);
	    for (Situa��o s :this.vertices) {
		   if (c.localizaSitua��o(s)==false)
			  if(this.localizaSitua��oemtodosOsCaminhos(s)==false)
			     c.insereSitua��o(s);
	    }
	    if(c.getVertices().size()>1) {/* se foi criado um caminho com dois ou mais v�rtices*/
	      this.caminhos.add(c);
	    }  
	  }
	}
	
}
public boolean existeCaminho(List<Caminho> lc,Caminho c) {
	boolean dif;
	for (Caminho x: lc) {
		dif=true;
		for(Situa��o s: c.getVertices()) {
			dif = x.localizaSitua��o(s);
			if(dif==false) {
				break;
			}	
		}	
		if(dif==true)
			return true;
	}
	return false;
	
}

public List<Caminho> CriaCaminhosSecund�rios() {
	/*
	 * Crio caminhos secund�rios: s�o caminhos formados a partir dos caminhos prim�rios
	 * testo a partir do primeiro caminho/primeira situa��o tentando inserir as situa��es 
	 * dos demais caminhos.
	 */
	if (this.caminhos.isEmpty()) { 
		return null;
	}	
	List<Caminho> caux = new ArrayList<Caminho>();
	caux.addAll(this.caminhos);
	int i,k;
	Caminho cCandidato;
	Set<Situa��o> mySet;
	 Set<Set<Situa��o>> pset;
	for (i=0; i<caux.size()-1;i++) {
		/* obtenho o power set do caminho base sem o caminho base */
		 mySet = new HashSet<Situa��o>();
		 mySet.addAll(caux.get(i).getVertices());
		 pset =PowerSet.powerSetr(mySet);
		 
		
		 /*
		  * O pset (power set do caminho que estaremos combinando com as outras situa��oes) 
		  * � o grupo de combina��es de situa��es para os quais iremos inserir novas situa��es 
		  * e verificar se formam novos caminhos
		  * Abaixo cada conjunto do pset (ss) � um subcaminho do caminho base
		  */
		 int tinicial,tfinal;
		 for(Set<Situa��o> ss: pset) {
			if (ss.size()>0) { 
			cCandidato = new Caminho(); 
			cCandidato.addAll(ss);
			tinicial = ss.size();
			/*
			 * Aqui eu percorro a lista dos demais caminhos que est�o contidos na lista de caminhos 
			 * prim�rios e tento inserir as situa��es que est�o nestes caminhos no 
			 * subcaminho ss do caminho base
			 */
			//for (k=i+1; k<caux.size(); k++){
			  k=suc(i,caux.size());
			  do {
				  Caminho cs = caux.get(k);
                  for(Situa��o s: cs.getVertices()) {
                	  cCandidato.insereSitua��o(s);
                  }
			    k=suc(k,caux.size());
		    }while(k!=i+1); 
			tfinal=cCandidato.getVertices().size();
			if (tfinal>tinicial) {
				if(this.existeCaminho(this.caminhos, cCandidato)==false)
				  this.caminhos.add(cCandidato);
			}
			
			}
	    }
	}	 
	return null;
}
public static void  testaEstrutura(GrafoColorido g) {
	int gsize = g.vertices.size();
	int[] a =  new int[gsize];
	
	int i, k ;
	/*for (k=0;k<gsize;k++) {
	   for (i=0;i<gsize;i++) {
		  a[i] = 1;
		
     	}
	   g.arestas.add(a);
	}
	*/
	for (int[] linha :g.arestas ) {
		for(i=0;i<linha.length; i++)
			System.out.print(linha[i]);
		System.out.println();
	}
}
public void mostraSitua��es() {
	for (Situa��o s : this.vertices) {
	    System.out.println("["+s.getEsq()+"-"+s.getOp()+"-"+s.getDir()+"]");
	  }
}
  public void constroiGrafo() {
	  /* neste m�todo iremos definir as arestas do grafo baseado nas regras l�gicas a seguir:
	   * 
	   * 1. Um caminho no grafo (digrafo) � definido como um conjunto de 2 ou mais v�rtices contectados por arestas direcionadas adjascentes 
	   * de uma mesma cor. 
	   * 2. Em um caminho  n�o pode haver v�rtices contendo situa��es mutualmente exclusivas.
	   * 3. Todos os caminhos  devem existir no grafo. 
	   * 4.Opera��es: 
	   *  a) Caminho insereAresta(Caminho c, Vertice v): Insere uma aresta conectando o caminho c ao Vertice v
	   *     Se bem sucedida a opera��o retorna o caminho alterado. Sen�o retorna nil
	   *     Para a opera��o ser bem sucedida o vertice deve conter uma opera��o que n�o inconsistente com nenhuma situa��o que 
	   *   existente no caminho.
	   *  b) boolean checaConsist�ncia(Vertice v1, Vertice v2): Verifica se v1 � consistente com v2
	   *     Retorna true se v1 � consistente com v2 e false se v1 � n�o consistente com v2  
	   *       
	   *  
	   */
 }
 
 public static int suc(int x, int M) {
     if( x == M-1)
    	 return 0;
     else 
    	 return (x+1);
} 
 public static void main(String[] args) {
	iniciaTeste();
	/* int i,k;
	 k=3;         
	 System.out.println("aqui");
	 //for (i=k; (i != ((ant(k,10)))) ; i +=((i%10)+1)) {
	 i=k;
	 do {	     
		 System.out.println(i);
		 i=suc(i,10);
	 }while (i!=k);
	*/
 }
  
}
