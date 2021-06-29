package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private PremierLeagueDAO dao;
	private Graph<Match,DefaultWeightedEdge> grafo;
	private Map<Integer,Match> idMap;
	private List<Adiacenza> listaArchi;
	List<Match> percorso;
	
	public Model() {
		this.dao=new PremierLeagueDAO();
		idMap=new HashMap<Integer,Match>();
		percorso=new ArrayList<Match>();
	}
	

	public void creaGrafo(int mese, int minuti) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		dao.listAllMatches(idMap);
		
		Graphs.addAllVertices(this.grafo, dao.getVertici(mese, idMap));
		listaArchi=dao.getArchi(idMap, mese);
		for(Adiacenza aa:listaArchi) {
			Graphs.addEdgeWithVertices(this.grafo,aa.getMatch1(), aa.getMatch2(), aa.getPeso());
		}
		
		System.out.println(this.grafo.vertexSet().size());
		System.out.println(this.grafo.edgeSet().size());
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Adiacenza> getMigliore(){
		//Collections.sort(listaArchi);
		List<Adiacenza> lista=new ArrayList<Adiacenza>();
		double peso=0.0;
		for(Adiacenza aa:listaArchi) {
			if(aa.getPeso()>peso)
				peso=aa.getPeso();
		}
		
		for(Adiacenza aa:listaArchi)
			if(aa.getPeso()==peso)
				lista.add(aa);
		return lista ;
	}
	public Set<Match> getVertici(){
		return this.grafo.vertexSet();
	}
	
	public List<Match> getPercorso(Match partenza, Match arrivo) {
		List<Match> parziale=new ArrayList<Match>();
		parziale.add(partenza);
		cerca(parziale,0,arrivo,0.0);
		
		return percorso;
	}


	private void cerca(List<Match> parziale, int livello, Match arrivo, double peso) {
		
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
		    //Calcoliamo il peso e vediamo la soluzione migliore
			double pesoNuovo=this.calcolaPeso(parziale);
			if(pesoNuovo>peso) {
				peso=pesoNuovo;
				percorso.clear();
				percorso.addAll(parziale);	
			}
			return;
		}else {
			for(Match vicino : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
				if(!parziale.contains(vicino)) {
					parziale.add(vicino);
					cerca(parziale,livello,arrivo,peso);
					parziale.remove(parziale.size()-1);
					
				}
			}
			
			
			
			
		}
		
	}


	private double calcolaPeso(List<Match> parziale) {
		double peso=0.0;
		for(int i=0; i<parziale.size()-1;i++) {
		  DefaultWeightedEdge e = this.grafo.getEdge(parziale.get(i), parziale.get(i+1));
		  peso=peso+this.grafo.getEdgeWeight(e);
		}
		return peso;
	}
	
	
	
}
