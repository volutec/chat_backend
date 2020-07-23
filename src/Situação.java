/**
 * Guarda as Situation do artigo do professor.
 * Elas são 
 * não coexistência -'Xor' 
 * independencia - 
 * dependencia estrita - 'Dep'
 * dependencia circunstancial
 * 
 */
public class Situação {
		String esq;
		String dir;
		Operador op;
		public String getEsq() {
			return esq;
		}
		public void setEsq(String esq) {
			this.esq = esq;
		}
		public String getDir() {
			return dir;
		}
		public void setDir(String dir) {
			this.dir = dir;
		}
		public Operador getOp() {
			return op;
		}
		public void setOp(Operador  op) {
			this.op = op;
		}
		public Situação(String esq, String dir, Operador op) {
			super();
			this.esq = esq;
			this.dir = dir;
			this.op = op;
		}
		public boolean isChoice() {
			return ((this.op==Operador.Uni) || (this.op==Operador.Xor));
		}
		public void imprime() {
			System.out.println("["+this.getEsq()+"-"+this.getOp()+"-"+this.getDir()+"]");
			// TODO Auto-generated method stub
			
		}
		public boolean éEquivalente(Situação s) {
			 if ((this.getDir()==s.getDir()) && (this.getEsq()==s.getEsq()) && (this.getOp()==s.getOp()))    
			        return true;
			 return false;
		}
		public boolean contemOperando(String operan) {
			return(this.getDir().equals(operan) || this.getEsq().equals(operan));
		}
	}


