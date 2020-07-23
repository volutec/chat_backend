/**
 * 
 */

/**
 * @author ubuntu_ia
 *
 */
import java.util.ArrayList;

public class Dominio {
	
	private ArrayList dominio;

	public Dominio() {
		dominio = new ArrayList<String>();
	}
	/**
	 * Adiciona objetos de fluxo ao vetor
	 * @param obj
	 */
	public void addObjFluxo(String obj) {
		try {
		dominio.add(obj);
		} catch (Exception e) {
			System.out.println("Nao foi possivel adicionar objeto de fluxo "+ obj +" ao dominio.");
		}
		
	}

	/**
	 * Retorna o dominio - lista de objetos de fluxo
	 * @param 
	 */
	public ArrayList getDominio() {
		try {
		return dominio;
		} catch (Exception e) {
			System.out.println("Dominio.getDominio(): Nao foi possivel devolver o dominio.");
			return null;
		}
		
	}

}
