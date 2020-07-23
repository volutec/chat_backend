
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
	private List<Situa��o> vertices;
	private Cores cor;
	ProcessDomain dCaminho; //guarda o dom�nio
	private DirectedSparseMultigraph<String, Number> gbif = new DirectedSparseMultigraph<String, Number> ();
	private DirectedSparseMultigraph<String, Number> gdep = new DirectedSparseMultigraph<String, Number> ();
	public List<Situa��o> getVertices() {
		return vertices;
	}

	public void setVertices(List<Situa��o> vertices) {
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
	
	
	
  /* o grafo de depend�ncia � para manter a consist�ncia das situa��o.
   * Este grafo permite por exemplo checar por meio de ciclos se n�o havera depend�ncia c�clica na inclus�o de um
  um nova situa��o ao caminho. 
   */
public Caminho() {
  this.vertices = new ArrayList<Situa��o>();	
  this.cor=Cores.red;
	}	
/*
 * Para uma dependencia ser inserida n�o deve haver depend�ncia c�clica. Isto � checado pela detec��o de
 * depend�ncia inversa ou de ciclos no grafo auxiliar gdep.
 */
public boolean insereSitua��oDepend�ncia(Situa��o v) {
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
			/* j� existe esta depend�ncia */
		//   System.out.println("Depend�ncia j� existe");
		   return false;
		}	
	//if(gdep.isNeighbor(v.dir, v.esq)) {
		if(gdep.findEdge(v.dir, v.esq)!=null){
			/* j� existe esta depend�ncia */
		 // System.out.println("Depend�ncia inversa");
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
/* uma situa��o de escolha pode ser um XOR ou uma Uni�o
 * Pr�-condi��es:
 * Uma situa��o de escolha s� pode ser inserida no caminho se:
 * - Os operandos n�o estiverem em uma rela��o direta ou c�clica de depend�ncia
 * - Nem um dos  operandos  estiver do lado direito de uma depend�ncia.
 * Isso se verifica no grafo gdep vendo se h� incid�ncia no vertice v.esq ou no v.dir 
 * a dep b
 * b dep c
 * a xor c
 */
/* remove uma situa��o qualquer no caminho. Caso a situa��o seja de depend�ncia 
 * a rela��o de depend�ncia tamb�m � removida de gdep  
 */
public void removeSitua��o(Situa��o v) {
	this.gdep.removeEdge(this.gdep.findEdge(v.esq,v.dir));
	this.vertices.remove(v);
	
	//this.gdep.removeVertex(v.esq);
	//this.gdep.removeVertex(v.dir);
}
public boolean insereSitua��oEscolha(Situa��o v) {
	Situa��o dummy = new Situa��o(v.esq,v.dir,Operador.Dep);
	boolean poss�vel=false;
	poss�vel = this.insereSitua��oDepend�ncia(dummy);
//	System.out.println("1-" +poss�vel + " " + dummy.esq + " " + dummy.dir);
	if(poss�vel) {
		this.removeSitua��o(dummy);
		dummy= new Situa��o(v.dir,v.esq,Operador.Dep);
		poss�vel = this.insereSitua��oDepend�ncia(dummy);
	//	System.out.println("2-" +poss�vel + " " + dummy.esq + " " + dummy.dir);
		if(poss�vel) {
			this.vertices.add(v);
			this.removeSitua��o(dummy);
		} 
	} 
	return poss�vel;
}

public boolean testaSitua��oEscolha(Situa��o v) {
	Situa��o dummy = new Situa��o(v.esq,v.dir,Operador.Dep);
	boolean poss�vel=false;
	poss�vel = this.insereSitua��oDepend�ncia(dummy);
	//System.out.println("1-" +poss�vel + " " + dummy.esq + " " + dummy.dir);
	this.removeSitua��o(dummy);
/*	if(poss�vel) {
		
		dummy= new Situa��o(v.dir,v.esq,Operador.Dep);
		poss�vel = this.insereSitua��oDepend�ncia(dummy);
		System.out.println("2-" +poss�vel + " " + dummy.esq + " " + dummy.dir);
		if(poss�vel) {
			this.removeSitua��o(dummy);
		} 
	}*/ 
	return poss�vel;
}

public boolean insereSitua��o(Situa��o v) {
	boolean poss�vel=true;
	List<Situa��o> vaux;// = new ArrayList<Situa��o>(vertices);
	Situa��o aux,aux1; 
	/* 
	 * Teste equivalencia - verifico se existe uma depend�ncia igual ou de algum   tipo choice Xor ou Uni
	 * 
	 */
	if(v.isChoice()) { //verifica tipo de operador: Xor ou Uni
		/*checo prosmicuidade na inser��o */
		if(this.checaSitua��oPromiscua(v)==true) {
			System.out.println("Situa��o de promiscuidade detectada:");
				return false;
		} 	
		//System.out.println("tentando inserir nova situa��o:");
		// v.imprime();	
		  if (v.getOp()==Operador.Xor) {
			 aux = new Situa��o(v.getEsq(),v.getDir(), Operador.Uni);
		  }
		  else {
			  aux = new Situa��o(v.getEsq(),v.getDir(), Operador.Xor);
		  }
			
		 // System.out.println("verificando se j� existe:");
		//	aux.imprime();
			
		 if((this.localizaSitua��o(v)) || (this.localizaSitua��o(aux))){
			  return false;
		 }
	}
	else {
		 aux = new Situa��o(v.getEsq(),v.getDir(), Operador.Xor);
		 aux1 = new Situa��o(v.getEsq(),v.getDir(), Operador.Uni);
		if(this.localizaSitua��o(v) || this.localizaSitua��o(aux) || this.localizaSitua��o(aux1)){
				return false;
	 }
    }
	/**
	 * Teste dual - Verifica se a situa��o vai gerar depedencia dual no caminho. 
	 * Se sim a situa��o n�o pode ser inserida.
	 */
	if(this.checaDepend�nciaDualdeChoice(v)) {
		//System.out.println("Depend�ncia dual Detectada");
		return false;
	}

	/* faz uma copia  dos vertices pois a lista original sera manipulada durante a verifica��o */
	
	if(v.isChoice()==false) {
		if(poss�vel=insereSitua��oDepend�ncia(v)) {
			vaux = new ArrayList<Situa��o>(vertices);
	 	  for (Situa��o s : vaux) {
			 if(s.isChoice()) {
				 poss�vel= testaSitua��oEscolha(s);
			 }
		  } 
		  if (poss�vel==false) {
				this.removeSitua��o(v);
				
			}
		  return poss�vel;
	   }
	   return false;	
		
	}
	
	else { /* testa a inser��o de uma dependencia esq-dir */
	     	
	    Situa��o vx = new Situa��o(v.getEsq(),v.getDir(), Operador.Dep);
		if(poss�vel=insereSitua��oDepend�ncia(vx)) {
			  vaux = new ArrayList<Situa��o>(vertices);
		 	  for (Situa��o s : vaux) {
				 if(s.isChoice()) {
					 poss�vel= testaSitua��oEscolha(s);
				 }
			  } 
		 	 this.removeSitua��o(vx);
	          if (poss�vel) {
	        	  vx = new Situa��o(v.getDir(),v.getEsq(), Operador.Dep);
	        	  if(insereSitua��oDepend�ncia(vx)) {
	        	    for (Situa��o s : vaux) {
	 			     	 if(s.isChoice()) {
	 					   poss�vel= testaSitua��oEscolha(s);
	 				     }
	 			     } 
	              }
	          }
	          this.removeSitua��o(vx);
	          if(poss�vel){ 	  
		         return insereSitua��oEscolha(v);
	          }
	          return poss�vel;
	     }
		 return poss�vel;
   }
}
/**
 * verifica se os par�metros de uma situa��o choice n�o s�o ambas operadores dependidos de um mesmo operador. e Vice-versa.
 * ex: b dep a, b dep c, a xor c.
 * solu��o: ao inserir uma sit. z xor w,  para todo operador x fa�a Verifico em Gdep se existe o edge (x --> z) e (x --> w) 
 * @param v
 * @return boolean
 */

 
public boolean checaDepend�nciaDualdeChoice(Situa��o v) {
	boolean depDual=false;
	if (v.isChoice()) { /* verifico a situa��o dual quando v � um choice. Neste caso testo o choice a ser 
	 inserido com as depend�ncias existentes */
	   for (String s : this.gdep.getVertices()) {
		  if(depDual = ( (this.gdep.findEdge(s, v.getEsq())!=null) && (this.gdep.findEdge(s, v.getDir())!=null) ))
			break;
	   }
	   return depDual;
	}
	else { /* verifico a condi��o dual quando v � uma depend�ncia. neste caso testo a condi��o com cada choice j� inserido*/
		boolean inseriu = this.insereSitua��oDepend�ncia(v); /* esta depend�ncia j� pode ter sido inserida */
	   for (Situa��o s1 : this.getVertices()) {
		 if(s1.isChoice()) {
			 if(depDual = ( (this.gdep.findEdge(v.getEsq(),s1.getEsq())!=null) && (this.gdep.findEdge(v.getEsq(),s1.getDir())!=null) ))
				break;
		     }
	 	 }
	     if (inseriu) {
	    	  this.removeSitua��o(v);
	     }	  
		 return depDual; 
	} 
		
}
/**
 * ChecarChoicesTiposDiferenteMesmoOPerador - 
 * Verificar se a situa��o � um choice (xor, uni) de um tipo com operandos j� usados em um 
 * choice de outro tipo.
 * Solu��o:
 */
  public boolean checaSitua��oPromiscua(Situa��o s) {
	  boolean eesq=false,edir=false;
	  Operador opaux1=null, opaux2=null;
	  for (Situa��o v: this.getVertices()) {
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
	for (Situa��o v: this.getVertices()) {
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
     
     JFrame frame = new JFrame("Grafo de Depend�ncia do Caminho");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.getContentPane().add(vv);
     frame.pack();
     frame.setVisible(true);   	    
	
}
public boolean isEmpty() {
	return vertices.isEmpty();
}
public void addAll(Set<Situa��o> setSitua��o) {
	for (Situa��o s: setSitua��o) {
		this.insereSitua��o(s);
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
	
	Situa��o s1,s2,s3,s4,s5,s6,s7,s8;
	s1 = new Situa��o("b", "c", Operador.Xor);
	s2 = new Situa��o("a", "c", Operador.Xor);
	s3 = new Situa��o("a", "b", Operador.Uni);  		
	s4 = new Situa��o("a", "c", Operador.Dep);
	s5 = new Situa��o("b", "c", Operador.Dep);
	s6 = new Situa��o("c", "a", Operador.Dep);
	ret=c.insereSitua��o(s1);
	if(ret) {
		System.out.println("Situa��o Inserida s1");
	}
	else {
		System.out.println("Depend�ncia detectada s1");
			
	}
	
	ret=c.insereSitua��o(s2);
	if(ret) {
		System.out.println("Situa��o Inserida s2");
	}
	else {
		System.out.println("Depend�ncia detectada s2");
			
	}
	
	ret=c.insereSitua��o(s3);
	if(ret) {
		System.out.println("Situa��o Inserida s3");
	}
	else {
		System.out.println("Depend�ncia detectada s3");
			
	}
	
		
	ret=c.insereSitua��o(s4);
	if(ret) {
		System.out.println("Situa��o Inserida s4");
	}
	else {
		System.out.println("Depend�ncia detectada s4");
			
	}

	ret=c.insereSitua��o(s5);
	//ret=c.insereSitua��oDepend�ncia(s5);
	if(ret) {
		System.out.println("Situa��o Inserida s5");
	}
	else {
		System.out.println("Depend�ncia detectada s5");
			
	}
	
	ret=c.insereSitua��o(s6);
	if(ret) {
		System.out.println("Situa��o Inserida s6");
	}
	else {
		System.out.println("Depend�ncia detectada s6");
			
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
	
	Situa��o s1,s2,s3,s4,s5,s6,s7,s8;
	s1 = new Situa��o("b", "c", Operador.Xor);
	s2 = new Situa��o("a", "b", Operador.Uni);
	s3 = new Situa��o("a", "c", Operador.Dep);
	s4 = new Situa��o("c", "b", Operador.Uni);  		
	
	ret=c.insereSitua��o(s1);
	if(ret) {
		System.out.println("Situa��o Inserida s1");
	}
	else {
		System.out.println("Depend�ncia detectada s1");
			
	}
	
	ret=c.insereSitua��o(s2);
	if(ret) {
		System.out.println("Situa��o Inserida s2");
	}
	else {
		System.out.println("Depend�ncia detectada s2");
			
	}
	
	ret=c.insereSitua��o(s3);
	if(ret) {
		System.out.println("Situa��o Inserida s3");
	}
	else {
		System.out.println("Depend�ncia detectada s3");
			
	}
	
		
	ret=c.insereSitua��o(s4);
	if(ret) {
		System.out.println("Situa��o Inserida s4");
	}
	else {
		System.out.println("Depend�ncia detectada s4");
			
	}

		c.mostraGrafo(c.getGdep());
}
public static void testeCaminho() {
	Caminho c = new Caminho();
	String[] domain = {"a","b","c","d","e","f","g"};
	
	c.dCaminho = new ProcessDomain(domain);
	boolean ret;
	Situa��o s1,s2,s3,s4,s5,s6,s7,s8;

	/*
	//iguais ok
	s1 = new Situa��o("a", "b", Operador.Dep);
	s2 = new Situa��o("a", "b", Operador.Dep);
*/

	/*
	//iguais ok
	s1 = new Situa��o("a", "b", Operador.Xor);
	s2 = new Situa��o("a", "b", Operador.Xor);
*/
	
	
/*
	//invertida ok
	s1 = new Situa��o("b", "a", Operador.Dep);
	s2 = new Situa��o("a", "b", Operador.Dep);
*/
	
/*
	// [a dep X] ... [X dep b]
	s1 = new Situa��o("b", "a", Operador.Dep);
	s2 = new Situa��o("a", "c", Operador.Dep);
	s3 = new Situa��o("c", "d", Operador.Dep);  
	s4 = new Situa��o("d", "b", Operador.Dep);
*/
	
	// [a dep X] ... [X dep b] longa
	s1 = new Situa��o("b", "a", Operador.Dep);
	s2 = new Situa��o("a", "c", Operador.Dep);
	s3 = new Situa��o("c", "d", Operador.Dep);  
	s4 = new Situa��o("d", "e", Operador.Dep);
	s5 = new Situa��o("e", "f", Operador.Dep);
	s6 = new Situa��o("f", "g", Operador.Dep);
	s7 = new Situa��o("g", "b", Operador.Dep);
	



	
	
//	s1 = new Situa��o("b", "a", Operador.Dep);
//	s2 = new Situa��o("b", "c", Operador.Dep);
//	s3 = new Situa��o("a", "c", Operador.Dep); // esta depend�ncia foi removida pelo algoritmo 
												// na inser��o de. Preciso testar se a depend�ncia
	//j� existe no grafo para que ela n�o seja removida nos testes usados para inser��o de choices. 
		
//	s4 = new Situa��o("a", "c", Operador.Dep);
//	s4 = new Situa��o("d", "e", Operador.Dep);
	//s5 = new Situa��o("a", "g", Operador.Xor);
//s5 = new Situa��o("d", "a", Operador.Dep);
//	s6 = new Situa��o("b", "g", Operador.Xor);
	//s7 = new Situa��o("c", "g", Operador.Uni);
//	s7 = new Situa��o("a", "b", Operador.Uni);
/* erro edge j� existente ao tentar inserir depend�ncia igual a uma existente - corrigido */
 	s8 = new Situa��o("g", "d", Operador.Dep);
 	

	ret=c.insereSitua��o(s1);
	if(ret) {
		System.out.println("Situa��o Inserida s1");
	}
	else {
		System.out.println("Depend�ncia detectada s1");
			
	}
	
	ret=c.insereSitua��o(s2);
	if(ret) {
		System.out.println("Situa��o Inserida s2");
	}
	else {
		System.out.println("Depend�ncia detectada s2");
			
	}
	
	ret=c.insereSitua��o(s3);
	if(ret) {
		System.out.println("Situa��o Inserida s3");
	}
	else {
		System.out.println("Depend�ncia detectada s3");
			
	}
	
		
	ret=c.insereSitua��o(s4);
	if(ret) {
		System.out.println("Situa��o Inserida s4");
	}
	else {
		System.out.println("Depend�ncia detectada s4");
			
	}

	ret=c.insereSitua��o(s5);
	//ret=c.insereSitua��oDepend�ncia(s5);
	if(ret) {
		System.out.println("Situa��o Inserida s5");
	}
	else {
		System.out.println("Depend�ncia detectada s5");
			
	}
	
	ret=c.insereSitua��o(s6);
	if(ret) {
		System.out.println("Situa��o Inserida s6");
	}
	else {
		System.out.println("Depend�ncia detectada s6");
			
	}
	ret=c.insereSitua��o(s7);
	if(ret) {
		System.out.println("Situa��o Inserida s7");
	}
	else {
		System.out.println("Depend�ncia detectada s7");
			
	}
/* erro ao inserir depend�ncia igual
 * 	
corrigido */
	ret=c.insereSitua��o(s8);
	if(ret) {
		System.out.println("Situa��o Inserida s8");
	}
	else {
		System.out.println("Depend�ncia detectada s8");
			
	}
 
	c.mostraGrafo(c.getGdep());
	
	c.imprimeCaminho();
}


public void InserirSituacaoNoCaminho(ArrayList<Situa��o> listSituation, Caminho c) {
	for (int i = 0; i<listSituation.size(); i++) {
		if (c.insereSitua��o(listSituation.get(i))) {
			System.out.println("Situa��o Inserida [" + listSituation.get(i).esq + "-" + listSituation.get(i).op + "-" + listSituation.get(i).dir + "]");
		} else {
			System.out.println("Depend�ncia detectada " + listSituation.get(i).esq + "-" + listSituation.get(i).op + "-" + listSituation.get(i).dir + "]");
		}
	}
	//return c;

}

public static void testeCaminhoX() {
	Caminho c = new Caminho();

	String[] domain = {"a","b","c","d","e","f","g"};

	ArrayList<Situa��o> listSituation = new ArrayList<Situa��o>();
	
	c.dCaminho = new ProcessDomain(domain);

	
	
	// [a dep X] ... [X dep b] longa
	listSituation.add(new Situa��o("a", "b", Operador.Xor));
	listSituation.add(new Situa��o("b", "c", Operador.Xor));
	listSituation.add(new Situa��o("a", "c", Operador.Uni));
	listSituation.add(new Situa��o("b", "a", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();

	/*
	//iguais ok
	listSituation.add(new Situa��o("a", "b", Operador.Dep));
	listSituation.add(new Situa��o("a", "b", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
	*/
/*
	//invertida ok
	listSituation.add(new Situa��o("a", "b", Operador.Dep));
	listSituation.add(new Situa��o("b", "a", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/
/*
	// [a dep X] ... [X dep b] curta
	listSituation.add(new Situa��o("a", "b", Operador.Dep));
	listSituation.add(new Situa��o("a", "c", Operador.Dep));
	listSituation.add(new Situa��o("c", "d", Operador.Dep));
	listSituation.add(new Situa��o("d", "b", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/	

/*
	// [a dep X] ... [X dep b] longa
	listSituation.add(new Situa��o("b", "a", Operador.Dep));
	listSituation.add(new Situa��o("a", "c", Operador.Dep));
	listSituation.add(new Situa��o("c", "d", Operador.Dep));
	listSituation.add(new Situa��o("d", "e", Operador.Dep));
	listSituation.add(new Situa��o("e", "f", Operador.Dep));
	listSituation.add(new Situa��o("f", "g", Operador.Dep));
	listSituation.add(new Situa��o("g", "b", Operador.Dep));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/
/*	
	// [a dep X] ... [X xor b] longa
	listSituation.add(new Situa��o("b", "a", Operador.Dep));
	listSituation.add(new Situa��o("a", "c", Operador.Dep));
	listSituation.add(new Situa��o("c", "d", Operador.Dep));
	listSituation.add(new Situa��o("d", "e", Operador.Dep));
	listSituation.add(new Situa��o("e", "f", Operador.Dep));
	listSituation.add(new Situa��o("f", "g", Operador.Dep));
	listSituation.add(new Situa��o("g", "b", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

/*
	// [a Xor b] [a xor b] 
	listSituation.add(new Situa��o("a", "b", Operador.Xor));
	listSituation.add(new Situa��o("a", "b", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

/*
	// [a Xor b] [b xor a] 
	listSituation.add(new Situa��o("a", "b", Operador.Xor));
	listSituation.add(new Situa��o("b", "a", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

	/*
	// [a Xor b] [b xor a] 
	listSituation.add(new Situa��o("a", "b", Operador.Xor));
	listSituation.add(new Situa��o("b", "a", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/

/*
	// [a Xor X] ... [X xor b] longa
	listSituation.add(new Situa��o("b", "a", Operador.Xor));
	listSituation.add(new Situa��o("a", "c", Operador.Dep));
	listSituation.add(new Situa��o("c", "d", Operador.Dep));
	listSituation.add(new Situa��o("d", "e", Operador.Dep));
	listSituation.add(new Situa��o("e", "f", Operador.Dep));
	listSituation.add(new Situa��o("f", "g", Operador.Dep));
	listSituation.add(new Situa��o("g", "b", Operador.Xor));
	c.InserirSituacaoNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
 
*/
/*	
	//  
	listSituation.add(new Situa��o("a", "b", Operador.Xor));
	listSituation.add(new Situa��o("b", "c", Operador.Xor));
	listSituation.add(new Situa��o("b", "a", Operador.Xor)); // retirar a operacao contraria
	c.InserirSituacaroNoCaminho(listSituation, c);
	c.mostraGrafo(c.getGdep());
	c.imprimeCaminho();
*/	
}

public boolean localizaSitua��o(Situa��o s) {
	// TODO Auto-generated method stub
	   for (Situa��o v: this.getVertices()) {
		 if (v.�Equivalente(s))    
	        return true;
	   }
	   return  false;
}

public static void main(String[] args) {
	//Caminho.testeCaminho2();
	Caminho.testeCaminhoX();
}



}
