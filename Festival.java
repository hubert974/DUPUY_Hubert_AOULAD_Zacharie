package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Festival {
	
	private String nom; 
	private int nbplaces;
	private List<Concert> concerts;
	public List<Scene> scenes;
	
	//Constructors
	public Festival(String nom, int nb) {
		this.nom = nom;
		this.nbplaces = nb;
	}
	
	public Festival(String nom, int nbplaces, List<Concert> concerts) {
		this.nom = nom;
		this.nbplaces = nbplaces;
		this.concerts = new ArrayList<Concert>();
		this.scenes = new ArrayList<Scene>();
	}
	
	//Assessors
	public String getNom() {
		return this.nom;
	}
	
	public int getNombrePlaces() {
		return this.nbplaces;
	}
	
	public List<Concert> getConcerts(){
		return this.concerts;
	}
	
	public List<Scene> getScenes(){
		return this.scenes;
	}
	
	//Methods
	public void ajouterfestivalbdd() throws SQLException {
		Connection connection = Connexion.getInstance();
		String sql = "INSERT INTO festival (nom, nbplaces) VALUES (?, ?)";
		PreparedStatement pStatement = connection.prepareStatement(sql);
		pStatement.setString(1, this.getNom());
		pStatement.setInt(2, this.getNombrePlaces());
		pStatement.execute();
	}
	
	public void addScene(Scene scene) {
		this.scenes.add(scene);
	}
	
	public boolean inviter_artiste(String nom, String jour, float prix) {
		Scanner scan = new Scanner(System.in);
		System.out.println("L'artiste " + nom +" souhaite-t-il participer au festival '" + this.getNom()
		+ "' le " + jour + " pour un prix de " + prix + " euros ? " +
		"(vous devez répondre par 'true' ou par 'false')");
		try {
			boolean reponse = scan.nextBoolean();
			
			scan.nextLine();
			if (reponse == true) {
			System.out.println("L'artiste accepte la demande.");
			}
			else if (reponse == false) {
			System.out.println("L'artiste refuse la demande.");
			}
			scan.close();
			return reponse;
		} catch (InputMismatchException error) {
			System.out.println("Veuillez recommencer.");
			return this.inviter_artiste(nom, jour, prix);
		}
	}
	
	public void addConcert(Concert concert) {
		this.concerts.add(concert);
	}
	
	public void annuler_participation(Artiste artistesup) throws SQLException {
		
		//On supprime dans la liste des concerts
		Iterator<Concert> i = concerts.iterator();
		while ( i.hasNext() ) {
		    Concert concert = i.next();
		    Artiste artiste = concert.getArtiste();
		    if (artiste == artistesup) {
		        // On supprime l'élément de la liste
		        i.remove();
		    }
		}
		
		//On supprime dans la base de données
		Connection connection = Connexion.getInstance();
		String sql1 = "DELETE FROM artiste WHERE id = ?";
		String sql2 = "DELETE FROM concert WHERE id_artiste = ?";
		PreparedStatement pStatement1 = connection.prepareStatement(sql1);
		PreparedStatement pStatement2 = connection.prepareStatement(sql2);
		pStatement1.setInt(1, artistesup.getId());
		pStatement2.setInt(2, artistesup.getId());
		pStatement1.execute();
		pStatement2.execute();
	}
	
	public void ajouter_phase_preparation_bdd(String jour, String heuredebut, String heurefin, int sceneid) throws SQLException {
		//a modifier::::::Le laps de temps sera d'autant plus important 
		//que la configuration de la scène sera modifiée.
		Connection connection = Connexion.getInstance();
		String sql = "INSERT INTO concert (nom,jour,heuredebut,heurefin,sceneid,id_artiste) VALUES (?,?,?,?,?,?)";
		PreparedStatement pStatement = connection.prepareStatement(sql);
		pStatement.setString(1, "phase de préparation");
		pStatement.setString(2, jour);
		pStatement.setString(3, heuredebut);
		pStatement.setString(4, heurefin);
		pStatement.setInt(5, sceneid);
		pStatement.setNull(6, java.sql.Types.INTEGER);
		pStatement.execute();
	}
	
	public static void main(String[] args) throws SQLException {
		
		JOptionPane jop_nomfesti = new JOptionPane();
		String nomfestival = jop_nomfesti.showInputDialog(null, "Veuillez entrer le nom de votre festival", "MyFesti",JOptionPane.QUESTION_MESSAGE);
		System.out.println(nomfestival);

		JOptionPane jop_nmbrefestivaliers = new JOptionPane();
		boolean test=false;
		do {
			try {
				int nb = Integer.parseInt(jop_nmbrefestivaliers.showInputDialog(null,"Veuillez entrer le nombre de festivaliers attendus", "MyFesti", JOptionPane.QUESTION_MESSAGE));
				System.out.println(nb);
				test=false;
				Festival f = new Festival(nomfestival, nb);//Creation du festival
				//f.ajouterfestivalbdd();
				
				JOptionPane jop_nomartiste = new JOptionPane();
				String nomartiste = jop_nomartiste.showInputDialog(null, "Veuillez entrer le nom d'un groupe/artiste que vous voulez inviter", "MyFesti",JOptionPane.QUESTION_MESSAGE);
				System.out.println(nomartiste);
				
				JOptionPane jop_nbpersonnes = new JOptionPane();
				
				int nbpersonnes = Integer.parseInt(jop_nbpersonnes.showInputDialog(null,"Veuillez entrer le nombre de personnes dans le groupe", "MyFesti", JOptionPane.QUESTION_MESSAGE));
				System.out.println(nbpersonnes);
				test=false;
				
				
				JOptionPane jop_dateartiste = new JOptionPane();
				String jour = jop_dateartiste.showInputDialog(null, "Veuillez entrer le jour où vous souhaitez ajouter cet artiste de la forme JJ/MM/AAAA", "MyFesti",JOptionPane.QUESTION_MESSAGE);
				System.out.println(jour);
				
				JOptionPane jop_somme = new JOptionPane();
				
				float somme = Float.parseFloat(jop_somme.showInputDialog(null,"Veuillez entrer la somme que vous voulez payer à ce groupe/artiste", "MyFesti", JOptionPane.QUESTION_MESSAGE));
				System.out.println(somme);
				test=false;
				
				JOptionPane jop_style= new JOptionPane();
				String style=jop_style.showInputDialog(null,"Quel est le style musical de ce groupe/artiste?", "MyFesti", JOptionPane.QUESTION_MESSAGE);
				System.out.println(style);
			
				if (f.inviter_artiste(nomartiste, jour, somme) == true) {
					
					Artiste a = new Artiste(nomartiste,nbpersonnes,somme,style);
					
					System.out.println("L'artiste a été ajouté avec succès aux artistes du festival.");
			
					//On trouve alors la scène relative au style de l'artiste
					Connection connection = Connexion.getInstance();
					PreparedStatement pStatement = connection.prepareStatement( "SELECT scene.id FROM scene WHERE style = ?" );
					pStatement.setString(1, a.getStyle());
					ResultSet result = pStatement.executeQuery();
					result.next();
					int idscenefin = result.getInt(1);
			
			
					//On regarde si la scène est disponible à une heure
			
					//On regarde si le matériel demandé par l'artiste est dispo à 
			
			
			
					System.out.println("L'artiste/le groupe se reproduira sur la scène " + idscenefin);
			
				}
		
		
				//si une certaine quantité de matériel nécessaire pour le concert est disponible à une heure précise,
				//on ajoute le concert un peu après cette heure (on laisse le délai du temps de préparation)
		
		
				//if (materiel bon)
				//création d'un concert
				//Concert c1 = new Concert(1,"Concert de Booba","vendredi","16h","18h",1);
				//ajouter le concert dans la liste
				//f.addConcert(c1);
				//ajouter le concert dans la base de données
				//try {
				//	c1.ajouter_concert_bdd();
				//} catch (SQLException e) {
				//	// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
		
				//retirer un artiste
				//try {
				//	f.annuler_participation(a);
				//} catch (SQLException e) {
				//	e.printStackTrace();
				//}
		
		
		
				//création d'un billet pour un festivalier
				//Billet b1 = new Billet(1,"lundi 30 mars", 1);
				//b1.afficher_informations(c1);
		
			} catch (NumberFormatException e) {
				JOptionPane message;
				message = new JOptionPane();
				message.showMessageDialog(null, "Veuillez entrer un nombre svp", "Erreur lors de la saisie",JOptionPane.INFORMATION_MESSAGE);
				test=true;
			}
		}while(test);

	}
}