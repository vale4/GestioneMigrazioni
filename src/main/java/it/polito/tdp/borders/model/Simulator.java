package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulator {
	
	//Modello -> Stato del sistema ad ogni passo
	private Graph<Country, DefaultEdge>grafo;
	
	//Tipi di evento? ->coda prioritaria
	private PriorityQueue<Evento>queue;
	
	//Parametri della simulazione
	private int N_MIGRANTI=1000;
	private Country partenza;
	
	//Valori in output
	private int T=-1;
	private Map<Country, Integer>stanziali;
	
	
	public void init(Country partenza, Graph<Country, DefaultEdge>grafo) {
		this.partenza=partenza;
		this.grafo=grafo;
		
		//impostazione dello stato iniziale
		this.T=1;
		stanziali=new HashMap<>();
		for(Country c : this.grafo.vertexSet()) {
			stanziali.put(c, 0);
		}
		//creo la coda
		this.queue=new PriorityQueue<Evento>();
		//inserisco il primo evento
		this.queue.add(new Evento(T, partenza, N_MIGRANTI));
	}
	
	public void run() {
		//Finchè la coda non si svuota, estraggo un evento per volta e lo eseguo
		Evento e;
		while((e=this.queue.poll())!=null) {
			this.T=e.getT();
			//ESEGUO L'EVENTO e
			int nPersone=e.getN();
			Country stato=e.getStato();
			//cerco i vicini di "stato"
			List<Country>vicini=Graphs.neighborListOf(this.grafo, stato);
			
			int migranti=(nPersone/2)/vicini.size();
			
			if(migranti>0) {
				//le persone si possono muovere in parti uguali
				for(Country confinante: vicini) {
					queue.add(new Evento(e.getT()+1, confinante, migranti));
				}
			}
			int stanziali=nPersone-migranti*vicini.size();
			this.stanziali.put(stato, this.stanziali.get(stato)+stanziali);
		}
	}
	
	public Map<Country, Integer>getStanziali(){
		return this.stanziali;
	}
	
	public Integer getT() {
		return this.T;
	}
	

}
