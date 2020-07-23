
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ciclo.CycleDetector;
import ciclo.SimpleGraphView2;

import java.awt.Dimension;
import javax.swing.JFrame;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

public class Caminho {
	private List<Situação> vertices;
	private Cores cor;
	ProcessDomain dCaminho; //guarda o domínio
	private DirectedSparseMultigraph<String, Number> gbif = new DirectedSparseMultigraph<String, Number> ();
	private DirectedSparseMultigraph<String, Number> gdep = new DirectedSparseMultigraph<String, Number> ();
	public List<Situação> getVertices() {
		return vertices;
	}

	public void setVertices(List<Situação> vertices) {
		this.vertices = vertices;
	}

	public Cores getCor() {
		return cor;
	}

	public void setCor(Cores cor) {
		this.cor = cor;
	}

	public DirectedSparseMultigraph<String, Number> getGdep() {
		return gdep;
	}

	public void setGdep(DirectedSparseMultigraph<String, Number> gdep) {
		this.gdep = gdep;
	}
	
	
	
  /* o grafo de dependência é para manter a consistência das situação.
   * Este grafo permite por exemplo checar por meio de ciclos se não havera dependência cíclica na inclusão de um
  um nova situação ao caminho. 
   */
public Caminho() {
  this.vertices = new ArrayList<Situação>();	
  this.cor=Cores.red;
	}	
/*
 * Para uma dependencia ser inserida não deve haver dependência cíclica. Isto é checado pela detecção de
 * dependência inversa ou de ciclos no grafo auxiliar gdep.
 */
public boolean insereSituaçãoDependência(Situação v) {
	CycleDetector dcd = new CycleDetector (gdep);
	boolean b=true;
	if (this.vertices.isEmpty()) { // primeiro elemento no caminhos
		this.vertices.add(v);
		gdep.addVertex(v.esq);
		gdep.addVertex(v.dir);
		gdep.addEdge(gdep.getEdgeCount(), v.esq,v.dir);
		return true;		
	} 
	if(gdep.containsVertex(v.esq) && gdep.containsVertex(v.dir)) {
		if(gdep.findEdge(v.esq, v.dir)!=null) {						
	//if(gdep.isNeighbor(v.esq, v.dir)) { 
			/* já existe esta dependência */
		//   System.out.println("Dependência já existe");
		   return false;
		}	
	//if(gdep.isNeighbor(v.dir, v.esq)) {
		if(gdep.findEdge(v.dir, v.esq)!=null){
			/* já existe esta dependência */
		 // System.out.println("Dependência inversa");
		  return false;
	 }	
	}
    if(gdep.containsVertex(v.esq)) {
    	if(gdep.containsVertex(v.dir)) {
    		gdep.addEdge(gdep.getEdgeCount(), v.esq,v.dir);
    		b = dcd.detectCycle();
    		if (b) {
    			gdep.removeEdge(gdep.findEdge(v.esq, v.dir));
    			
    		}
    		else this.vertices.add(v);
    		return !b;
    	}
    	else {
    		gdep.addVertex(v.dir);
    		gdep.addEdge(gdep.getEdgeCount(), v.esq,v.dir);
    		b = dcd.detectCycle();
    		if (b) {
    			gdep.removeEdge(gdep.findEdge(v.esq, v.dir));
    			gdep.removeVertex(v.dir);
    			
    		}
    		else this.vertices.add(v);
    		return !b;
    	}
    }
    else {
    	if(gdep.containsVertex(v.dir)) {
    		//System.out.print("passou aqui");
    		gdep.addVertex(v.esq);
    	    gdep.addEdge(gdep.getEdgeCount(), v.esq,v.dir);
    	    b = dcd.detectCycle();
    	    if (b) {
    			gdep.removeEdge(gdep.findEdge(v.esq, v.dir));
    			gdep.removeVertex(v.esq);
    			
    		}
    	    else this.vertices.add(v);
    		return !b;
    	}
         else {
        	 System.out.println("aqui2");
        	 gdep.addVertex(v.esq);
        	 gdep.addVertex(v.dir);
     	     gdep.addEdge(gdep.getEdgeCount(), v.esq,v.dir);
     	     b = dcd.detectCycle();
     	     
     	    if (b) {
    			gdep.removeEdge(gdep.findEdge(v.esq, v.dir));
    			gdep.removeVertex(v.esq);
    			gdep.removeVertex(v.dir);
    			
    		}
     	   else { this.vertices.add(v); 
     	     
     	   }
    		return !b;
        	 
        }
    }
    	
}
/* uma situação de escolha pode ser um XOR ou uma União
 * Pré-condições:
 * Uma situação de escolha só pode ser inserida no caminho se:
 * - Os operandos não estiverem em uma relação direta ou cíclica de dependência
 * - Nem um dos  operandos  estiver do lado direito de uma dependência.
 * Isso se verifica no grafo gdep vendo se há incidência no vertice v.esq ou no v.dir 
 * a dep b
 * b dep c
 * a xor c
 */
/* remove uma situação qualquer no caminho. Caso a situação seja de dependência 
 * a relação de dependência também é removida de gdep  
 */
public void removeSituação(Situação v) {
	this.gdep.removeEdge(this.gdep.findEdge(v.esq,v.dir));
	this.vertices.remove(v);
	
	//this.gdep.removeVertex(v.esq);
	//this.gdep.removeVertex(v.dir);
}
public boolean insereSituaçãoEscolha(Situação v) {
	Situação dummy = new Situação(v.esq,v.dir,Operador.Dep);
	boolean possível=false;
	possível = this.insereSituaçãoDependência(dummy);
//	System.out.println("1-" +possível + " " + dummy.esq + " " + dummy.dir);
	if(possível) {
		this.removeSituação(dummy);
		dummy= new Situação(v.dir,v.esq,Operador.Dep);
		possível = this.insereSituaçãoDependência(dummy);
	//	System.out.println("2-" +possível + " " + dummy.esq + " " + dummy.dir);
		if(possível) {
			this.vertices.add(v);
			this.removeSituação(dummy);
		} 
	} 
	return possível;
}

public boolean testaSituaçãoEscolha(Situação v) {
	Situação dummy = new Situação(v.esq,v.dir,Operador.Dep);
	boolean possível=false;
	possível = this.insereSituaçãoDependência(dummy);
	//System.out.println("1-" +possível + " " + dummy.esq + " " + dummy.dir);
	this.removeSituação(dummy);
/*	if(possível) {
		
		dummy= new Situação(v.dir,v.esq,Operador.Dep);
		possível = this.insereSituaçãoDependência(dummy);
		System.out.println("2-" +possível + " " + dummy.esq + " " + dummy.dir);
		if(possível) {
			this.removeSituação(dummy);
		} 
	}*/ 
	return possível;
}

public boolean insereSituação(Situação v) {
	boolean possível=true;
	List<Situação> vaux;// = new ArrayList<Situação>(vertices);
	Situação aux,aux1; 
	/* 
	 * Teste equivalencia - verifico se existe uma dependência igual ou de algum   tipo choice Xor ou Uni
	 * 
	 */
	if(v.isChoice()) { //verifica tipo de operador: Xor ou Uni
		/*checo prosmicuidade na inserção */
		if(this.checaSituaçãoPromiscua(v)==true) {
			System.out.println("Situação de promiscuidade detectada:");
				return false;
		} 	
		//System.out.println("tentando inserir nova situação:");
		// v.imprime();	
		  if (v.getOp()==Operador.Xor) {
			 aux = new Situação(v.getEsq(),v.getDir(), Operador.Uni);
		  }
		  else {
			  aux = new Situação(v.getEsq(),v.getDir(), Operador.Xor);
		  }
			
		 // System.out.println("verificando se já existe:");
		//	aux.imprime();
			
		 if((this.localizaSituação(v)) || (this.localizaSituação(aux))){
			  return false;
		 }
	}
	else {
		 aux = new Situação(v.getEsq(),v.getDir(), Operador.Xor);
		 aux1 = new Situação(v.getEsq(),v.getDir(), Operador.Uni);
		if(this.localizaSituação(v) || this.localizaSituação(aux) || this.localizaSituação(aux1)){
				return false;
	 }
    }
	/**
	 * Teste dual - Verifica se a situação vai gerar depedencia dual no caminho. 
	 * Se sim a situação não pode ser inserida.
	 */
	if(this.checaDependênciaDualdeChoice(v)) {
		//System.out.println("Dependência dual Detectada");
		return false;
	}

	/* faz uma copia  dos vertices pois a lista original sera manipulada durante a verificação */
	
	if(v.isChoice()==false) {
		if(possível=insereSituaçãoDependência(v)) {
			vaux = new ArrayList<Situação>(vertices);
	 	  for (Situação s : vaux) {
			 if(s.isChoice()) {
				 possível= testaSituaçãoEscolha(s);
			 }
		  } 
		  if (possível==false) {
				this.removeSituação(v);
				
			}
		  return possível;
	   }
	   return false;	
		
	}
	
	else { /* testa a inserção de uma dependencia esq-dir */
	     	
	    Situação vx = new Situação(v.getEsq(),v.getDir(), Operador.Dep);
		if(possível=insereSituaçãoDependência(vx)) {
			  vaux = new ArrayList<Situação>(vertices);
		 	  for (Situação s : vaux) {
				 if(s.isChoice()) {
					 possível= testaSituaçãoEscolha(s);
				 }
			  } 
		 	 this.removeSituação(vx);
	          if (possível) {
	        	  vx = new Situação(v.getDir(),v.getEsq(), Operador.Dep);
	        	  if(insereSituaçãoDependência(vx)) {
	        	    for (Situação s : vaux) {
	 			     	 if(s.isChoice()) {
	 					   possível= testaSituaçãoEscolha(s);
	 				     }
	 			     } 
	              }
	          }
	          this.removeSituação(vx);
	          if(possível){ 	  
		         return insereSituaçãoEscolha(v);
	          }
	          return possível;
	     }
		 return possível;
   }
}
/**
 * verifica se os parâmetros de uma situação choice não são ambas operadores dependidos de um mesmo operador. e Vice-versa.
 * ex: b dep a, b dep c, a xor c.
 * solução: ao inserir uma sit. z xor w,  para todo operador x faça Verifico em Gdep se existe o edge (x --> z) e (x --> w) 
 * @param v
 * @return boolean
 */

 
public boolean checaDependênciaDualdeChoice(Situação v) {
	boolean depDual=false;
	if (v.isChoice()) { /* verifico a situação dual quando v é um choice. Neste caso testo o choice a ser 
	 inserido com as dependências existentes */
	   for (String s : this.gdep.getVertices()) {
		  if(depDual = ( (this.gdep.findEdge(s, v.getEsq())!=null) && (this.gdep.findEdge(s, v.getDir())!=null) ))
			break;
	   }
	   return depDual;
	}
	else { /* verifico a condição dual quando v é uma dependência. neste caso testo a condição com cada choice já inserido*/
		boolean inseriu = this.insereSituaçãoDependência(v); /* esta dependência já pode ter sido inserida */
	   for (Situação s1 : this.getVertices()) {
		 if(s1.isChoice()) {
			 if(depDual = ( (this.gdep.findEdge(v.getEsq(),s1.getEsq())!=null) && (this.gdep.findEdge(v.getEsq(),s1.getDir())!=null) ))
				break;
		     }
	 	 }
	     if (inseriu) {
	    	  this.removeSituação(v);
	     }	  
		 return depDual; 
	} 
		
}
/**
 * ChecarChoicesTiposDiferenteMesmoOPerador - 
 * Verificar se a situação é um choice (xor, uni) de um tipo com operandos já usados em um 
 * choice de outro tipo.
 * Solução:
 */
  public boolean checaSituaçãoPromiscua(Situação s) {
	  boolean eesq=false,edir=false;
	  Operador opaux1=null, opaux2=null;
	  for (Situação v: this.getVertices()) {
		  if (v.isChoice()) {
			  if (v.contemOperando(s.getEsq())){
				  eesq=true;
				  opaux1=v.getOp();
			  } else {
				  eesq=false;
			  }
			  if (v.contemOperando(s.getDir())){
				  edir=true;
				  opaux2=v.getOp();
			  } else {
				  edir=false;
			  }
		  }
		  if (eesq && edir) {
			  if((opaux1!=opaux2) || (opaux1!=s.getOp()) || (opaux2!=s.getOp())) {
				  return true;
			  }	  
		  }
	  }
	  return false;
  }
public void imprimeCaminho() {
	// TODO Auto-generated method stub
	System.out.println("*********** novo caminho***********");
	for (Situação v: this.getVertices()) {
	 v.imprime();
	}
	
	
}

public void mostraGrafo(DirectedSparseMultigraph g) {
	 // SimpleGraphView2 sgv = new SimpleGraphView2(); // This builds the graph
      // Layout<V, E>, VisualizationComponent<V,E>
	 Layout<Integer, String> layout = new CircleLayout(g);
     layout.setSize(new Dimension(300,300));
     //BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
     BasicVisualizationServer vv = new BasicVisualizationServer<>(layout);
     vv.setPreferredSize(new Dimension(350,350));       
     // Setup up a new vertex to paint transformer...
     Transformer<String,Paint> vertexPaint = new Transformer<String,Paint>() {
         public Paint transform(String i) {
             return Color.YELLOW;
         }
     };  
     // Set up a new stroke Transformer for the edges
    // float dash[] = {10.0f};
    /* final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
          */
    /* Transformer<String, Stroke> edgeStrokeTransformer = new Transformer<String, Stroke>() {
         public Stroke transform(String s) {
             return edgeStroke;
         }
     };
     */
     vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
    // vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
     vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
     vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
     vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);        
     
     JFrame frame = new JFrame("Grafo de Dependência do Caminho");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.getContentPane().add(vv);
     frame.pack();
     frame.setVisible(true);   	    
	
}
public boolean isEmpty() {
	return vertices.isEmpty();
}
public void addAll(Set<Situação> setSituação) {
	for (Situação s: setSituação) {
		this.insereSituação(s);
	}
}

public static void testeCaminho1() {
	/*
[b-Xor-c]
[a-Xor-c]
[a-Uni-b]
[a-Dep-c]
[b-Dep-c]
[c-Dep-a]
	 */
	Caminho c = new Caminho();
	String[] domain = {"a","b","c","d","e","f","g"};
	boolean ret;
	c.dCaminho = new ProcessDomain(domain);
	
	Situação s1,s2,s3,s4,s5,s6,s7,s8;
	s1 = new Situação("b", "c", Operador.Xor);
	s2 = new Situação("a", "c", Operador.Xor);
	s3 = new Situação("a", "b", Operador.Uni);  		
	s4 = new Situação("a", "c", Operador.Dep);
	s5 = new Situação("b", "c", Operador.Dep);
	s6 = new Situação("c", "a", Operador.Dep);
	ret=c.insereSituação(s1);
	if(ret) {
		System.out.println("Situação Inserida s1");
	}
	else {
		System.out.println("Dependência detectada s1");
			
	}
	
	ret=c.insereSituação(s2);
	if(ret) {
		System.out.println("Situação Inserida s2");
	}
	else {
		System.out.println("Dependência detectada s2");
			
	}
	
	ret=c.insereSituação(s3);
	if(ret) {
		System.out.println("Situação Inserida s3");
	}
	else {
		System.out.println("Dependência detectada s3");
			
	}
	
		
	ret=c.insereSituação(s4);
	if(ret) {
		System.out.println("Situação Inserida s4");
	}
	else {
		System.out.println("Dependência detectada s4");
			
	}

	ret=c.insereSituação(s5);
	//ret=c.insereSituaçãoDependência(s5);
	if(ret) {
		System.out.println("Situação Inserida s5");
	}
	else {
		System.out.println("Dependência detectada s5");
			
	}
	
	ret=c.insereSituação(s6);
	if(ret) {
		System.out.println("Situação Inserida s6");
	}
	else {
		System.out.println("Dependência detectada s6");
			
	}
	
	c.mostraGrafo(c.getGdep());
}
/*
 * [b-Xor-c]
[a-Uni-b]
[a-Dep-c]
[c-Dep-b]
 */

public static void testeCaminho2() {
	
	Caminho c = new Caminho();
	String[] domain = {"a","b","c","d","e","f","g"};
	boolean ret;
	c.dCaminho = new ProcessDomain(domain);
	
	Situação s1,s2,s3,s4,s5,s6,s7,s8;
	s1 = new Situação("b", "c", Operador.Xor);
	s2 = new Situação("a", "b", Operador.Uni);
	s3 = new Situação("a", "c", Operador.Dep);
	s4 = new Situação("c", "b", Operador.Uni);  		
	
	ret=c.insereSituação(s1);
	if(ret) {
		System.out.println("Situação Inserida s1");
	}
	else {
		System.out.println("Dependência detectada s1");
			
	}
	
	ret=c.insereSituação(s2);
	if(ret) {
		System.out.println("Situação Inserida s2");
	}
	else {
		System.out.println("Dependência detectada s2");
			
	}
	
	ret=c.insereSituação(s3);
	if(ret) {
		System.out.println("Situação Inserida s3");
	}
	else {
		System.out.println("Dependência detectada s3");
			
	}
	
		
	ret=c.insereSituação(s4);
	if(ret) {
		System.out.println("Situação Inserida s4");
	}
	else {
		System.out.println("Dependência detectada s4");
			
	}

		c.mostraGrafo(c.getGdep());
}
public static void testeCaminho() {
	Caminho c = new Caminho();
	String[] domain = {"a","b","c","d","e","f","g"};
	
	c.dCaminho = new ProcessDomain(domain);
	boolean ret;
	Situação s1,s2,s3,s4,s5,s6,s7,s8;

	/*
	//iguais ok
	s1 = new Situação("a", "b", Operador.Dep);
	s2 = new Situação("a", "b", Operador.Dep);
*/

	/*
	//iguais ok
	s1 = new Situação("a", "b", Operador.Xor);
	s2 = new Situação("a", "b", Operador.Xor);
*/
	
	
/*
	//invertida ok
	s1 = new Situação("b", "a", Operador.Dep);
	s2 = new Situação("a", "b", Operador.Dep);
*/
	
/*
	// [a dep X] ... [X dep b]
	s1 = new Situação("b", "a", Operador.Dep);
	s2 = new Situação("a", "c", Operador.Dep);
	s3 = new Situação("c", "d", Operador.Dep);  
	s4 = new Situação("d", "b", Operador.Dep);
*/
	
	// [a dep X] ... [X dep b] longa
	s1 = new Situação("b", "a", Operador.Dep);
	s2 = new Situação("a", "c", Operador.Dep);
	s3 = new Situação("c", "d", Operador.Dep);  
	s4 = new Situação("d", "e", Operador.Dep);
	s5 = new Situação("e", "f", Operador.Dep);
	s6 = new Situação("f", "g", Operador.Dep);
	s7 = new Situação("g", "b", Operador.Dep);
	



	
	
//	s1 = new Situação("b", "a", Operador.Dep);
//	s2 = new Situação("b", "c", Operador.Dep);
//	s3 = new Situação("a", "c", Operador.Dep); // esta dependência foi removida pelo algoritmo 
												// na inserção de. Preciso testar se a dependência
	//já existe no grafo para que ela não seja removida nos testes usados para inserção de choices. 
		
//	s4 = new Situação("a", "c", Operador.Dep);
//	s4 = new Situação("d", "e", Operador.Dep);
	//s5 = new Situação("a", "g", Operador.Xor);
//s5 = new Situação("d", "a", Operador.Dep);
//	s6 = new Situação("b", "g", Operador.Xor);
	//s7 = new Situação("c", "g", Operador.Uni);
//	s7 = new Situação("a", "b", Operador.Uni);
/* erro edge já existente ao tentar inserir dependência igual a uma existente - corrigido */
 	s8 = new Situação("g", "d", Operador.Dep);
 	

	ret=c.insereSituação(s1);
	if(ret) {
		System.out.println("Situação Inserida s1");
	}
	else {
		System.out.println("Dependência detectada s1");
			
	}
	
	ret=c.insereSituação(s2);
	if(ret) {
		System.out.println("Situação Inserida s2");
	}
	else {
		System.out.println("Dependência detectada s2");
			
	}
	
	ret=c.insereSituação(s3);
	if(ret) {
		System.out.println("Situação Inserida s3");
	}
	else {
		System.out.println("Dependência detectada s3");
			
	}
	
		
	ret=c.insereSituação(s4);
	if(ret) {
		System.out.println("Situação Inserida s4");
	}
	else {
		System.out.println("Dependência detectada s4");
			
	}

	ret=c.insereSituação(s5);
	//ret=c.insereSituaçãoDependência(s5);
	if(ret) {
		System.out.println("Situação Inserida s5");
	}
	else {
		System.out.println("Dependência detectada s5");
			
	}
	
	ret=c.insereSituação(s6);
	if(ret) {
		System.out.println("Situação Inserida s6");
	}
	else {
		System.out.println("Dependência detectada s6");
			
	}
	ret=c.insereSituação(s7);
	if(ret) {
		System.out.println("Situação Inserida s7");
	}
	else {
		System.out.println("Dependência detectada s7");
			
	}
/* erro ao inserir dependência igual
 * 	
corrigido */
	ret=c.insereSituação(s8);
	if(ret) {
		System.out.println("Situação Inserida s8");
	}
	else {
		System.out.println("Dependência detectada s8");
			
	}
 
	c.mostraGrafo(c.getGdep());
	
	c.imprimeCaminho();
}


public void InserirSituacaoNoCaminho(ArrayList<Situação> listSituation, Caminho c) {
	for (int i = 0; i<listSituation.size(); i++) {
		if (c.insereSituação(listSituation.get(i))) {
			System.out.println("Situação Inserida [" + listSituation.get(i).esq + "-" + listSituation.get(i).op + "-" + listSituation.get(i).dir + "]");
		} else {
			System.out.println("Dependência detectada " + listSituation.get(i).esq + "-" + listSituation.get(i).op + "-" + listSituation.get(i).dir + "]");
		}
	}
	//return c;

}

public static void testeCaminhoX() {
	Caminho c = new Caminho();

	String[] domain = {"a","b","c","d","e","f","g"};

	ArrayList<Situação> listSituation = new ArrayList<Situação>();
	
	c.dCaminho = new ProcessDomain(domain);

	
	
	// [a dep X] ... [X dep b] longa
	listSituation.add(new Situação("a", "b", Operador.Xor));
	listSituation.add(new Situação("b", "c", Operador.Xor));
	listSituation.add(new Situação("a", "c", Operador.Uni));
	listSituation.add(new Situação("b", "a", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();

	/*
	//iguais ok
	listSituation.add(new Situação("a", "b", Operador.Dep));
	listSituation.add(new Situação("a", "b", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
	*/
/*
	//invertida ok
	listSituation.add(new Situação("a", "b", Operador.Dep));
	listSituation.add(new Situação("b", "a", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/
/*
	// [a dep X] ... [X dep b] curta
	listSituation.add(new Situação("a", "b", Operador.Dep));
	listSituation.add(new Situação("a", "c", Operador.Dep));
	listSituation.add(new Situação("c", "d", Operador.Dep));
	listSituation.add(new Situação("d", "b", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/	

/*
	// [a dep X] ... [X dep b] longa
	listSituation.add(new Situação("b", "a", Operador.Dep));
	listSituation.add(new Situação("a", "c", Operador.Dep));
	listSituation.add(new Situação("c", "d", Operador.Dep));
	listSituation.add(new Situação("d", "e", Operador.Dep));
	listSituation.add(new Situação("e", "f", Operador.Dep));
	listSituation.add(new Situação("f", "g", Operador.Dep));
	listSituation.add(new Situação("g", "b", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/
/*	
	// [a dep X] ... [X xor b] longa
	listSituation.add(new Situação("b", "a", Operador.Dep));
	listSituation.add(new Situação("a", "c", Operador.Dep));
	listSituation.add(new Situação("c", "d", Operador.Dep));
	listSituation.add(new Situação("d", "e", Operador.Dep));
	listSituation.add(new Situação("e", "f", Operador.Dep));
	listSituation.add(new Situação("f", "g", Operador.Dep));
	listSituation.add(new Situação("g", "b", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

/*
	// [a Xor b] [a xor b] 
	listSituation.add(new Situação("a", "b", Operador.Xor));
	listSituation.add(new Situação("a", "b", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

/*
	// [a Xor b] [b xor a] 
	listSituation.add(new Situação("a", "b", Operador.Xor));
	listSituation.add(new Situação("b", "a", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

	/*
	// [a Xor b] [b xor a] 
	listSituation.add(new Situação("a", "b", Operador.Xor));
	listSituation.add(new Situação("b", "a", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

/*
	// [a Xor X] ... [X xor b] longa
	listSituation.add(new Situação("b", "a", Operador.Xor));
	listSituation.add(new Situação("a", "c", Operador.Dep));
	listSituation.add(new Situação("c", "d", Operador.Dep));
	listSituation.add(new Situação("d", "e", Operador.Dep));
	listSituation.add(new Situação("e", "f", Operador.Dep));
	listSituation.add(new Situação("f", "g", Operador.Dep));
	listSituation.add(new Situação("g", "b", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
 
*/
/*	
	//  
	listSituation.add(new Situação("a", "b", Operador.Xor));
	listSituation.add(new Situação("b", "c", Operador.Xor));
	listSituation.add(new Situação("b", "a", Operador.Xor)); // retirar a operacao contraria
	c.InserirSituacaroNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/	
}

public boolean localizaSituação(Situação s) {
	// TODO Auto-generated method stub
	   for (Situação v: this.getVertices()) {
		 if (v.éEquivalente(s))    
	        return true;
	   }
	   return  false;
}

public static void main(String[] args) {
	//Caminho.testeCaminho2();
	Caminho.testeCaminhoX();
}



}
