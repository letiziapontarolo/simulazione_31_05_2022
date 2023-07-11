package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private Graph<String, DefaultWeightedEdge> grafo;
	private NYCDao dao;
	private List<String> listaQuartieri;
	private List<Arco> archi;
	
	public Model() {
		
		dao = new NYCDao();
		
	}
	
	public List<String> listaProvider() {
		return this.dao.listaProvider();
	}
	
	public void creaGrafo(String provider) {
		
		listaQuartieri = new ArrayList<String>();
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		archi = new ArrayList<Arco>();
		
		this.dao.creaVertici(listaQuartieri, provider);
		Graphs.addAllVertices(this.grafo, listaQuartieri);
		
		this.dao.listaArchi(archi, provider);
		
		for (Arco a : archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getCity1(), a.getCity2(), a.getDistanza());
		}

	}
	
	public List<String> listaQuartieri() {
		return this.listaQuartieri;
	}
	
	
	public String quartieriAdiacenti(String quartiere) {
		
		String result = "";
		
		List<Arco> l = new ArrayList<Arco>();
		
		List<String> vicini = Graphs.neighborListOf(this.grafo, quartiere);
		
		for (String q1 : vicini) {
			DefaultWeightedEdge e = this.grafo.getEdge(q1, quartiere);
			Arco a = new Arco(quartiere, q1, this.grafo.getEdgeWeight(e));
			l.add(a);
		}
		
		Collections.sort(l, new Comparator<Arco>() {
			 @Override
			 public int compare(Arco o1, Arco o2) {
			 return (int) (o1.getDistanza() - o2.getDistanza());
			 }});
		
		for (Arco aaa : l) {
			if (aaa.getCity1().equals(quartiere)) {
				result = result + aaa.getCity2() + ", distanza: " + aaa.getDistanza() + "\n";
			}
			else {
				result = result + aaa.getCity1() + ", distanza: " + aaa.getDistanza() + "\n";
			}
		}
		
		return result;
		
		
		
	}
	
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
		}
	
		 public int numeroArchi() {
		return this.grafo.edgeSet().size();
		}
	
	
	
	

	
	
}
