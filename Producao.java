import java.util.ArrayList;

public class Producao {
	/*Classe que representa uma produ��o de regras*/
	private String variavel;
	private ArrayList<String> regras;
	
	public Producao(String variavel,ArrayList<String> regras ){
		this.variavel = variavel;
		this.regras = (ArrayList<String>) regras.clone();
	}
	
	public void adicionarRegra(String regra){
		/*Adiciona uma nova regra a uma produ��o*/
		if(regras.indexOf(regra) == -1){
			regras.add(regra);
		}
	}
	
	//Bloco de GETTERS
	public String getVariavel() {
		return this.variavel;
	}
	public ArrayList<String> getRegras() {
		return this.regras;
	}

	//Bloco de SETTERS
	public void setVariavel(String variavel) {
		this.variavel = variavel;
	}
	public void setRegras(ArrayList<String> regras) {
		this.regras = (ArrayList<String>) regras.clone();
	}

}