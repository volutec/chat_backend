import java.util.ArrayList;

public class Combinacao {

	private ArrayList<String> combinacao = new ArrayList<String>();
	private ArrayList<String> tipoRestricao = new ArrayList<String>();
		
	public void setObjetoDeFluxo(ArrayList<String> vetObjetoDeFluxo) {

		//Faz todas as combinacoes possiveis
		int indiceCombinacao = 0;
		// Carrega os tipos de restricao
		tipoRestricao.clear();
		tipoRestricao.add("dep");
		tipoRestricao.add("xor");


		for(int i=0; i<vetObjetoDeFluxo.size(); i++){
			for(int j=0; j<vetObjetoDeFluxo.size(); j++){
				indiceCombinacao++;
				if (vetObjetoDeFluxo.get(i) != vetObjetoDeFluxo.get(j)) {
					System.out.println("Combinação " + indiceCombinacao + ": " + vetObjetoDeFluxo.get(i) + " - " + vetObjetoDeFluxo.get(j));
					// gerar os vertices
					for(int r=0; r<tipoRestricao.size(); r++){
						vertice.add(new Vertice (vetObjetoDeFluxo.get(i), tipoRestricao.get(r), vetObjetoDeFluxo.get(j)));
					}

				}

			}

		}
		// Listar todos os vertices gerados
		for (int i=0; i<vertice.size(); i++){
			System.out.println("Vertice : " + vertice.get(i).getVertice());
		}


	}

}