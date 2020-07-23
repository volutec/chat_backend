

public enum Operador {
			  Dep(1), Xor(2), Uni(3);
		   private final int value;

		   private Operador(int value) {
		      this.value = value;
		   }
		   public int getOperador() {
		      return value;
		   }
		}


