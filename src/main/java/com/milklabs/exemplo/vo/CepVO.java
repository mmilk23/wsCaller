package com.milklabs.exemplo.vo;

public class CepVO {

	private String bairro;

	private String cep;
	private String cidade;
	private String complemento2;
	private String end;
	private String uf;
	
	@Override
	public String toString() {
		return new StringBuffer("CepVO cep: [").append(cep).append("]\n")
						.append("      cidade: [").append(cidade).append("]\n")
						.append("      complemento2: [").append(complemento2).append("]\n")
						.append("      end: [").append(end).append("]\n")
						.append("      uf: [").append(uf).append("]").toString();
		
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getComplemento2() {
		return complemento2;
	}

	public void setComplemento2(String complemento2) {
		this.complemento2 = complemento2;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
